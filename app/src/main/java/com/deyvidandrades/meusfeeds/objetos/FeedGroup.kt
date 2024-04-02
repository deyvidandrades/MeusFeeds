package com.deyvidandrades.meusfeeds.objetos

import com.deyvidandrades.meusfeeds.interfaces.Objeto
import java.util.Calendar

data class FeedGroup(
    val titulo: String,
    val descricao: String,
    val favicon: String,
    val isPodcast: Boolean,
    val itemCount: Int,
    val lastUpdate: String,
    val score: Int,
    val siteName: String,
    val siteUrl: String,
    val url: String,
    val id: Long = Calendar.getInstance().timeInMillis,
    var adicionado: Boolean = false
) : Objeto {

    override fun toString(): String {
        return "Artigo(titulo=$titulo, descricao=$descricao, favicon=$favicon, isPodcast=$isPodcast, itemCount=$itemCount, lastUpdate=$lastUpdate, score=$score, siteName=$siteName, siteUrl=$siteUrl, url=$url, id=$id, adicionado=$adicionado)"
    }

    override fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["id"] = id
        result["url"] = url
        result["score"] = score
        result["titulo"] = titulo
        result["favicon"] = favicon
        result["siteUrl"] = siteUrl
        result["siteName"] = siteName
        result["descricao"] = descricao
        result["isPodcast"] = isPodcast
        result["itemCount"] = itemCount
        result["lastUpdate"] = lastUpdate
        result["adicionado"] = adicionado
        return result
    }

    override fun compareTo(o: Any): Int {
        return score.compareTo((o as FeedGroup).score)
    }
}
