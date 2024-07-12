package com.deyvidandrades.meusfeeds.objetos

import java.util.Calendar

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

    override fun compareTo(other: Artigo): Int {
        return data.compareTo(other.data)
    }
}
