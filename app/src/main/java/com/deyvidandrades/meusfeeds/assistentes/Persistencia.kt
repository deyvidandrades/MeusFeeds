package com.deyvidandrades.meusfeeds.assistentes

import android.content.Context
import android.content.SharedPreferences
import com.deyvidandrades.meusfeeds.objetos.Artigo
import com.deyvidandrades.meusfeeds.objetos.FeedGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Persistencia {
    var ARTIGO_ATUAL: Artigo? = null
    var LISTA_FEED_GROUPS: ArrayList<FeedGroup>? = null

    var isFirstTime: Boolean = true
    var isDarkTheme: Boolean = false
    var notificacao: Boolean = true

    private var preferences: SharedPreferences? = null

    private var arrayFeedGroups = ArrayList<FeedGroup>()
    private var arrayArtigos = ArrayList<Artigo>()

    enum class Paths { FEED_GROUPS, ARTIGOS, IS_FIRST_TIME, IS_DARK_THEME, NOTIFICACOES }

    fun getInstance(context: Context) {
        preferences = context.getSharedPreferences("MAIN_DATA", Context.MODE_PRIVATE)
        carregarDados()
    }

    /*FLUXO DADOS*/

    private fun carregarDados() {
        if (preferences != null) {
            val listaRawFeedGroups = preferences!!.getString(Paths.FEED_GROUPS.name.lowercase(), "")!!
            val listaRawArtigos = preferences!!.getString(Paths.ARTIGOS.name.lowercase(), "")!!

            isFirstTime = preferences!!.getBoolean(Paths.IS_FIRST_TIME.name.lowercase(), true)
            isDarkTheme = preferences!!.getBoolean(Paths.IS_DARK_THEME.name.lowercase(), false)
            notificacao = preferences!!.getBoolean(Paths.NOTIFICACOES.name.lowercase(), true)

            val typeTokenFeedGroup = object : TypeToken<ArrayList<FeedGroup>>() {}.type
            val typeTokenArtigos = object : TypeToken<ArrayList<Artigo>>() {}.type

            try {
                arrayFeedGroups.clear()
                arrayFeedGroups.addAll(Gson().fromJson(listaRawFeedGroups, typeTokenFeedGroup))
            } catch (e: NullPointerException) {
                arrayFeedGroups = ArrayList()
            }

            try {
                arrayArtigos.clear()
                arrayArtigos.addAll(Gson().fromJson(listaRawArtigos, typeTokenArtigos))
            } catch (e: NullPointerException) {
                DWS.getDados(e.toString())
                arrayArtigos = ArrayList()
            }
        }
    }

    private fun salvarDados() {
        if (preferences != null) {
            with(preferences!!.edit()) {
                putBoolean(Paths.IS_FIRST_TIME.name.lowercase(), isFirstTime)
                putBoolean(Paths.IS_DARK_THEME.name.lowercase(), isDarkTheme)
                putBoolean(Paths.NOTIFICACOES.name.lowercase(), notificacao)

                putString(Paths.FEED_GROUPS.name.lowercase(), Gson().toJson(arrayFeedGroups))
                putString(Paths.ARTIGOS.name.lowercase(), Gson().toJson(arrayArtigos))

                commit()
            }

            carregarDados()
        }
    }

    /*FLUXO SETTINGS*/
    fun setFirstTime() {
        isFirstTime = false
        salvarDados()
    }

    fun setDarkTheme() {
        isDarkTheme = !isDarkTheme
        salvarDados()
    }

    fun setNotificacoes() {
        notificacao = !notificacao
        salvarDados()
    }

    /*FLUXO FEED GROUPS*/

    fun getFeedGroups(): ArrayList<FeedGroup> {
        return arrayFeedGroups
    }

    fun adicionarFeedGroup(feedGroup: FeedGroup) {
        for (group in arrayFeedGroups)
            if (group.url == feedGroup.url)
                return

        feedGroup.adicionado = true
        arrayFeedGroups.add(feedGroup)
        salvarDados()
    }

    fun removerFeedGroup(feedGroup: FeedGroup) {
        arrayFeedGroups.remove(feedGroup)
        salvarDados()
    }

    fun feedGroupExists(feedGroupId: Long): Boolean {
        for (feed in arrayFeedGroups)
            if (feed.id == feedGroupId)
                return true

        return false
    }

    /*ARTIGOS*/
    fun getArtigos() = arrayArtigos

    fun updateArtigos(array: ArrayList<Artigo>) {
        arrayArtigos.clear()
        arrayArtigos.addAll(array)
        salvarDados()
    }
}