package edument.perl6idea.editor.podPreview;

import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.jetbrains.annotations.NotNull;

public class NoContentResourceHandler extends CefResourceHandlerAdapter {
    @Override
    public boolean processRequest(@NotNull CefRequest request, @NotNull CefCallback callback) {
        callback.Continue();
        return true;
    }

    @Override
    public void getResponseHeaders(@NotNull CefResponse response, IntRef responseLength, StringRef redirectUrl) {
        response.setStatus(204);
    }

    @Override
    public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
        bytesRead.set(0);
        return false;
    }
}
