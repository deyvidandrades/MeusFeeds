package com.deyvidandrades.meusfeeds.objetos

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale

data class FeedGroup(
    val titulo: String,
    val descricao: String,
    val favicon: String,
    val itemCount: Int,
    val lastUpdate: String,
    val url: String,
    val score: Int,
    var adicionado: Boolean = false
) : Comparable<FeedGroup> {
    val id: Long = Calendar.getInstance().timeInMillis

    override fun toString(): String {
        return "Artigo(titulo=$titulo, descricao=$descricao, favicon=$favicon, itemCount=$itemCount, lastUpdate=$lastUpdate, url=$url, score=$score, id=$id, adicionado=$adicionado)"
    }

    private fun getDataMilli(): Long {
        val listaFormatos = listOf(
            DateTimeFormatter.BASIC_ISO_DATE,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_TIME,
            DateTimeFormatter.ISO_ORDINAL_DATE,
            DateTimeFormatter.ISO_TIME,
            DateTimeFormatter.ISO_WEEK_DATE,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.RFC_1123_DATE_TIME
        )

        var milli: Long = -1

        for (formato in listaFormatos) {
            try {
                val zonedDateTime = ZonedDateTime.parse(lastUpdate, formato)
                val instant = zonedDateTime.toInstant()
                milli = instant.toEpochMilli()
                break
            } catch (_: DateTimeParseException) {


            }
        }

        return milli
    }

    fun getDataFormatada(): String {
        val instant = Instant.ofEpochMilli(getDataMilli())
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }

    override fun compareTo(other: FeedGroup): Int {
        return getDataMilli().compareTo(other.getDataMilli())
    }
}
