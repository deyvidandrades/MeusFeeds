package com.deyvidandrades.meusfeeds.assistentes

import android.content.Context
import android.graphics.Bitmap
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
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object RequestManager {

    suspend fun fazerRequisicao(url: URL): String = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            try {
                val connection = URL(url.toString()).openConnection() as HttpURLConnection
                val result = connection.inputStream.bufferedReader().use { it.readText() }

                continuation.resume(result)
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
                    return false
                }

            }).into(view)
        } else
            view.visibility = View.GONE
    }

    fun carregarImagem(context: Context, url: String, listener: (Bitmap?) -> Unit) {
        if (url != "") {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: Target<Bitmap>, isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any,
                        target: Target<Bitmap>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        listener.invoke(resource)
                        return true
                    }

                }).submit()
        } else
            listener.invoke(null)
    }
}