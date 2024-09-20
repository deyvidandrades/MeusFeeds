package com.deyvidandrades.meusfeeds.assistentes

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DataUtil {

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE d MMM yy", Locale.getDefault())
        return sdf.format(timestamp)
    }

    private fun formatHours(timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
        val differenceMillis = currentTimeMillis - timestamp
        val hoursDifference = TimeUnit.MILLISECONDS.toHours(differenceMillis)

        return if (hoursDifference > 0) "$hoursDifference horas atrás" else "Agora"
    }

    fun getDataFormatada(timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
        val differenceMillis = currentTimeMillis - timestamp
        val daysDifference = TimeUnit.MILLISECONDS.toDays(differenceMillis)

        return if (daysDifference >= 1) {
            formatDate(timestamp).replaceFirstChar { it.uppercase() }
        } else {
            formatHours(timestamp).replaceFirstChar { it.uppercase() }
        }
    }
}