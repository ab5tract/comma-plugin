package org.raku.comma.utils;

import com.google.gson.Gson;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.Consumer;
import com.intellij.util.ExceptionUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.awt.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@InternalIgnoreDependencyViolation
public class RakuErrorReportSubmitter extends ErrorReportSubmitter {
    private static final String URL = "https://commaide.com/api/error-reports";

    @NotNull
    @Override
    public String getReportActionText() {
        return "Report to Edument Team";
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events,
                          @Nullable String additionalInfo,
                          @NotNull Component parentComponent,
                          @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        try {
            postError(new Gson().toJson(createErrorBean(events[0], additionalInfo)));
            return true;
        }
        catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            return false;
        }
    }

    private static Map<String, String> createErrorBean(IdeaLoggingEvent event, String info) {
        Map<String, String> errorBean = new HashMap<>();
        errorBean.put("pluginName", "Raku Language Support");
        IdeaPluginDescriptor rakuPlugin = PluginManagerCore.getPlugin(PluginId.getId("org.raku-idea-plugin"));
        if (rakuPlugin != null)
            errorBean.put("pluginVersion", rakuPlugin.getVersion());
        errorBean.put("message", event.getMessage());
        errorBean.put("stackTrace", ExceptionUtil.getThrowableText(event.getThrowable()));
        errorBean.put("lastAction", info);
        return errorBean;
    }

    private static void postError(String json) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        try {
            HttpClient httpClient = createTrustingClient();
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            Logger.getInstance(RakuErrorReportSubmitter.class)
                  .error("Could not send bug report: " + e.getMessage());
        }
    }

    private static HttpClient createTrustingClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder builder = HttpClientBuilder.create();

        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] arg0, String arg1) {
                return true;
            }
        }).build();
        builder.setSSLContext(sslContext);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
          .register("http", PlainConnectionSocketFactory.getSocketFactory())
          .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
          .build();

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        builder.setConnectionManager(connMgr);
        return builder.build();
    }
}
