package edument.perl6idea.utils;

import com.intellij.errorreport.bean.ErrorBean;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.Consumer;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;

public class Perl6ErrorReportSubmitter extends ErrorReportSubmitter {
    //private static String URL = "http://commaide.com/api/error-reports/";
    private static String URL = "http://localhost:20000/error";

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
        ErrorBean bean = createErrorBean(events[0], additionalInfo);
        String json = new JSONObject(bean).toString();
        postError(json);
        return true;
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

    private static void postError(String json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(URL);
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        try {
            httpClient.execute(httpPost);
        } catch (IOException e) {
            Logger.getInstance(Perl6ErrorReportSubmitter.class)
                  .error("Could not send bug report: " + e.getMessage());
        }
    }
}
