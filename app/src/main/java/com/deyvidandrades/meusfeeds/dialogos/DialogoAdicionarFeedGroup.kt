package com.deyvidandrades.meusfeeds.dialogos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.activities.CarregarDadosActivity
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.assistentes.RssParser
import com.deyvidandrades.meusfeeds.objetos.FeedGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.net.URL

class DialogoAdicionarFeedGroup : BottomSheetDialogFragment() {
    private lateinit var loading: LinearProgressIndicator
    private lateinit var btnAdicionarFeedGroup: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dialogoView = inflater.inflate(R.layout.dialogo_adicionar_feed_group, container, false)

        val btnVoltar: Button = dialogoView.findViewById(R.id.btn_voltar)
        val btnInfo: Button = dialogoView.findViewById(R.id.btn_info)

        val liInfoHolder: LinearLayout = dialogoView.findViewById(R.id.li_info_holder)
        val etAdicionarFeedGroup: TextInputEditText =
            dialogoView.findViewById(R.id.et_feed_group_link)

        loading = dialogoView.findViewById(R.id.progress)
        btnAdicionarFeedGroup = dialogoView.findViewById(R.id.btn_adicionar_feed_group)

        btnVoltar.setOnClickListener {
            dismiss()
        }

        btnInfo.setOnClickListener {
            liInfoHolder.visibility =
                if (liInfoHolder.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        btnAdicionarFeedGroup.setOnClickListener {
            val text = etAdicionarFeedGroup.text.toString()
            if (text != "" && text.contains(".") && text.contains("https://")) {
                carregarFuncaoAssincrona(URL("https://feedsearch.dev/api/v1/search?url=${text}&info=true&favicon=true&opml=false&skip_crawl=false"))
            }
        }

        return dialogoView
    }

    private fun carregarFuncaoAssincrona(url: URL) {
        lifecycleScope.launch {
            loading.visibility = View.VISIBLE
            btnAdicionarFeedGroup.isEnabled = false
            try {
                val array = ArrayList<FeedGroup>()
                val result = RequestManager.fazerRequisicao(url)

                if (result != "") {
                    RssParser.getFeedGroups(result) {
                        array.addAll(it)
                    }

                    array.sortByDescending { it.score }
                    //carregarFeedGroups(array)
                    Persistencia.LISTA_FEED_GROUPS = array
                    requireContext().startActivity(
                        Intent(requireContext(), CarregarDadosActivity::class.java)
                    )

                    dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.falha_ao_encontrar_feed),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Persistencia.LISTA_FEED_GROUPS = null
            }
            loading.visibility = View.GONE
            btnAdicionarFeedGroup.isEnabled = true
        }
    }
}