package com.deyvidandrades.meusfeeds.objetos

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale

data class Artigo(
    val titulo: String,
    val conteudo: String,
    val descricao: String,
    val feedGroup: FeedGroup,
    val data: String,
    val imagem: String = "",
    val categoria: String = ""
) : Comparable<Artigo> {
    val id: Long = Calendar.getInstance().timeInMillis

    fun getDataMilli(): Long {
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
                val zonedDateTime = ZonedDateTime.parse(data, formato)
                val instant = zonedDateTime.toInstant()
                milli = instant.toEpochMilli()
                break
            } catch (_: DateTimeParseException) {


            }
        }

        return milli
    }

    override fun compareTo(other: Artigo): Int {
        return getDataMilli().compareTo(other.getDataMilli())
    }
}
