package com.deyvidandrades.meusfeeds.assistentes

import com.deyvidandrades.meusfeeds.dataclasses.Artigo
import com.deyvidandrades.meusfeeds.dataclasses.Fonte
import com.google.gson.JsonParser
import java.net.URL
import java.util.Calendar

object RssParser {
    fun getArrayArtigos(
        fonte: Fonte,
        dataRaw: String,
        numMaximo: Int = 5,
        listener: (ArrayList<Artigo>) -> Unit
    ) {
        val matches = Regex("""<item>(.+?)</item>""", RegexOption.DOT_MATCHES_ALL).findAll(dataRaw)

        val arrayList = ArrayList<Artigo>()
        var index = numMaximo

        matches.forEach { match ->
            if (index >= 0) {
                val article = match.value

                val titulo = Regex("""<title>(.+?)</title>""", RegexOption.DOT_MATCHES_ALL).find(article)
                val data = Regex("""<pubDate>(.+?)</pubDate>""", RegexOption.DOT_MATCHES_ALL).find(article)
                val descricao = Regex("""<description>(.+?)</description>""", RegexOption.DOT_MATCHES_ALL).find(article)
                val link = Regex("""<link>(.+?)</link>""", RegexOption.DOT_MATCHES_ALL).find(article)

                val conteudo =
                    Regex("""<content:encoded>(.+?)</content:encoded>""", RegexOption.DOT_MATCHES_ALL).find(article)

                val thumbnails = Regex("""<media:thumbnail.*?url="(.+?)".*?/>""", RegexOption.DOT_MATCHES_ALL)
                    .findAll(article).map { it.groupValues[1] }.toCollection(ArrayList())

                val contentImages =
                    Regex("""<media:content.*url="(.+?)"""", RegexOption.DOT_MATCHES_ALL)
                        .findAll(article).map { it.groupValues[1] }.toCollection(ArrayList())

                val categories = Regex("""<category>(.+?)</category>""", RegexOption.DOT_MATCHES_ALL)
                    .findAll(article).map { it.groupValues[1] }.toCollection(ArrayList())

                val arrayImages = ArrayList<String>()
                arrayImages.addAll(thumbnails)
                arrayImages.addAll(contentImages)
                arrayImages.add("")

                val image = if (arrayImages[0].contains("${URL(fonte.url).host}/${URL(fonte.url).host}"))
                    arrayImages[0].replace(
                        "${URL(fonte.url).host}/${URL(fonte.url).host}",
                        URL(fonte.url).host
                    )
                else
                    arrayImages[0]
                image.replace("?w=200", "")

                try {
                    arrayList.add(
                        Artigo(
                            if (titulo != null) titulo.groupValues[1] else "",
                            if (conteudo != null) conteudo.groupValues[1] else "",
                            if (descricao != null) descricao.groupValues[1] else "",
                            fonte,
                            if (data != null) data.groupValues[1] else "",
                            if (link != null) link.groupValues[1] else "",
                            if (arrayImages.isEmpty()) "" else image,
                            if (categories.isEmpty()) ArrayList() else categories
                        )
                    )
                } catch (_: Exception) {
                }
            }
            index--
        }
        listener.invoke(arrayList)
    }

    fun getFontes(dataRaw: String, listener: (ArrayList<Fonte>) -> Unit) {
        val jsonObject = JsonParser.parseString("""{"data": $dataRaw}""").getAsJsonObject()
        val array = ArrayList<Fonte>()

        var index = 0
        for (joArray in jsonObject["data"].asJsonArray) {
            val item = joArray.asJsonObject

            if (!item["is_podcast"].asBoolean && item["item_count"].asInt > 0) { // && item["score"].asInt >= 0
                array.add(
                    Fonte(
                        item["title"].toString().replace("\"", ""),
                        item["description"].toString().replace("\"", "").replace("null", ""),
                        item["favicon"].toString().replace("\"", ""),
                        item["item_count"].toString().replace("\"", "").toInt(),
                        item["url"].toString().replace("\"", ""),
                        item["last_updated"].toString().replace("\"", ""),
                        false,
                        Calendar.getInstance().timeInMillis + index
                    )
                )
            }
            index += 1
        }

        listener.invoke(array)
    }
}