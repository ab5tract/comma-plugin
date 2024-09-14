package org.raku.utils;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

import static kotlinx.collections.immutable.ExtensionsKt.persistentListOf;

@Service(Service.Level.PROJECT)
public final class RakuModuleListFetcher {
    public static final String GITHUB_MIRROR1 = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/p6c1.json";
    public static final String CPAN_MIRROR1 = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/cpan1.json";
    public static final String FEZ_MIRROR1 = "https://360.zef.pm/";
    public static final String REA_MIRROR1 = "https://raw.githubusercontent.com/Raku/REA/main/META.json";
    public static final List<String> PREINSTALLED_MODULES =
        persistentListOf( "CompUnit::Repository::Staging",
                                    "CompUnit::Repository::FileSystem",
                                    "CompUnit::Repository::Installation",
                                    "CompUnit::Repository::AbsolutePath",
                                    "CompUnit::Repository::Unknown",
                                    "CompUnit::Repository::NQP", "CompUnit::Repository::Raku",
                                    "CompUnit::Repository::RepositoryRegistry",
                                    "NativeCall", "NativeCall::Types", "NativeCall::Compiler::GNU",
                                    "NativeCall::Compiler::MSVC",
                                    "Test", "Pod::To::Text", "Telemetry");
    public static final List<String> PRAGMAS = persistentListOf( "v6.c", "v6.d", "MONKEY-GUTS", "MONKEY-SEE-NO-EVAL",
                                                                           "MONKEY-TYPING", "MONKEY", "experimental", "fatal",
                                                                           "internals", "invocant", "isms", "lib", "nqp", "newline",
                                                                           "parameters", "precompilation", "soft", "strict",
                                                                           "trace", "v6", "variables", "worries");
    private static Pair<Map<String, JSONObject>, Instant> modulesList = null;
    private static boolean isFirst = true;

    public static void refreshModules(Project project) {
        Instant now = Instant.now();
        if (modulesList == null || now.isAfter(modulesList.second)) {
            populateModules(project);
        }
    }

    @Nullable
    public static String getModuleByProvide(Project project, String text) {
        refreshModules(project);
        for (JSONObject module : modulesList.first.values()) {
            if (! module.has("provides")) continue;
            JSONObject provide = (JSONObject) module.get("provides");
            if (provide.keySet().contains(text)) return (String) module.get("name");
        }
        return null;
    }

    @NotNull
    public static Set<String> getProvidesByModule(Project project, String name, Set<String> seen) {
        refreshModules(project);
        HashSet<String> provides = new HashSet<>();
        if (seen.contains(name))
            return provides;
        else
            seen.add(name);
        JSONObject module = modulesList.first.get(name);
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
                        if (depend instanceof String) {
                            provides.addAll(getProvidesByModule(project, (String)depend, seen));
                        }
                        else if (depend instanceof JSONObject) {
                           // XXX Ignore JSON-based dependencies for now, is there a fixed format in Raku already?
                        }
                    }
                }
            }
        }
        return provides;
    }

    @NotNull
    public static Set<String> getNames(Project project) {
        refreshModules(project);
        Set<String> names = new HashSet<>();
        for (Object module : modulesList.first.values()) {
            JSONObject json = (JSONObject)module;
            if (json.has("name"))
                names.add(json.getString("name"));
        }
        return names;
    }

    @NotNull
    public static Set<String> getProvides(Project project) {
        refreshModules(project);
        Set<String> names = new HashSet<>();
        for (Object module : modulesList.first.values()) {
            JSONObject json = (JSONObject)module;
            if (json.has("provides"))
                names.addAll(json.getJSONObject("provides").keySet());
        }
        return names;
    }

    public static synchronized CompletableFuture<Boolean> populateModules(Project project) {
        if (isFirst) {
            isFirst = false;
            CompletableFuture<Boolean> isCacheLoaded = new CompletableFuture<>();
            ProgressManager.getInstance()
                .runProcessWithProgressAsynchronously(new Task.Backgroundable(project, "Raku modules metadata downloading") {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        populateModules();
                        isCacheLoaded.complete(true);
                        indicator.setFraction(1.0);
                        indicator.setText("Finished");
                    }

                    @Override
                    public void onThrowable(@NotNull Throwable error) {
                        modulesList = new Pair<>(new HashMap<>(), Instant.now().plus(3, ChronoUnit.MINUTES));
                    }

                    @Override
                    public void onCancel() {
                        modulesList = new Pair<>(new HashMap<>(), Instant.now().plus(3, ChronoUnit.MINUTES));
                    }
                }, new EmptyProgressIndicator());
            return isCacheLoaded;
        }
        else {
            return CompletableFuture.completedFuture(true);
        }
    }

    private static void populateModules() {
        JSONArray jsonArray = new JSONArray();
        for (String url : Arrays.asList(GITHUB_MIRROR1, CPAN_MIRROR1, FEZ_MIRROR1, REA_MIRROR1)) {
            String output = doRequest(url);
            if (output != null) {
                populateArrayFromSource(output, jsonArray);
            }
        }

        Map<String, JSONObject> modulesMap = new ConcurrentHashMap<>();
        for (Object json : jsonArray) {
            JSONObject jsonObject = (JSONObject)json;
            if (jsonObject.has("name") && jsonObject.has("version"))
                if (checkVersions(jsonObject, modulesMap))
                    modulesMap.put(jsonObject.getString("name"), jsonObject);
        }
        modulesList = new Pair<>(modulesMap, Instant.now().plus(30, ChronoUnit.MINUTES));
    }

    private static boolean checkVersions(JSONObject module, Map<String, JSONObject> modulesMap) {
        // Return immediately if the module is not yet added
        if (!modulesMap.containsKey(module.getString("name"))) return true;

        // Do not add if invalid, in any case
        if (!module.has("version")) return false;

        String versionBefore = modulesMap.get(module.getString("name")).getString("version");
        String versionNew = module.getString("version");

        String[] version1 = versionBefore.split("\\.");
        String[] version2 = versionNew.split("\\.");

        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < version1.length && i < version2.length && version1[i].equals(version2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < version1.length && i < version2.length) {
            try {
                return Integer.valueOf(version1[i]) < Integer.valueOf(version2[i]);
            }
            catch (Exception e) {
                return true;
            }
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return version1.length < version2.length;
    }

    private static void populateArrayFromSource(String output, JSONArray array) {
        try {
            new JSONArray(output).forEach((o) -> array.put(o));
        }
        catch (JSONException ignored) {
        }
    }

    @Nullable
    private static String doRequest(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
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
        catch (IOException e) {
            return null;
        }
    }

    public static boolean isReady() {
        return modulesList != null;
    }
}
