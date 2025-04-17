package com.deyvidandrades.meusfeeds.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adapters.AdaptadorPreviewArtigos
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.dataclasses.Artigo
import com.deyvidandrades.meusfeeds.interfaces.OnArtigoClickListener

class FragmentoSalvos : Fragment(R.layout.fragmento_salvos), OnArtigoClickListener {
    private var array = ArrayList<Artigo>()
    private lateinit var adaptador: AdaptadorPreviewArtigos
    private lateinit var liNenhumItem: LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragmento_salvos, container, false)

        val recycler: RecyclerView = view.findViewById(R.id.recycler)
        val mySwipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        adaptador = AdaptadorPreviewArtigos(requireContext(), array, this)
        liNenhumItem = view.findViewById(R.id.li_nenhum_item)

        recycler.setHasFixedSize(false)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        mySwipeRefreshLayout.setOnRefreshListener {
            carregarArtigosSalvos()
            mySwipeRefreshLayout.isRefreshing = false
        }

        carregarArtigosSalvos()
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarArtigosSalvos() {
        array.clear()
        array.addAll(Persistencia.getArtigosSalvos())
        array.sort()
        adaptador.notifyDataSetChanged()

        liNenhumItem.visibility = if (array.size == 0) View.VISIBLE else View.GONE
    }

    override fun onArtigoSaved(artigo: Artigo) {
        Persistencia.adicionarArtigoSalvo(artigo)
        carregarArtigosSalvos()
    }

    override fun onArtigoUnSaved(artigo: Artigo) {
        Persistencia.removerArtigoSalvo(artigo)
        carregarArtigosSalvos()
    }
}