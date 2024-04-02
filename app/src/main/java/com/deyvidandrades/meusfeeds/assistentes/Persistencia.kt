package com.deyvidandrades.meusfeeds.assistentes

import android.content.Context
import android.content.SharedPreferences
import com.deyvidandrades.meusfeeds.objetos.Artigo
import com.deyvidandrades.meusfeeds.objetos.FeedGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Persistencia {
    var ARTIGO_ATUAL: Artigo? = null

    var isFirstTime: Boolean = true
    var isDarkTheme: Boolean = false
    var horarioNotificacao: Int = 8

    private var preferences: SharedPreferences? = null

    private var arrayFeedGroups = ArrayList<FeedGroup>()

    enum class Paths { FEED_GROUPS, IS_FIRST_TIME, IS_DARK_THEME, HORARIO_NOTIFICACOES }

    fun getInstance(context: Context) {
        preferences = context.getSharedPreferences("MAIN_DATA", Context.MODE_PRIVATE)
        carregarDados()
    }

    /*FLUXO DADOS*/

    private fun carregarDados() {
        if (preferences != null)
            try {
                val listaRaw = preferences!!.getString(Paths.FEED_GROUPS.name.lowercase(), "")!!
                isFirstTime = preferences!!.getBoolean(Paths.IS_FIRST_TIME.name.lowercase(), true)
                isDarkTheme = preferences!!.getBoolean(Paths.IS_DARK_THEME.name.lowercase(), false)
                horarioNotificacao = preferences!!.getInt(Paths.HORARIO_NOTIFICACOES.name.lowercase(), 8)

                val typeToken = object : TypeToken<ArrayList<FeedGroup>>() {}.type

                arrayFeedGroups.clear()
                arrayFeedGroups.addAll(Gson().fromJson(listaRaw, typeToken))
            } catch (e: NullPointerException) {
                arrayFeedGroups = ArrayList()
            }
    }

    private fun salvarDados() {
        if (preferences != null) {
            with(preferences!!.edit()) {
                putBoolean(Paths.IS_FIRST_TIME.name.lowercase(), isFirstTime)
                putBoolean(Paths.IS_DARK_THEME.name.lowercase(), isDarkTheme)
                putInt(Paths.HORARIO_NOTIFICACOES.name.lowercase(), horarioNotificacao)
                putString(Paths.FEED_GROUPS.name.lowercase(), Gson().toJson(arrayFeedGroups))
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

    fun setNovoHorarioNotificacao(context: Context, valor: Int) {
        horarioNotificacao = valor
        salvarDados()
        AssistenteAlarmManager.criarAlarme(context)
    }

    /*FLUXO FEED GROUPS*/

    fun getFeedGroups(): ArrayList<FeedGroup> {
        return arrayFeedGroups
    }

    fun adicionarFeedGroup(feedGroup: FeedGroup) {
        for (group in arrayFeedGroups)
            if (group.siteUrl == feedGroup.siteUrl)
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
}