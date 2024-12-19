package org.raku.comma.editor.podPreview

import com.intellij.openapi.diagnostic.Logger
import org.cef.callback.CefCallback
import org.cef.handler.CefResourceHandlerAdapter
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import kotlin.math.min

class HtmlStringResourceHandler(html: String) : CefResourceHandlerAdapter() {
    private val stream: InputStream = ByteArrayInputStream(html.toByteArray(StandardCharsets.UTF_8))

    override fun processRequest(request: CefRequest, callback: CefCallback): Boolean {
        callback.Continue()
        return true
    }

    override fun getResponseHeaders(response: CefResponse, responseLength: IntRef?, redirectUrl: StringRef?) {
        response.mimeType = "text/html"
        response.status = 200
    }

    override fun readResponse(
        dataOut: ByteArray,
        bytesRequested: Int,
        bytesReadOut: IntRef,
        callback: CefCallback?
    ): Boolean {
        try {
            val availableSize = stream.available()
            if (availableSize > 0) {
                var bytesToRead = min(bytesRequested.toDouble(), availableSize.toDouble()).toInt()
                bytesToRead = stream.read(dataOut, 0, bytesToRead)
                bytesReadOut.set(bytesToRead)
                return true
            }
        } catch (e: IOException) {
            LOG.error(e)
        }
        bytesReadOut.set(0)
        try {
            stream.close()
        } catch (e: IOException) {
            LOG.error(e)
        }
        return false
    }

    companion object {
        private val LOG = Logger.getInstance(HtmlStringResourceHandler::class.java)
    }
}
