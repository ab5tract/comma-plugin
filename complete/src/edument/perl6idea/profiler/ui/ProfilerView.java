package edument.perl6idea.profiler.ui;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import edument.perl6idea.profiler.model.Perl6ProfileData;
import edument.perl6idea.profiler.run.Perl6ProfileTask;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProfilerView extends JPanel {
    private final Project myProject;
    private JComponent myView;

    public ProfilerView(Project project) {
        super();
        myProject = project;
        setLayout(new BorderLayout());
        myView = new JLabel("Waiting for the program to terminate to collect results...");
        add(myView, BorderLayout.CENTER);
    }

    public void updateResultsFromFile(@Nullable File file, boolean hasToRemoveTheFile) {
        if (file == null) {
            setView(new JLabel("Error during results connecting: SQL file was absent"));
            return;
        } else {
            if (hasToRemoveTheFile)
                setView(new JLabel("The program has terminated, calculating results to present..."));
            else
                setView(new JLabel("Importing the SQL, calculating results to present..."));
        }
        Task.Backgroundable task = new Perl6ProfileTask(myProject, "Processing Profiling Data", true, file, this, hasToRemoveTheFile);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

    public void updateResultsFromData(Perl6ProfileData data) {
        Task.Backgroundable task = new Perl6ProfileTask(myProject, "Processing Profiling Data", true, data, this);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

    public void setView(JComponent view) {
        remove(myView);
        myView = view;
        add(myView, BorderLayout.CENTER);
    }
}
