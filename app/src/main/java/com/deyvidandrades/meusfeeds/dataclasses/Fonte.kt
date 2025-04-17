package com.deyvidandrades.meusfeeds.dataclasses

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar

data class Fonte(
    val titulo: String,
    val descricao: String,
    val favicon: String,
    val itemCount: Int,
    val url: String,
    val lastUpdate: String,
    var isActive: Boolean = false,
    val id: Long = Calendar.getInstance().timeInMillis
) : Comparable<Fonte> {

    override fun toString(): String {
        return "Fonte(titulo=$titulo, descricao=$descricao, favicon=$favicon, itemCount=$itemCount, lastUpdate=$lastUpdate, url=$url, id=$id, isActive=$isActive)"
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

    /*fun getDataFormatada(): String {
        val instant = Instant.ofEpochMilli(getDataMilli())
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy, HH:mm", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }*/

    override fun compareTo(other: Fonte): Int {
        return getDataMilli().compareTo(other.getDataMilli())
    }
}
