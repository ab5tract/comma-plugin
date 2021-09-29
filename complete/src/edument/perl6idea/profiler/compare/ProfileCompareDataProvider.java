package edument.perl6idea.profiler.compare;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class ProfileCompareDataProvider {
    protected CompletableFuture<List<ProfileCompareProcessor.ProfileCompareRow>> task;

    public CompletableFuture<List<ProfileCompareProcessor.ProfileCompareRow>> getTask() {
        if (task != null)
            return task;
        return task = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Loading rows (" + getClass().getName() + ")");
                List<ProfileCompareProcessor.ProfileCompareRow> rows = getRows();
                System.out.println("Rows loaded (" + getClass().getName() + ")");
                return rows;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public void onDataReady(Consumer<List<ProfileCompareProcessor.ProfileCompareRow>> cb) {
        getTask().thenAccept(cb);
    }

    protected abstract List<ProfileCompareProcessor.ProfileCompareRow> getRows() throws SQLException;
}
