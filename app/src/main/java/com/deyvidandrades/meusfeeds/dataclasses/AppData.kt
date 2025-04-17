package com.deyvidandrades.meusfeeds.dataclasses

data class AppData(
    var firstTime:Boolean = true,
    var darkTheme:Boolean = false,
    var notifications:Boolean = true,
    var arrayFontes: ArrayList<Fonte> = ArrayList(),
    var arrayArtigos: ArrayList<Artigo> = ArrayList(),
    var arrayArtigosSalvos: ArrayList<Artigo> = ArrayList(),
)
