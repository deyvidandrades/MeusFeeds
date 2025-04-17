package com.deyvidandrades.meusfeeds.assistentes

import android.content.Context
import android.content.SharedPreferences
import android.text.Html
import com.deyvidandrades.meusfeeds.dataclasses.AppData
import com.deyvidandrades.meusfeeds.dataclasses.Artigo
import com.deyvidandrades.meusfeeds.dataclasses.Fonte
import com.google.gson.Gson

object Persistencia {
    var ARTIGO_ATUAL: Artigo? = null
    var FONTE_ATUAL: Fonte? = null
    private var preferences: SharedPreferences? = null
    private lateinit var appData: AppData

    /*INIT*/
    fun init(context: Context) {
        preferences = context.getSharedPreferences("MAIN_DATA", Context.MODE_PRIVATE)
        carregarDados()
    }

    private fun checkInit() {
        if (preferences == null)
            throw IllegalStateException("Persistencia.kt is not initialized. Call init() first.")
    }

    /*FLUXO DADOS*/
    private fun carregarDados() {
        checkInit()

        val appDataRaw = preferences!!.getString("appData", "")
        appData = if (appDataRaw != "") Gson().fromJson(appDataRaw, AppData::class.java) else AppData()
    }

    private fun salvarDados() {
        checkInit()

        with(preferences!!.edit()) {
            putString("appData", Gson().toJson(appData))
            commit()
        }
        carregarDados()
    }

    /*FLUXO SETTINGS*/
    fun setFirstTime(value: Boolean) {
        appData.firstTime = value
        salvarDados()
    }

    fun setDarkMode(value: Boolean) {
        appData.darkTheme = value
        salvarDados()
    }

    fun setNotifications(value: Boolean) {
        appData.notifications = value
        salvarDados()
    }

    fun getNotifications() = appData.notifications

    fun getFirstTime() = appData.firstTime

    fun getDarkMode() = appData.darkTheme

    /* FLUXO FONTES */
    fun getFontes() = appData.arrayFontes

    fun getFontesActive(): ArrayList<Fonte> {
        val arrayFontesActive = ArrayList<Fonte>()
        appData.arrayFontes.forEach {
            if (it.isActive)
                arrayFontesActive.add(it)
        }

        return arrayFontesActive
    }

    fun adicionarFonte(novaFonte: Fonte) {
        for (fonte in appData.arrayFontes)
            if (fonte.url == novaFonte.url)
                return

        novaFonte.isActive = true
        appData.arrayFontes.add(novaFonte)
        salvarDados()
    }

    fun removerFonte(fonteId: Long) {
        appData.arrayFontes.removeIf {
            it.id == fonteId
        }
        salvarDados()
    }

    fun atualizarFonte(fonteId: Long, isActive: Boolean) {
        for (fonte in appData.arrayFontes)
            if (fonte.id == fonteId)
                fonte.isActive = isActive

        salvarDados()
    }

    fun fonteExists(fonteId: Long): Boolean {
        for (fonte in appData.arrayFontes)
            if (fonte.id == fonteId)
                return true
        return false
    }

    /* FLUXO ARTIGOS */
    fun getArtigos() = appData.arrayArtigos

    fun getCategories(): ArrayList<String> {
        val array = ArrayList<String>()

        appData.arrayArtigos.forEach { artigo ->
            artigo.categorias.forEach { categoria ->
                if (!array.contains(categoria))
                    array.add(categoria)
            }
        }
        array.sort()

        return array
    }

    fun getDestaques(): ArrayList<Artigo> {
        val arrayDestaques = ArrayList<Artigo>()

        appData.arrayArtigos.forEach { artigo ->
            if (artigo.categorias.isNotEmpty())
                arrayListOf("news").forEach { item ->
                    if (Html.fromHtml(artigo.categorias.first(), Html.FROM_HTML_MODE_COMPACT).toString()
                            .lowercase() == item
                    )
                        arrayDestaques.add(artigo)
                }
        }
        return arrayDestaques
    }

    fun updateArtigos(array: ArrayList<Artigo>) {
        appData.arrayArtigos.clear()
        appData.arrayArtigos.addAll(array)
        salvarDados()
    }

    /* FLUXO ARTIGOS SALVOS */
    fun getArtigosSalvos() = appData.arrayArtigosSalvos

    fun isArtigoSaved(artigo: Artigo): Boolean {
        appData.arrayArtigosSalvos.forEach {
            if (it.titulo == artigo.titulo)
                return true
        }

        return false
    }

    fun adicionarArtigoSalvo(artigo: Artigo) {
        appData.arrayArtigosSalvos.forEach {
            if (it.titulo == artigo.titulo) return
        }

        appData.arrayArtigosSalvos.add(artigo)
        salvarDados()
    }

    fun removerArtigoSalvo(artigo: Artigo) {
        var artigoRemover: Artigo? = null
        appData.arrayArtigosSalvos.forEach {
            if (it.titulo == artigo.titulo)
                artigoRemover = it
        }

        appData.arrayArtigosSalvos.remove(artigoRemover)
        salvarDados()
    }

}