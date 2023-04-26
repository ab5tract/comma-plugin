package edument.perl6idea.profiler.compare;

import com.intellij.openapi.diagnostic.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class ProfileCompareDataProvider {
    private final static Logger LOG = Logger.getInstance(ProfileCompareDataProvider.class);
    protected CompletableFuture<List<ProfileCompareProcessor.ProfileCompareRow>> task;

    private CompletableFuture<List<ProfileCompareProcessor.ProfileCompareRow>> getTask() {
        if (task != null)
            return task;
        return task = CompletableFuture.supplyAsync(() -> {
            try {
                LOG.info("Loading rows (" + getClass().getName() + ")");
                List<ProfileCompareProcessor.ProfileCompareRow> rows = getRows();
                LOG.info("Rows loaded (" + getClass().getName() + ")");
                return rows;
            }
            catch (Exception e) {
                LOG.error(e);
                return null;
            }
        });
    }

    public void onDataReady(Consumer<List<ProfileCompareProcessor.ProfileCompareRow>> cb) {
        getTask().thenAccept(cb);
    }

    protected abstract List<ProfileCompareProcessor.ProfileCompareRow> getRows() throws SQLException;
}
