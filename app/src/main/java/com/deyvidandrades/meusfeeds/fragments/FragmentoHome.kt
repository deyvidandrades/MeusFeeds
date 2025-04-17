package com.deyvidandrades.meusfeeds.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adapters.AdaptadorCategories
import com.deyvidandrades.meusfeeds.adapters.AdaptadorPreviewArtigos
import com.deyvidandrades.meusfeeds.adapters.AdaptadorPreviewDestaques
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.assistentes.RssParser
import com.deyvidandrades.meusfeeds.dataclasses.Artigo
import com.deyvidandrades.meusfeeds.interfaces.OnArtigoClickListener
import com.deyvidandrades.meusfeeds.interfaces.OnCategoryClickListener
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch
import java.net.URL

class FragmentoHome : Fragment(R.layout.fragmento_home), OnCategoryClickListener, OnArtigoClickListener {
    private var array = ArrayList<Artigo>()
    private lateinit var adaptador: AdaptadorPreviewArtigos

    private var arrayCategories = ArrayList<String>()
    private lateinit var adaptadorCategories: AdaptadorCategories

    private var arrayDestaques = ArrayList<Artigo>()
    private lateinit var adaptadorPreviewDestaques: AdaptadorPreviewDestaques

    private lateinit var btnEscolherCategorias: Button
    private lateinit var liNenhumArtigo: LinearLayout
    private lateinit var listPopupWindow: ListPopupWindow
    private var fonteSelecionada: String = ""

    private lateinit var progress: LinearProgressIndicator

    private lateinit var liDestaquesHolder: LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragmento_home, container, false)

        progress = view.findViewById(R.id.progress_indicator)
        liNenhumArtigo = view.findViewById(R.id.li_nenhum_item)
        btnEscolherCategorias = view.findViewById(R.id.btn_filtro)
        listPopupWindow = ListPopupWindow(requireContext(), null)

        liDestaquesHolder = view.findViewById(R.id.li_destaques_holder)

        /* ARTIGOS */
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_artigos)
        val mySwipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        adaptador = AdaptadorPreviewArtigos(requireContext(), array, this)

        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adaptador
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)

        mySwipeRefreshLayout.setOnRefreshListener {
            recarregarArtigos()
            mySwipeRefreshLayout.isRefreshing = false
        }

        /* CATEGORIES */
        val recyclerViewCategories: RecyclerView = view.findViewById(R.id.recycler_categories)
        adaptadorCategories = AdaptadorCategories(requireContext(), arrayCategories, this)

        recyclerViewCategories.setHasFixedSize(false)
        recyclerViewCategories.adapter = adaptadorCategories
        recyclerViewCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        /* DESTAQUE */
        val recyclerViewDestaque: RecyclerView = view.findViewById(R.id.recycler_destaques)
        adaptadorPreviewDestaques = AdaptadorPreviewDestaques(requireContext(), arrayDestaques)

        recyclerViewDestaque.setHasFixedSize(false)
        recyclerViewDestaque.adapter = adaptadorPreviewDestaques
        recyclerViewDestaque.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewDestaque)

        carregarCategories()
        carregarDestaques()
        carregarFiltros()
        carregarArtigos()
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarDestaques() {
        arrayDestaques.clear()

        Persistencia.getDestaques().forEach {
            arrayDestaques.add(it)
        }

        adaptadorPreviewDestaques.notifyDataSetChanged()
        liDestaquesHolder.visibility = if (arrayDestaques.isEmpty()) View.GONE else View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarCategories() {
        arrayCategories.clear()

        Persistencia.getCategories().forEach {
            arrayCategories.add(it)
        }
        arrayCategories.add(0, "<b>Todos</b>")

        adaptadorCategories.notifyDataSetChanged()
    }

    private fun carregarFiltros() {
        listPopupWindow.anchorView = btnEscolherCategorias

        val items = ArrayList<String>()
        items.add(getString(R.string.tudo))

        for (item in Persistencia.getFontesActive())
            items.add(item.titulo)

        val adapter = ArrayAdapter(requireContext(), R.layout.list_popup_window_item, items)
        listPopupWindow.setAdapter(adapter)

        listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, view: View?, position: Int, _: Long ->
            btnEscolherCategorias.text = ((view) as TextView).text.toString()
            fonteSelecionada = items[position]
            listPopupWindow.dismiss()

            carregarArtigos(fonteSelecionada)
        }

        btnEscolherCategorias.setOnClickListener {
            listPopupWindow.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun carregarArtigos(filtro: String = "", categoria: String = "") {
        array.clear()
        val artigos = Persistencia.getArtigos()
        val arrayAux = ArrayList<Artigo>()

        if (filtro == "" || filtro == getString(R.string.tudo))
            arrayAux.addAll(artigos)
        else
            artigos.forEach { artigo ->
                if (artigo.fonte.titulo == filtro)
                    arrayAux.add(artigo)
            }

        if (categoria == "" || categoria == "<b>Todos</b>")
            array.addAll(arrayAux)
        else
            arrayAux.forEach { artigo ->
                if (categoria in artigo.categorias)
                    array.add(artigo)
            }

        array.sortByDescending { it.getDataMilli() }
        adaptador.notifyDataSetChanged()

        liNenhumArtigo.visibility = if (array.isEmpty()) View.VISIBLE else View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun recarregarArtigos() {
        progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            val arrayUpdate = ArrayList<Artigo>()
            for (fonte in Persistencia.getFontesActive()) {
                val result = RequestManager.fazerRequisicao(URL(fonte.url))

                if (result != "") {
                    RssParser.getArrayArtigos(fonte, result) {
                        arrayUpdate.addAll(it)
                    }

                }
            }

            arrayUpdate.sortByDescending { it.getDataMilli() }
            Persistencia.updateArtigos(arrayUpdate)

            progress.visibility = View.GONE

            carregarCategories()
            carregarDestaques()
            carregarFiltros()
            carregarArtigos()
        }
    }

    override fun onCategoryClicked(categoria: String) {
        carregarArtigos(fonteSelecionada, if (categoria != "<b>Todos</b>") categoria else "")
    }

    override fun onArtigoSaved(artigo: Artigo) {
        Persistencia.adicionarArtigoSalvo(artigo)
        carregarArtigos(fonteSelecionada)
    }

    override fun onArtigoUnSaved(artigo: Artigo) {
        Persistencia.removerArtigoSalvo(artigo)
        carregarArtigos(fonteSelecionada)
    }
}