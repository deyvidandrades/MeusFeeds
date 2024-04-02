package com.deyvidandrades.meusfeeds.assistentes

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object RequestManager {

    suspend fun fazerRequisicao(context: Context, url: URL): String = withContext(Dispatchers.IO) {
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
                    Toast.makeText(context, "Falha ao carregar dados", Toast.LENGTH_SHORT).show()
                    continuation.resume("")
                }
            } catch (_: Exception) {
                Toast.makeText(context, "Falha ao carregar, tente novamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}