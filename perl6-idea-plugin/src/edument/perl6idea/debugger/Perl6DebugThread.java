package edument.perl6idea.debugger;

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.actions.StopProcessAction;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import edument.perl6idea.debugger.event.Perl6DebugEventBreakpointReached;
import edument.perl6idea.debugger.event.Perl6DebugEventBreakpointSet;
import edument.perl6idea.debugger.event.Perl6DebugEventStop;
import edument.perl6idea.run.Perl6DebuggableConfiguration;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;
import org.edument.moarvm.DebugEvent;
import org.edument.moarvm.EventType;
import org.edument.moarvm.RemoteInstance;
import org.edument.moarvm.types.ExecutionStack;
import org.edument.moarvm.types.Lexical.*;
import org.edument.moarvm.types.MoarThread;
import org.edument.moarvm.types.StackFrame;
import org.edument.moarvm.types.event.BreakpointNotification;
import org.jetbrains.annotations.NotNull;
import org.msgpack.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Perl6DebugThread extends Thread {
    private static final Logger LOG = Logger.getInstance(Perl6DebugThread.class);
    private final XDebugSession mySession;
    private final ExecutionResult myExecutionResult;
    private RemoteInstance client;
    private static final Executor myExecutor = Executors.newSingleThreadExecutor();
    private final List<BreakpointActionRequest> breakpointQueue = new CopyOnWriteArrayList<>();
    private boolean ready = false;

    Perl6DebugThread(XDebugSession session, ExecutionResult result) {
        super("Perl6DebugThread");
        mySession = session;
        myExecutionResult = result;
    }

    @Override
    public void run() {
        try {
            Perl6DebuggableConfiguration runConfiguration = (Perl6DebuggableConfiguration) mySession.getRunProfile();
            client = RemoteInstance.connect(runConfiguration.getDebugPort()).get(2, TimeUnit.SECONDS);
            ready = true;
            sendBreakpoints();
            setEventHandler();
            if (!runConfiguration.isStartSuspended())
                client.resume().get(2, TimeUnit.SECONDS);
        } catch (CancellationException | InterruptedException | TimeoutException | ExecutionException e) {
            LOG.error(e);
        }
    }

    private void setEventHandler() {
        Subject<DebugEvent> events = client.getSubject();
        events.subscribeOn(Schedulers.newThread()).subscribe(event -> {
            if (event.getEventType() == EventType.BreakpointNotification) {
                Perl6DebugThread thread = this;
                Executors.newSingleThreadExecutor().execute(() -> {
                    BreakpointNotification bpn = (BreakpointNotification) event;
                    Perl6ThreadDescriptor[] threads;
                    try {
                        // Need to suspend all the threads at the breakpoint, so we can
                        // get the thread list.
                        client.suspend().get();
                        threads = getThreads();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    int activeThreadIndex = 0;
                    for (int i = 0; i < threads.length; i++) {
                        if (threads[i].getThreadId() == bpn.getThread()) {
                            activeThreadIndex = i;
                            break;
                        }
                    }
                    Perl6DebugEventBreakpointReached reachedEvent =
                            new Perl6DebugEventBreakpointReached(threads, activeThreadIndex,
                                    mySession, thread, bpn.getFile(), bpn.getLine());
                    myExecutor.execute(reachedEvent);
                });
            } else {
                System.out.println("Event + " + event.getClass().getName());
            }
        });
    }

    public void stopDebugThread() {
        StopProcessAction.stopProcess(myExecutionResult.getProcessHandler());
        ((ConsoleView)myExecutionResult.getExecutionConsole()).print("Disconnected\n", ConsoleViewContentType.SYSTEM_OUTPUT);
    }

    public void resumeExecution() {
        try {
            client.resume().get();
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            LOG.error(e);
        }
    }

    public void pauseExecution() {
        try {
            client.suspend().get();
            Perl6ThreadDescriptor[] threads = getThreads();
            Perl6DebugEventStop stopEvent =
                    new Perl6DebugEventStop(threads, 1, mySession, this);
            myExecutor.execute(stopEvent);
        } catch (CancellationException | InterruptedException | ExecutionException e) {
            LOG.error(e);
        }
    }

    private Perl6ThreadDescriptor[] getThreads() throws ExecutionException, InterruptedException {
        List<MoarThread> threads = client.threadList().get();
        List<Perl6ThreadDescriptor> threadDescriptors = new ArrayList<Perl6ThreadDescriptor>(threads.size());
        for (MoarThread thread : threads) {
            try {
                ExecutionStack stack = client.threadStackTrace(thread.threadId).get();
                threadDescriptors.add(new Perl6ThreadDescriptor(thread.threadId, thread.nativeId,
                        stackToFrames(thread, stack)));
            }
            catch (ExecutionException e) {
                // Some threads we can not get (e.g. the debug or spesh threads). Ignore.
            }
        }
        return threadDescriptors.toArray(new Perl6ThreadDescriptor[0]);
    }

    private Perl6StackFrameDescriptor[] stackToFrames(MoarThread thread, ExecutionStack stack) {
        List<StackFrame> frames = stack.getFrames();
        Perl6StackFrameDescriptor[] result = new Perl6StackFrameDescriptor[frames.size()];
        for (int i = 0; i < frames.size(); i++) {
            StackFrame frame = frames.get(i);
            Perl6LoadedFileDescriptor fileDescriptor = new Perl6LoadedFileDescriptor(frame.getFile(), frame.getName());
            result[i] = new Perl6StackFrameDescriptor(fileDescriptor, frame);
            int finalI = i;
            client.contextHandle(thread.threadId, i)
                    .thenApply(v -> client.contextLexicals(v).thenApply(lex -> {
                        result[finalI].setLexicals(convertLexicals(lex));
                        client.releaseHandle(new int[]{v});
                        return null;
                    }));
        }
        return result;
    }

    private static Perl6ValueDescriptor[] convertLexicals(Map<String, Lexical> lex) {
        Perl6ValueDescriptor[] result = new Perl6ValueDescriptor[lex.size()];
        AtomicInteger i = new AtomicInteger();
        lex.forEach((k, v) -> {
            result[i.get()] = convertDescriptor(k, v);
            i.getAndIncrement();
        });
        return result;
    }

    @NotNull
    private static Perl6ValueDescriptor convertDescriptor(String k, Lexical v) {
        Perl6ValueDescriptor descriptor;
        switch (v.getKind()) {
            case INT:
                descriptor = new Perl6NativeValueDescriptor(k, "int",
                        String.valueOf(((IntValue) v).getValue()));
                break;
            case NUM:
                descriptor = new Perl6NativeValueDescriptor(k, "num",
                        String.valueOf(((NumValue) v).getValue()));
                break;
            case STR:
                descriptor = new Perl6NativeValueDescriptor(k, "str",
                        String.valueOf(((StrValue) v).getValue()));
                break;
            default:
                ObjValue ov = (ObjValue)v;
                descriptor = new Perl6ObjectValueDescriptor(k, ov.getType(), ov.isConcrete(), ov.getHandle());
                break;
        }
        return descriptor;
    }

    private void sendBreakpoints() {
        if (ready) {
            try {
                for (BreakpointActionRequest request : breakpointQueue) {
                    if (!request.isRemove) {
                        /* IDEA counts line numbers from 0, even though they
                         * are counted starting at 1 in the UI.
                         * MoarVM's debugserver likewise starts at 1, so we
                         * have to add 1 here to get everything right.
                         */
                        int realLine = client.setBreakpoint(request.file, request.line + 1, true, false).get();
                        /* The breakpoint line number we've received back from
                         * the debugserver has to be translated back.
                         */
                        realLine -= 1;
                        Perl6DebugEventBreakpointSet setEvent =
                                new Perl6DebugEventBreakpointSet(request.file, realLine);
                        setEvent.setDebugSession(mySession);
                        setEvent.setDebugThread(this);
                        myExecutor.execute(setEvent);
                    } else {
                        client.clearBreakpoint(request.file, request.line + 1).get();
                    }
                }
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                LOG.error(e);
            }
            breakpointQueue.clear();
        }
    }

    public void queueBreakpoint(XLineBreakpoint breakpoint, boolean isRemove) {
        breakpointQueue.add(new BreakpointActionRequest(isRemove, breakpoint));
        sendBreakpoints();
    }

    public void addObjectChildren(int handle, XCompositeNode node) {
        objectMetadata(handle)
            .thenAccept(metadata -> {
                Value attrFeatures = metadata.get("attr_features");
                if (attrFeatures != null && attrFeatures.asBooleanValue().getBoolean()) {
                    addObjectAttributes(handle, node);
                    return;
                }
                Value posFeatures = metadata.get("pos_features");
                if (posFeatures != null && posFeatures.asBooleanValue().getBoolean()) {
                    addPositionalElements(handle, node);
                    return;
                }
                Value assFeatures = metadata.get("ass_features");
                if (assFeatures != null && assFeatures.asBooleanValue().getBoolean()) {
                    addAssociativeElements(handle, node);
                    return;
                }
                node.addChildren(XValueChildrenList.EMPTY, true);
            });
    }

    private void addObjectAttributes(int handle, XCompositeNode node) {
        client.getObjectAttributes(handle)
              .thenAccept(classAttrs -> {
                  XValueChildrenList children = new XValueChildrenList();
                  for (Map.Entry<String, Map<String,Lexical>> classEntry : classAttrs.entrySet()) {
                        Perl6ValueDescriptor[] descriptors = convertLexicals(classEntry.getValue());
                        String className = classEntry.getKey();
                        for (Perl6ValueDescriptor desc : descriptors) {
                            children.add(new Perl6XAttributeValue(desc, this, className));
                        }
                    }
                    node.addChildren(children, true);
              });
    }

    private void addPositionalElements(int handle, XCompositeNode node) {
        node.addChildren(XValueChildrenList.EMPTY, true);
        client.getObjectPositionals(handle)
              .thenAccept(positional -> {
                  XValueChildrenList children = new XValueChildrenList();
                  for (int i = 0; i < positional.size(); i++) {
                      children.add(new Perl6XAggregateValue(
                          convertDescriptor(Integer.toString(i), positional.get(i)),
                          this));
                  }
                  node.addChildren(children, true);
              });
    }

    private void addAssociativeElements(int handle, XCompositeNode node) {
        node.addChildren(XValueChildrenList.EMPTY, true);
        client.getObjectAssociatives(handle)
              .thenAccept(associative -> {
                  XValueChildrenList children = new XValueChildrenList();
                  for (Perl6ValueDescriptor desc : convertLexicals(associative))
                      children.add(new Perl6XAggregateValue(desc, this));
                  node.addChildren(children, true);
              });
    }

    private CompletableFuture<Map<String, Value>> objectMetadata(int handle) {
        return client.getObjectMetadata(handle)
            .thenApply(metadataMessage -> {
                Map<String, Value> metadata = new HashMap<>();
                for (Map.Entry<Value, Value> entry : metadataMessage.asMapValue().entrySet())
                    metadata.put(entry.getKey().asStringValue().asString(), entry.getValue());
                return metadata;
            });
    }

    static class BreakpointActionRequest {
        final boolean isRemove;
        final String file;
        final int line;

        BreakpointActionRequest(boolean isRemove, XLineBreakpoint bp) {
            this.isRemove = isRemove;
            this.file = bp.getFileUrl().substring(7);
            this.line = bp.getLine();
        }
    }
}
