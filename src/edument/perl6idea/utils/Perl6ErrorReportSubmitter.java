package edument.perl6idea.utils;

import com.google.gson.Gson;
import com.intellij.errorreport.bean.ErrorBean;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.Consumer;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.awt.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Perl6ErrorReportSubmitter extends ErrorReportSubmitter {
    private static String URL = "https://commaide.com/api/error-reports";

    @NotNull
    @Override
    public String getReportActionText() {
        return "Report to Edument team";
    }

    @Override
    public SubmittedReportInfo submit(IdeaLoggingEvent[] events, Component parent) {
        return new SubmittedReportInfo(null, "0", SubmittedReportInfo.SubmissionStatus.FAILED);
    }

    public boolean submit(@NotNull IdeaLoggingEvent[] events,
                          @Nullable String additionalInfo,
                          @NotNull Component parentComponent,
                          @NotNull Consumer<SubmittedReportInfo> consumer) {
        try {
            postError(new Gson().toJson(createErrorBean(events[0], additionalInfo)));
            return true;
        }
        catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
            return false;
        }
    }

    private static ErrorBean createErrorBean(IdeaLoggingEvent event, String info) {
        ErrorBean errorBean = new ErrorBean(event.getThrowable(), info);
        errorBean.setMessage(event.getMessage());
        errorBean.setPluginName("Perl 6 Language Support");
        IdeaPluginDescriptor perl6plugin = PluginManager.getPlugin(PluginId.getId("edument.perl6-idea-plugin"));
        if (perl6plugin != null) // Should not happen
            errorBean.setPluginVersion(perl6plugin.getVersion());
        return errorBean;
    }

    private static void postError(String json) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        try {
            HttpClient httpClient = createTrustingClient();
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            Logger.getInstance(Perl6ErrorReportSubmitter.class)
                  .error("Could not send bug report: " + e.getMessage());
        }
    }

    private static HttpClient createTrustingClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder builder = HttpClientBuilder.create();

        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
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
