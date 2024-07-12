package com.deyvidandrades.meusfeeds.servicos

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.deyvidandrades.meusfeeds.assistentes.NotificacoesUtil
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.assistentes.RssParser
import com.deyvidandrades.meusfeeds.objetos.Artigo
import kotlinx.coroutines.runBlocking
import java.net.URL

class FeedGroupsWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Persistencia.getInstance(applicationContext)

        val arrayArtigos = ArrayList<Artigo>()

        for (feedGroup in Persistencia.getFeedGroups()) {
            runBlocking {
                val result = RequestManager.fazerRequisicao(URL(feedGroup.url))

                if (result != "")
                    RssParser.getArrayArtigos(feedGroup, result) {
                        arrayArtigos.addAll(it)
                    }
            }
        }

        Persistencia.updateArtigos(arrayArtigos)

        if (Persistencia.notificacao)
            NotificacoesUtil.enviarNotificacao(applicationContext)

        return Result.success()
    }
}