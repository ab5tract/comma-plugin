package edument.perl6idea.utils;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Perl6ModuleListFetcher {
    public static final String GITHUB_MIRROR1 = "http://ecosystem-api.p6c.org/projects.json";
    public static final String GITHUB_MIRROR2 = "http://ecosystem-api.p6c.org/projects1.json";
    public static final String CPAN_MIRROR1 = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/cpan.json";
    private static Pair<Set<String>, Instant> modulesList = null;

    public static Set<String> getModulesListAsync(Project project) {
        if (modulesList != null) {
            Instant past = Instant.now().minus(Duration.ofMinutes(30));
            if (!past.isAfter(modulesList.second))
                return modulesList.first;
        }

        CompletableFuture<Set<String>> f = new CompletableFuture<>();
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(new Task.Backgroundable(project, "Getting Perl 6 Modules List"){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                f.complete(getModulesList());
                indicator.setFraction(1.0);
                indicator.setText("finished");
            }
        }, new EmptyProgressIndicator());
        try {
            return f.get();
        }
        catch (InterruptedException | ExecutionException e) {
            return new HashSet<>();
        }
    }

    private static Set<String> getModulesList() {
        // Try first mirror
        String githubOutput = doRequest(GITHUB_MIRROR1);
        // Try second mirror
        if (githubOutput == null)
            githubOutput = doRequest(GITHUB_MIRROR2);

        String cpanOutput = doRequest(CPAN_MIRROR1);

        Set<String> names = new HashSet<>();
        populateModulesSet(githubOutput, names);
        populateModulesSet(cpanOutput, names);
        modulesList = new Pair<>(names, Instant.now());
        return names;
    }

    private static void populateModulesSet(String output, Set<String> names) {
        if (output == null) return;
        new JSONArray(output).forEach((o) -> {
            try {
                names.add(((JSONObject)(o)).getString("name"));
            }
            catch (JSONException ignored) {}
        });
    }

    private static String doRequest(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200) return null;
            if (response.getEntity() == null) return null;
            return EntityUtils.toString(response.getEntity());
        }
        catch (Exception e) {
            return null;
        }
    }
}
