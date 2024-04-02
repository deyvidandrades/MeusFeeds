package com.deyvidandrades.meusfeeds.objetos

import com.deyvidandrades.meusfeeds.interfaces.Objeto

data class Artigo(
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val data: String,
    var conteudo: String,
    val imagem:String,
    val feedGroup: FeedGroup
) : Objeto {

    override fun toString(): String {
        return "Artigo(titulo=$titulo, data=$data, descricao=$descricao, categoria=$categoria, conteudo=$conteudo) feedGroup=$feedGroup"
    }

    override fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["titulo"] = titulo
        result["descricao"] = descricao
        result["categoria"] = categoria
        result["data"] = data
        result["conteudo"] = conteudo
        return result
    }

    override fun compareTo(o: Any): Int {
        return titulo.compareTo((o as Artigo).titulo)
    }
}
