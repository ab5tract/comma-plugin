package org.raku.editor.podPreview;

import com.intellij.openapi.diagnostic.Logger;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HtmlStringResourceHandler extends CefResourceHandlerAdapter {
    private static final Logger LOG = Logger.getInstance(HtmlStringResourceHandler.class);
    private final InputStream stream;

    public HtmlStringResourceHandler(String html) {
        stream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean processRequest(@NotNull CefRequest request, @NotNull CefCallback callback) {
        callback.Continue();
        return true;
    }

    @Override
    public void getResponseHeaders(@NotNull CefResponse response, IntRef responseLength, StringRef redirectUrl) {
        response.setMimeType("text/html");
        response.setStatus(200);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesRequested, IntRef bytesReadOut, CefCallback callback) {
        try {
            int availableSize = stream.available();
            if (availableSize > 0) {
                int bytesToRead = Math.min(bytesRequested, availableSize);
                bytesToRead = stream.read(dataOut, 0, bytesToRead);
                bytesReadOut.set(bytesToRead);
                return true;
            }
        }
        catch (IOException e) {
            LOG.error(e);
        }
        bytesReadOut.set(0);
        try {
            stream.close();
        }
        catch (IOException e) {
            LOG.error(e);
        }
        return false;
    }
}
