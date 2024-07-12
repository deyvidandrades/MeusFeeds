package com.deyvidandrades.meusfeeds.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adaptadoes.AdaptadorFeedGroup
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.interfaces.OnItemClickListener
import com.deyvidandrades.meusfeeds.objetos.FeedGroup

class CarregarDadosActivity : AppCompatActivity(), OnItemClickListener {
    private val arrayFeedsGroups = ArrayList<FeedGroup>()
    private lateinit var adaptadorFeedGroup: AdaptadorFeedGroup
    private lateinit var loading: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carregar_dados)

        loading = findViewById(R.id.loading)
        val btnVoltar: Button = findViewById(R.id.btn_voltar)
        val btnInfo: Button = findViewById(R.id.btn_info)
        val btnConcluido: Button = findViewById(R.id.btn_concluido)

        Persistencia.getInstance(this)

        //Recycler feed group
        val recyclerHabitos: RecyclerView = findViewById(R.id.recycler_feed_group)
        adaptadorFeedGroup = AdaptadorFeedGroup(this, arrayFeedsGroups, this)

        recyclerHabitos.setHasFixedSize(true)
        recyclerHabitos.adapter = adaptadorFeedGroup
        recyclerHabitos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        btnVoltar.setOnClickListener {
            finish()
        }

        btnConcluido.setOnClickListener {
            finish()
        }

        btnInfo.setOnClickListener {
            //todo dialog
            Toast.makeText(this, "Info", Toast.LENGTH_SHORT).show()
        }

        if (Persistencia.LISTA_FEED_GROUPS != null)
            carregarFeedGroups(Persistencia.LISTA_FEED_GROUPS!!)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarFeedGroups(array: ArrayList<FeedGroup>) {
        arrayFeedsGroups.clear()
        arrayFeedsGroups.addAll(array)
        adaptadorFeedGroup.notifyDataSetChanged()
    }

    override fun onItemClicked(item: Any, remover: Boolean) {
        if (!remover) {
            Persistencia.adicionarFeedGroup(item as FeedGroup)
            if (Persistencia.feedGroupExists(item.id)) {
                arrayFeedsGroups[arrayFeedsGroups.indexOf(item)].adicionado = true
                adaptadorFeedGroup.notifyItemChanged(arrayFeedsGroups.indexOf(item))

                Toast.makeText(this, getString(R.string.grupo_adicionado), Toast.LENGTH_SHORT).show()
            }
        } else {
            Persistencia.removerFeedGroup(item as FeedGroup)
            if (!Persistencia.feedGroupExists(item.id)) {
                arrayFeedsGroups[arrayFeedsGroups.indexOf(item)].adicionado = false
                adaptadorFeedGroup.notifyItemChanged(arrayFeedsGroups.indexOf(item))

                Toast.makeText(this, getString(R.string.grupo_removido), Toast.LENGTH_SHORT).show()
            }
        }
    }
}