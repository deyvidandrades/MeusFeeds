package com.deyvidandrades.meusfeeds.assistentes

import com.deyvidandrades.meusfeeds.objetos.Artigo
import com.deyvidandrades.meusfeeds.objetos.FeedGroup
import com.google.gson.JsonParser
import java.net.URL

object RssParser {
    fun getArrayArtigos(
        feedGroup: FeedGroup,
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

                val image = if (arrayImages[0].contains("${URL(feedGroup.url).host}/${URL(feedGroup.url).host}"))
                    arrayImages[0].replace(
                        "${URL(feedGroup.url).host}/${URL(feedGroup.url).host}",
                        URL(feedGroup.url).host
                    )
                else
                    arrayImages[0]
                image.replace("?w=200","")

                try {
                    arrayList.add(
                        Artigo(
                            if (titulo != null) titulo.groupValues[1] else "",
                            if (conteudo != null) conteudo.groupValues[1] else "",
                            if (descricao != null) descricao.groupValues[1] else "",
                            feedGroup,
                            if (data != null) data.groupValues[1] else "",
                            if (arrayImages.isEmpty()) "" else image,
                            if (categories.isEmpty()) "" else categories.first().toString()
                        )
                    )
                } catch (_: Exception) {
                }
            }
            index--
        }
        listener.invoke(arrayList)
    }

    fun getFeedGroups(dataRaw: String, listener: (ArrayList<FeedGroup>) -> Unit) {
        val jsonObject = JsonParser.parseString("""{"data": $dataRaw}""").getAsJsonObject()
        val array = ArrayList<FeedGroup>()

        for (joArray in jsonObject["data"].asJsonArray) {
            val item = joArray.asJsonObject

            array.add(
                FeedGroup(
                    item["title"].toString().replace("\"", ""),
                    item["description"].toString().replace("\"", "").replace("null", ""),
                    item["favicon"].toString().replace("\"", ""),
                    item["item_count"].toString().replace("\"", "").toInt(),
                    item["last_updated"].toString().replace("\"", ""),
                    item["url"].toString().replace("\"", ""),
                    item["score"].toString().replace("\"", "").toInt(),
                )
            )
        }

        listener.invoke(array)
    }
}