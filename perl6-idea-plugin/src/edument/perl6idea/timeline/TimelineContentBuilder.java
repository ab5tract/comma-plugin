package edument.perl6idea.timeline;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.runners.RunTab;
import com.intellij.execution.ui.*;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.notification.NotificationsManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.content.Content;
import edument.perl6idea.timeline.client.ClientEvent;
import edument.perl6idea.timeline.client.TimelineClient;
import edument.perl6idea.timeline.client.TimelineEventListener;
import edument.perl6idea.ui.CustomConsoleRunTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TimelineContentBuilder extends CustomConsoleRunTab {
    private final static String TIMELINE_CONTENT_ID = "TimelineContent";

    public TimelineContentBuilder(@NotNull ExecutionResult executionResult,
                                  @NotNull ExecutionEnvironment environment) {
        super(environment, executionResult, "Timeline");
    }

    @Override
    protected String getCustomTabText() {
        return "Timeline";
    }

    @Override
    protected void addCustomTab(Object clientObject) throws ExecutionException {
        if (!(clientObject instanceof TimelineClient))
            throw new ExecutionException("Expected TimelineClient, got " + clientObject.getClass() + " instead");
        TimelineClient client = (TimelineClient)clientObject;
        TimelineView timeline = new TimelineView();
        Content content = myUi.createContent(TIMELINE_CONTENT_ID, timeline, "Timeline", null, null);
        content.setPreferredFocusableComponent(timeline);
        content.setCloseable(false);
        myUi.addContent(content, 0, PlaceInGrid.center, false);

        client.connect(new TimelineEventListener() {
            @Override
            public void onEvent(ClientEvent e) {
                ApplicationManager.getApplication().invokeLater(() -> timeline.updateWith(e));
            }

            @Override
            public void onError(Throwable e) {
                timeline.endLiveUpdates();
                String message = e.getMessage();
                if (message == null)
                    message = "unknown problem";
                Notifications.Bus.notify(
                        new Notification("Timeline connection error", null,
                                         "Timeline connection error", null,
                                         "Could not get timeline data: " + message,
                                         NotificationType.ERROR, null));
            }
        });
        myExecutionResult.getProcessHandler().addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                timeline.endLiveUpdates();
            }
        });
    }
}
