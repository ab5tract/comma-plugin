package edument.perl6idea.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Perl6ExternalModulesStorage {
    private static final String ECOSYSTEM_URL_1 = "http://ecosystem-api.p6c.org/projects.json";
    private List<String> ecosystemCache;
    private static boolean isInitialized = false;
    private static final Logger LOGGER = Logger.getInstance(Perl6ExternalModulesStorage.class);

    public List<String> getExternalModules() {
        if (ecosystemCache != null) {

            return ecosystemCache;
        }

        if (!isInitialized) {
            isInitialized = true;
            ProgressManager.getInstance().run(new Task.Backgroundable(null, "Gathering Module Info") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    gatherEcosystemModules(ECOSYSTEM_URL_1);
                }
            });
        }

        return null;
    }

    private void gatherEcosystemModules(String url) {
        ArrayList<String> providedModules = new ArrayList<>();
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse resp = HttpClientBuilder.create().build().execute(httpGet);
            HttpEntity entity = resp.getEntity();
            if (entity == null) return;
            InputStream content = entity.getContent();
            String result;
            try {
                // A fast way to consume InputString into String
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = content.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                result = outputStream.toString(StandardCharsets.UTF_8.name());
                outputStream.close();
            } finally {
                content.close();
            }
            JSONArray json = new JSONArray(result);
            JSONObject dist;

            for (Object module : json) {
                dist = (JSONObject)module;
                if (dist.has("provides")) {
                    JSONObject provides = (JSONObject)dist.get("provides");
                    if (provides != null)
                         providedModules.addAll(provides.keySet());
                }
            }
        } catch (IOException e) {
            LOGGER.info(e);
        }
        ecosystemCache = providedModules;
    }
}
