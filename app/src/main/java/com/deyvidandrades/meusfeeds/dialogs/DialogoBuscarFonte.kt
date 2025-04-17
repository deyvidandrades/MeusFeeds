package com.deyvidandrades.meusfeeds.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adapters.AdaptadorFontes
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.assistentes.RssParser
import com.deyvidandrades.meusfeeds.dataclasses.Fonte
import com.deyvidandrades.meusfeeds.interfaces.OnFontesClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.net.URL

class DialogoBuscarFonte : BottomSheetDialogFragment(), OnFontesClickListener {
    private var array = ArrayList<Fonte>()
    private lateinit var adaptador: AdaptadorFontes

    private lateinit var loading: LinearProgressIndicator
    private lateinit var btnBuscarFonte: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialogo_buscar_fonte, container, false)

        val btnVoltar: MaterialButton = view.findViewById(R.id.btn_voltar)
        val tvTitle: TextView = view.findViewById(R.id.title)

        tvTitle.text = getString(R.string.buscar_fonte_de_rss)

        val etAdicionarFeedGroup: TextInputEditText =
            view.findViewById(R.id.et_feed_group_link)

        loading = view.findViewById(R.id.progress)
        btnBuscarFonte = view.findViewById(R.id.btn_buscar_feed)

        /* FONTES */
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler)
        adaptador = AdaptadorFontes(requireContext(), array, this)

        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adaptador
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        btnVoltar.setOnClickListener {
            dismiss()
        }

        btnBuscarFonte.setOnClickListener {
            val text = etAdicionarFeedGroup.text.toString()
            if (text != "" && text.contains(".") && text.contains("https://")) {
                carregarFuncaoAssincrona(text)
            }
        }

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarFuncaoAssincrona(query: String) {
        val url = URL(
            "https://feedsearch.dev/api/v1/search?url=${query}&info=true&favicon=false&opml=false&skip_crawl=false"
        )
        lifecycleScope.launch {
            loading.visibility = View.VISIBLE
            btnBuscarFonte.visibility = View.GONE
            try {
                val result = RequestManager.fazerRequisicao(url)

                if (result != "") {
                    array.clear()
                    RssParser.getFontes(result) {
                        array.addAll(it)
                    }

                    array.sortBy { it.titulo }
                    adaptador.notifyDataSetChanged()

                } else {
                    Toast.makeText(requireContext(), getString(R.string.falha_ao_encontrar_feed), Toast.LENGTH_SHORT)
                        .show()
                }

            } catch (_: Exception) {
            }

            loading.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onFonteSelected(fonteId: Long, active: Boolean) {
        if (active) {
            array.forEach { item ->
                if (item.id == fonteId) {
                    item.isActive = false
                    Persistencia.adicionarFonte(item)

                    item.isActive = true
                    adaptador.notifyDataSetChanged()

                    Toast.makeText(context, getString(R.string.fonte_adicionada, item.titulo), Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }
    }
}