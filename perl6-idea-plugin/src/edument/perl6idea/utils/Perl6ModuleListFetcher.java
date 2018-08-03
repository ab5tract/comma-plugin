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

public class Perl6ModuleListFetcher {
    public static final String GITHUB_MIRROR1 = "http://ecosystem-api.p6c.org/projects.json";
    public static final String GITHUB_MIRROR2 = "http://ecosystem-api.p6c.org/projects1.json";
    public static final String CPAN_MIRROR1   = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/cpan.json";
    private static Pair<JSONArray, Instant> modulesList = null;
    private static boolean isFirst = true;

    public static Set<String> getProvidesByModuleAsync(Project project, String dependency) {
        if (modulesList != null) {
            Instant past = Instant.now().minus(Duration.ofMinutes(30));
            if (!past.isAfter(modulesList.second))
                return getProvidesByModule(modulesList.first, dependency);
        }

        populateModulesAsync(project);
        return new HashSet<>();
    }

    private static Set<String> getProvidesByModule(JSONArray array, String name) {
        HashSet<String> provides = new HashSet<>();
        for (Object module : array) {
            JSONObject jsonModule = (JSONObject)module;
            if (!jsonModule.has("name")) continue;
            if (!jsonModule.get("name").equals(name)) continue;
            if (!jsonModule.has("provides")) continue;
            Object localProvides = jsonModule.get("provides");
            if (localProvides instanceof JSONObject)
                provides.addAll(((JSONObject)jsonModule.get("provides")).keySet());
            if (!jsonModule.has("depends")) continue;
            Object localDepends = jsonModule.get("depends");
            if (localDepends instanceof JSONArray) {
                for (Object depend : (JSONArray)localDepends) {
                    provides.addAll(getProvidesByModule(array, (String)depend));
                }
            }
        }
        return provides;
    }

    public static Set<String> getModulesNamesAsync(Project project) {
        if (modulesList != null) {
            Instant past = Instant.now().minus(Duration.ofMinutes(30));
            if (!past.isAfter(modulesList.second))
                return getNames(modulesList.first);
        }

        populateModulesAsync(project);
        return modulesList != null ? getNames(modulesList.first) : new HashSet<>();
    }

    private static Set<String> getNames(JSONArray modules) {
        Set<String> names = new HashSet<>();
        for (Object module : modules) {
            JSONObject json = (JSONObject)module;
            if (json.has("name"))
                names.add(json.getString("name"));
        }
        return names;
    }

    public static Set<String> getProvidesAsync(Project project) {
        if (modulesList != null) {
            Instant past = Instant.now().minus(Duration.ofMinutes(30));
            if (!past.isAfter(modulesList.second))
                return getProvides(modulesList.first);
        }

        populateModulesAsync(project);
        return modulesList != null ? getProvides(modulesList.first) : new HashSet<>();
    }

    private static Set<String> getProvides(JSONArray modules) {
        Set<String> names = new HashSet<>();
        for (Object module : modules) {
            JSONObject json = (JSONObject)module;
            if (json.has("provides"))
                names.addAll(json.getJSONObject("provides").keySet());
        }
        return names;
    }

    private static void populateModulesAsync(Project project) {
        if (isFirst) {
            isFirst = false;
            ProgressManager.getInstance()
                           .runProcessWithProgressAsynchronously(new Task.Backgroundable(project, "Getting Perl 6 Modules List") {
                               @Override
                               public void run(@NotNull ProgressIndicator indicator) {
                                   populateModules();
                                   indicator.setFraction(1.0);
                                   indicator.setText("finished");
                               }
                           }, new EmptyProgressIndicator());
        }
    }

    private static void populateModules() {
        // Try first mirror
        String githubOutput = doRequest(GITHUB_MIRROR1);
        // Try second mirror
        if (githubOutput == null)
            githubOutput = doRequest(GITHUB_MIRROR2);

        String cpanOutput = doRequest(CPAN_MIRROR1);

        JSONArray jsonArray = new JSONArray();
        populateArrayFromSource(githubOutput, jsonArray);
        populateArrayFromSource(cpanOutput, jsonArray);
        modulesList = new Pair<>(jsonArray, Instant.now());
    }

    private static void populateArrayFromSource(String output, JSONArray array) {
        if (output == null) return;
        try {
            new JSONArray(output).forEach((o) -> array.put(o));
        } catch (JSONException ignored) {}
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
