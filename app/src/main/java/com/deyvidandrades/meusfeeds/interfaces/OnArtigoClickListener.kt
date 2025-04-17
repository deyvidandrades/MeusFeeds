package com.deyvidandrades.meusfeeds.interfaces

import com.deyvidandrades.meusfeeds.dataclasses.Artigo

interface OnArtigoClickListener {
    fun onArtigoSaved(artigo: Artigo)
    fun onArtigoUnSaved(artigo: Artigo)
}