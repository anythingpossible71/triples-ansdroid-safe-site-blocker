package com.example.triples_safesiteblocker

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import java.io.InputStream

class LocalBlockHttpServer(private val context: Context) : NanoHTTPD(8080) {
    override fun serve(session: IHTTPSession): Response {
        val assetManager = context.assets
        val inputStream: InputStream = assetManager.open("blocked.html")
        return newChunkedResponse(Response.Status.OK, "text/html", inputStream)
    }
} 