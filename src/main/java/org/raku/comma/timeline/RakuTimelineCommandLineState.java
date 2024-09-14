package org.raku.comma.timeline;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.raku.comma.run.RakuRunCommandLineState;
import org.raku.comma.run.RakuRunConfiguration;
import org.raku.comma.timeline.client.TimelineClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;

public class RakuTimelineCommandLineState extends RakuRunCommandLineState {
    private int port;

    private TimelineClient timelineClient;

    public RakuTimelineCommandLineState(ExecutionEnvironment environment) {
        super(environment);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        catch (IOException e) {
            throw new ExecutionException("Could not find a free port for timeline server: " + e.getMessage());
        }
        ProcessHandler handler = super.startProcess();
        timelineClient = new TimelineClient("127.0.0.1", port);
        return handler;
    }

    @Override
    protected void setEnvironment(GeneralCommandLine cmd) {
        super.setEnvironment(cmd);
        cmd.withEnvironment("LOG_TIMELINE_SERVER", "127.0.0.1:" + port);
        if (runConfiguration instanceof RakuRunConfiguration) {
            String allowedEvents = ((RakuRunConfiguration)runConfiguration).getLogTimelineEvents();
            if (!allowedEvents.isEmpty()) {
                cmd.withEnvironment("LOG_TIMELINE_RAKU_EVENTS", allowedEvents);
            }
        }
    }

    @Override
    protected void populateRunCommand() throws ExecutionException {
        command.add("-MLog::Timeline");
        super.populateRunCommand();
    }

    public TimelineClient getTimelineClient() {
        return timelineClient;
    }
}
