package com.deyvidandrades.meusfeeds.assistentes

import com.deyvidandrades.meusfeeds.objetos.Artigo
import com.deyvidandrades.meusfeeds.objetos.FeedGroup
import com.google.gson.JsonParser

object RssParser {

    fun getArrayArtigos(
        feedGroup: FeedGroup,
        dataRaw: String,
        numMaximo: Int = 5,
        listener: (ArrayList<Artigo>) -> Unit
    ) {
        val regexItem = """.*?<item>(?<item>.+?)</item>.*?"""
        val matches = Regex(regexItem).findAll(dataRaw)

        var index = numMaximo
        val arrayList = ArrayList<Artigo>()
        matches.forEach { matchResult ->
            if (index >= 1) {

                val titulo = Regex("""<title>(.+?)</title>""").find(matchResult.value)
                val data = Regex("""<pubDate>(.+?)</pubDate>""").find(matchResult.value)
                val descricao = Regex("""<description>(.+?)</description>""").find(matchResult.value)
                val conteudo = Regex("""<content:encoded>(.+?)</content:encoded>""").find(matchResult.value)
                val categoria = Regex("""<category>(.+?)</category>""").find(matchResult.value)
                val imagem = Regex("""<media:content url="(.+?)"""").find(matchResult.value)

                val artigo = Artigo(
                    (titulo?.value ?: "").replace("<title>", "").replace("</title>", ""),
                    (descricao?.value ?: "").replace("<description>", "").replace("</description>", ""),
                    (categoria?.value ?: "").replace("<category>", "").replace("</category>", ""),
                    (data?.value ?: "").replace("<pubDate>", "").replace("</pubDate>", ""),
                    (conteudo?.value ?: "").replace("<content:encoded>", "")
                        .replace("</content:encoded>", ""),
                    (imagem?.value ?: "").replace("<media:content url=\"", "").replace("\"", ""),
                    feedGroup
                )

                arrayList.add(artigo)
            }
            index -= 1
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
                    item["description"].toString().replace("\"", ""),
                    item["favicon"].toString().replace("\"", ""),
                    item["is_podcast"].toString().replace("\"", "").toBoolean(),
                    item["item_count"].toString().replace("\"", "").toInt(),
                    item["last_updated"].toString().replace("\"", ""),
                    item["score"].toString().replace("\"", "").toInt(),
                    item["site_name"].toString().replace("\"", ""),
                    item["site_url"].toString().replace("\"", ""),
                    item["url"].toString().replace("\"", "")
                )
            )
        }

        listener.invoke(array)
    }
}