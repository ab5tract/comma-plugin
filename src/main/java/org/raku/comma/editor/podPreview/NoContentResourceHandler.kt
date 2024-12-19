package org.raku.comma.editor.podPreview

import org.cef.callback.CefCallback
import org.cef.handler.CefResourceHandlerAdapter
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse

class NoContentResourceHandler : CefResourceHandlerAdapter() {
    override fun processRequest(request: CefRequest, callback: CefCallback): Boolean {
        callback.Continue()
        return true
    }

    override fun getResponseHeaders(response: CefResponse, responseLength: IntRef?, redirectUrl: StringRef?) {
        response.status = 204
    }

    override fun readResponse(
        dataOut: ByteArray?,
        bytesToRead: Int,
        bytesRead: IntRef,
        callback: CefCallback?
    ): Boolean {
        bytesRead.set(0)
        return false
    }
}
