package com.deyvidandrades.meusfeeds.assistentes

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.deyvidandrades.meusfeeds.servicos.FeedGroupsWorker
import java.util.concurrent.TimeUnit

object WorkManagerUtil {
    enum class Tipo { ARTIGOS }

    fun iniciarWorker(context: Context, workerId: Tipo) {
        val workRequest = PeriodicWorkRequestBuilder<FeedGroupsWorker>(6, TimeUnit.HOURS).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workerId.name.lowercase(),
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun stopWorker(context: Context, workerId: Tipo) {
        WorkManager.getInstance(context).cancelUniqueWork(workerId.name.lowercase())
    }
}