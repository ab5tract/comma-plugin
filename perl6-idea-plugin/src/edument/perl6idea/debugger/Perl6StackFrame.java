package edument.perl6idea.debugger;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class Perl6StackFrame extends XStackFrame {
    private final Perl6StackFrameDescriptor myFrameDescriptor;
    private final Perl6ExecutionStack myStack;
    private final Perl6DebugThread myDebugThread;
    private final NullableLazyValue<VirtualFile> myVirtualFile;

    public Perl6StackFrame(Perl6StackFrameDescriptor frame, Perl6ExecutionStack stack) {
        myFrameDescriptor = frame;
        myStack = stack;
        myDebugThread = stack.getSuspendContext().getDebugThread();
        myVirtualFile = NullableLazyValue.createValue(() -> {
            String localFilePath = myFrameDescriptor.getFile().getPath();
            return VfsUtil.findFileByIoFile(new File(localFilePath), true);
        });
    }

    @Override
    public void customizePresentation(@NotNull ColoredTextContainer component) {
        component.append(myFrameDescriptor.getPresentableName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        component.setIcon(AllIcons.Debugger.StackFrame);
    }

    @Nullable
    @Override
    public XSourcePosition getSourcePosition() {
        VirtualFile file = myVirtualFile.getValue();
        if (file != null)
            return XSourcePositionImpl.create(file, myFrameDescriptor.getLine() - 1);
        return null;
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        Perl6ValueDescriptor[] lexicals = myFrameDescriptor.getLexicals();
        XValueChildrenList list = new XValueChildrenList();

        if (lexicals != null && lexicals.length > 0) {
            list.addTopGroup(new Perl6XLexicalGroup("Lexical variables", lexicals, this));
            node.addChildren(list, true);
        } else {
            super.computeChildren(node);
        }
    }

    public Perl6DebugThread getDebugThread() {
        return myDebugThread;
    }
}
