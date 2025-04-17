package com.deyvidandrades.meusfeeds.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adapters.AdaptadorFontes
import com.deyvidandrades.meusfeeds.adapters.AdaptadorFontesRecomendadas
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.dataclasses.Fonte
import com.deyvidandrades.meusfeeds.dialogs.DialogoBuscarFonte
import com.deyvidandrades.meusfeeds.interfaces.OnFontesClickListener
import com.deyvidandrades.meusfeeds.interfaces.OnFontesRecomendadasClickListener
import com.google.android.material.button.MaterialButton

class FragmentoFontes : Fragment(R.layout.fragmento_fontes), OnFontesClickListener, OnFontesRecomendadasClickListener {
    private var arrayFontesRecomendadas = ArrayList<Fonte>()
    private lateinit var adaptadorFontesRecomendadas: AdaptadorFontesRecomendadas

    private var arrayUserFontes = ArrayList<Fonte>()
    private lateinit var adaptadorUserFonte: AdaptadorFontes

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragmento_fontes, container, false)

        /* RECOMENDACOES */
        val recyclerRecomendadas: RecyclerView = view.findViewById(R.id.recycler_recomendados)
        adaptadorFontesRecomendadas = AdaptadorFontesRecomendadas(requireContext(), arrayFontesRecomendadas, this)

        recyclerRecomendadas.setHasFixedSize(false)
        recyclerRecomendadas.adapter = adaptadorFontesRecomendadas
        recyclerRecomendadas.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        /* FONTES USUARIO */
        val recyclerUserFontes: RecyclerView = view.findViewById(R.id.recycler_user_fontes)
        val mySwipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        adaptadorUserFonte = AdaptadorFontes(requireContext(), arrayUserFontes, this)

        recyclerUserFontes.setHasFixedSize(false)
        recyclerUserFontes.adapter = adaptadorUserFonte
        recyclerUserFontes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        mySwipeRefreshLayout.setOnRefreshListener {
            carregarUserFontes()
            mySwipeRefreshLayout.isRefreshing = false
        }

        val btnAdicionarFonte: MaterialButton = view.findViewById(R.id.btn_add_fonte)
        btnAdicionarFonte.setOnClickListener {
            val customBottomSheet = DialogoBuscarFonte()
            customBottomSheet.show(
                (context as AppCompatActivity).supportFragmentManager, DialogoBuscarFonte().javaClass.name
            )
        }

        carregarFontesRecomendadas()
        carregarUserFontes()

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarUserFontes() {
        arrayUserFontes.clear()
        arrayUserFontes.addAll(Persistencia.getFontes())
        adaptadorUserFonte.notifyDataSetChanged()
    }

    private fun carregarFontesRecomendadas() {
        val r1 = Persistencia.fonteExists(0)
        val r2 = Persistencia.fonteExists(1)

        arrayFontesRecomendadas.clear()
        arrayFontesRecomendadas.add(
            Fonte(
                "Android Authority",
                "Android News, Reviews, How To",
                "https://www.androidauthority.com/favicon.ico",
                80,
                "https://www.androidauthority.com/feed/",
                "2024-07-29T10:37:30+00:00",
                r1,
                0
            )
        )
        adaptadorFontesRecomendadas.notifyItemInserted(arrayFontesRecomendadas.lastIndex)
        arrayFontesRecomendadas.add(
            Fonte(
                "It's FOSS News",
                "Latest on Linux, Open Source and More",
                "https://news.itsfoss.com/content/images/size/w256h256/2022/08/android-chrome-192x192.png",
                15,
                "https://news.itsfoss.com/latest/rss/",
                "2025-02-06T13:48:18+00:00",
                r2,
                1
            )
        )
        adaptadorFontesRecomendadas.notifyItemInserted(arrayFontesRecomendadas.lastIndex)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFonteSelected(fonteId: Long, active: Boolean) {
        arrayUserFontes.forEach {
            if (it.id == fonteId) {
                it.isActive = active

                Persistencia.atualizarFonte(fonteId, active)
            }
        }
        adaptadorUserFonte.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFonteRecomendadaSelected(fonteId: Long, active: Boolean) {
        arrayFontesRecomendadas.forEach {
            if (it.id == fonteId) {
                it.isActive = active

                if (active)
                    Persistencia.adicionarFonte(it)
                else
                    Persistencia.removerFonte(it.id)
            }
        }
        adaptadorFontesRecomendadas.notifyDataSetChanged()
        carregarUserFontes()
    }
}