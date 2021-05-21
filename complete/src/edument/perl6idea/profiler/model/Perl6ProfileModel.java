package edument.perl6idea.profiler.model;

import com.intellij.openapi.project.Project;
import edument.perl6idea.profiler.ui.Perl6ProfilerFrameResultFilter;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perl6ProfileModel extends AbstractTableModel {
    protected final DecimalFormat myFormatter = new DecimalFormat("#,###");
    protected ArrayList<String> COLUMN_NAMES = new ArrayList<>(
        Arrays.asList("Name", "File", "Inclusive (μs)", "Entries")
    );
    protected final String myBaseProjectPath;
    protected int inclusiveSum;
    protected List<Perl6ProfileCall> nodes;
    protected boolean showRealFileNames = false;
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    @Override
    public int getRowCount() {
        return nodes.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    public Perl6ProfileModel(Project project, List<Perl6ProfileCall> routines) {
        myBaseProjectPath = project.getBasePath();
        nodes = routines;
        // Calculate inclusive time as sum of all inclusive times of calls in the table
        inclusiveSum = nodes.stream().mapToInt(p -> p.getInclusiveTime()).sum();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Perl6ProfileCall profilerNode = nodes.get(row);
        switch (column) {
            case 0:
                return profilerNode.getName();
            case 1:
                return showRealFileNames
                       ? profilerNode.getOriginalFile()
                       : profilerNode.getFilename(myBaseProjectPath);
            case 2:
                return profilerNode.getInclusiveTime();
            default:
                return profilerNode.getEntriesCount();
        }
    }

    protected String calculateInclusiveValue(int timeInMills) {
        String percents = DECIMAL_FORMAT.format(((double)timeInMills / inclusiveSum) * 100);
        return String.format("%s%% (%s μs)", percents,  myFormatter.format(timeInMills));
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
            case 1:
                return String.class;
            case 2:
            case 3:
            default:
                return Integer.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES.get(column);
    }

    public boolean isCellInternal(int row,
                                  List<String> projectModuleNames,
                                  List<String> moduleBasePaths,
                                  Perl6ProfilerFrameResultFilter filter) {
        // If Everything is shown, nothing is internal
        if (filter == Perl6ProfilerFrameResultFilter.Everything)
            return false;
        String file = nodes.get(row).getOriginalFile();
        if (file.startsWith("site#") && filter == Perl6ProfilerFrameResultFilter.NoCore)
            return false;
        String moduleName = nodes.get(row).getModuleName();
        if (moduleName != null) {
            for (String projectModuleName : projectModuleNames) {
                if (moduleName.equals(projectModuleName))
                    return false;
            }
        } else if (!file.contains(".precomp")) {
            for (String path : moduleBasePaths)
                if (file.startsWith(path))
                    return false;
        }
        return true;
    }

    public int getNodeId(int row) {
        return nodes.get(row).getRoutineID();
    }

    public String getNodeName(int row) {
        return nodes.get(row).getName();
    }

    public String getNodeSourceFile(int row) {
        String baseFilename = nodes.get(row).getOriginalFile();
        // it can contain module name, so strip it off
        String moduleName = nodes.get(row).getModuleName();

        if (moduleName != null && baseFilename.contains(moduleName)) {
            return baseFilename.substring(0, baseFilename.lastIndexOf(moduleName) - 1).trim();
        }
        return baseFilename;
    }

    public int getNodeSourceLine(int row) {
        return nodes.get(row).getLine();
    }

    public void setShowRealFileNames(boolean showRealFileNames) {
        this.showRealFileNames = showRealFileNames;
    }

    public int getNavigationIndexByCallId(int id) {
        for (int i = 0, size = nodes.size(); i < size; i++) {
            Perl6ProfileCall node = nodes.get(i);
            if (node.getRoutineID() == id)
                return i;
        }
        return -1;
    }

    public double getRatio(int value, int row, int column) {
        if (column == 2) {
            return value / (double)inclusiveSum;
        }
        return value;
    }
}
