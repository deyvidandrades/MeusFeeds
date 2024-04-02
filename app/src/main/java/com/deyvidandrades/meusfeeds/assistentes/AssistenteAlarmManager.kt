package com.deyvidandrades.meusfeeds.assistentes

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.deyvidandrades.meusfeeds.servicos.NotificationReceiver
import java.util.Calendar

object AssistenteAlarmManager {
    private const val REQUEST_CODE = 2
    fun criarAlarme(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intentVerificacao = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (intentVerificacao == null) {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, Persistencia.horarioNotificacao)
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis + 1000,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    /*
    fun cancelarAlarme(context: Context) {
        val existingPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, NotificationReceiver::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(existingPendingIntent)
    }
    */
}