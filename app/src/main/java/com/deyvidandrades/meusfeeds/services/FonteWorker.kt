package com.deyvidandrades.meusfeeds.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.deyvidandrades.meusfeeds.assistentes.NotificacoesUtil
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.assistentes.RssParser
import com.deyvidandrades.meusfeeds.dataclasses.Artigo
import kotlinx.coroutines.runBlocking
import java.net.URL

class FonteWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {


    override fun doWork(): Result {
        Persistencia.init(applicationContext)

        val arrayArtigos = ArrayList<Artigo>()

        for (feedGroup in Persistencia.getFontesActive()) {
            runBlocking {
                val result = RequestManager.fazerRequisicao(URL(feedGroup.url))

                if (result != "")
                    RssParser.getArrayArtigos(feedGroup, result) {
                        arrayArtigos.addAll(it)
                    }
            }
        }

        Persistencia.updateArtigos(arrayArtigos)

        if (Persistencia.getNotifications())
            NotificacoesUtil.enviarNotificacao(applicationContext)

        return Result.success()
    }
}