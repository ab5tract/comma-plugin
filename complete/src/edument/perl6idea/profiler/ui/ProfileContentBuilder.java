package edument.perl6idea.profiler.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.ui.content.Content;
import edument.perl6idea.profiler.run.Perl6ProfileCommandLineState;
import edument.perl6idea.ui.CustomConsoleRunTab;
import org.jetbrains.annotations.NotNull;

public class ProfileContentBuilder extends CustomConsoleRunTab {
    public static final String PROFILER_CONTENT_ID = "ProfilingResultsContent";

    public ProfileContentBuilder(ExecutionResult result, ExecutionEnvironment env) {
        super(env, result, "Profiler");
    }

    @Override
    protected String getCustomTabText() {
        return "Profiling Results";
    }

    @Override
    protected void addCustomTab(Object uiUpdater) throws ExecutionException {
        if (!(uiUpdater instanceof Perl6ProfileCommandLineState))
            throw new ExecutionException("Expected Profiler state, got " + uiUpdater.getClass() + " instead");
        ProfilerView profilerView = new ProfilerView(myProject);
        Content content = myUi.createContent(PROFILER_CONTENT_ID, profilerView, getCustomTabText(),
                                             null, null);
        content.setPreferredFocusableComponent(profilerView);
        content.setCloseable(false);
        myUi.addContent(content, 0, PlaceInGrid.center, false);
        myExecutionResult.getProcessHandler().addProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                profilerView.updateResultsFromFile(((Perl6ProfileCommandLineState)uiUpdater).getProfileResultsFile());
            }
        });
    }
}
