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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Perl6ModuleListFetcher {
    public static final String GITHUB_MIRROR1 = "http://ecosystem-api.p6c.org/projects.json";
    public static final String GITHUB_MIRROR2 = "http://ecosystem-api.p6c.org/projects1.json";
    public static final String CPAN_MIRROR1   = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/cpan.json";
    public static final List<String> PREINSTALLED_MODULES =
        Arrays.asList("CompUnit::Repository::Staging",
                      "CompUnit::Repository::FileSystem",
                      "CompUnit::Repository::Installation",
                      "CompUnit::Repository::AbsolutePath",
                      "CompUnit::Repository::Unknown",
                      "CompUnit::Repository::NQP", "CompUnit::Repository::Perl6",
                      "CompUnit::Repository::RepositoryRegistry",
                      "NativeCall", "NativeCall::Types", "NativeCall::Compiler::GNU",
                      "NativeCall::Compiler::MSVC",
                      "Test", "Pod::To::Text");
    public static final List<String> PRAGMAS =
        Arrays.asList("v6.c", "v6.d", "MONKEY-GUTS", "MONKEY-SEE-NO-EVAL",
                      "MONKEY-TYPING", "MONKEY", "experimental", "fatal",
                      "internals", "invocant", "isms", "lib", "nqp", "newline",
                      "parameters", "precompilation", "soft", "strict",
                      "trace", "v6", "variables", "worries");
    private static Pair<Map<String, JSONObject>, Instant> modulesList = null;
    private static boolean isFirst = true;

    public static String getModuleByProvideAsync(Project project, String text) {
        if (modulesList != null) {
            Instant past = Instant.now().minus(Duration.ofMinutes(30));
            if (!past.isAfter(modulesList.second))
                return getModuleByProvide(modulesList.first, text);
        }

        populateModulesAsync(project);
        return null;
    }

    private static String getModuleByProvide(Map<String, JSONObject> modules, String text) {
        for (Object module : modules.values()) {
            JSONObject jsonModule = (JSONObject)module;
            if (!jsonModule.has("provides")) continue;
            JSONObject provide = (JSONObject)jsonModule.get("provides");
            if (provide.keySet().contains(text))
                return (String)jsonModule.get("name");
        }
        return null;
    }

    public static Set<String> getProvidesByModuleAsync(Project project, String dependency) {
        if (modulesList != null) {
            Instant past = Instant.now().minus(Duration.ofMinutes(30));
            if (!past.isAfter(modulesList.second))
                return getProvidesByModule(modulesList.first, dependency);
        }

        populateModulesAsync(project);
        return null;
    }

    private static Set<String> getProvidesByModule(Map<String, JSONObject> modules, String name) {
        HashSet<String> provides = new HashSet<>();
        JSONObject module = modules.get(name);
        if (module != null) {
            if (module.has("provides")) {
                Object localProvides = module.get("provides");
                if (localProvides instanceof JSONObject)
                    provides.addAll(((JSONObject)module.get("provides")).keySet());
            }
            if (module.has("depends")) {
                Object localDepends = module.get("depends");
                if (localDepends instanceof JSONArray) {
                    for (Object depend : (JSONArray)localDepends) {
                        provides.addAll(getProvidesByModule(modules, (String)depend));
                    }
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

    private static Set<String> getNames(Map<String, JSONObject> modules) {
        Set<String> names = new HashSet<>();
        for (Object module : modules.values()) {
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

    private static Set<String> getProvides(Map<String, JSONObject> modules) {
        Set<String> names = new HashSet<>();
        for (Object module : modules.values()) {
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
        Map<String, JSONObject> modulesMap = new ConcurrentHashMap<>();
        for (Object json : jsonArray) {
            JSONObject jsonObject = (JSONObject)json;
            if (jsonObject.has("name"))
                modulesMap.put(jsonObject.getString("name"), jsonObject);
        }
        modulesList = new Pair<>(modulesMap, Instant.now());
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
