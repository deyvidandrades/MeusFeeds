package com.deyvidandrades.meusfeeds.assistentes

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object RequestManager {

    suspend fun fazerRequisicao(url: URL): String = withContext(Dispatchers.IO) {

        suspendCoroutine { continuation ->
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                // Disable chunked transfer encoding
                connection.setChunkedStreamingMode(0)

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = StringBuilder()

                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    reader.close()
                    connection.disconnect()

                    continuation.resume(response.toString())
                } else {
                    continuation.resume("")
                }
            } catch (_: Exception) {
                continuation.resume("")
            }
        }
    }

    fun carregarImagem(context: Context, view: ImageView, url: String) {
        if (url != "") {
            Glide.with(context).load(url).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    view.visibility = View.GONE
                    DWS.getDados(e.toString())
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    view.visibility = View.VISIBLE
                    DWS.getDados("DEU")
                    return false
                }

            }).into(view)
        } else
            view.visibility = View.GONE
    }
}