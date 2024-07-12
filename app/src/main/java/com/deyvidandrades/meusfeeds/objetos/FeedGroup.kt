package com.deyvidandrades.meusfeeds.objetos

import java.util.Calendar

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

    override fun compareTo(other: FeedGroup): Int {
        return lastUpdate.compareTo(other.lastUpdate)
    }
}
