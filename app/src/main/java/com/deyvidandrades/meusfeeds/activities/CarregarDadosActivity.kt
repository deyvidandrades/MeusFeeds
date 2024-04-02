package com.deyvidandrades.meusfeeds.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adaptadoes.AdaptadorFeedGroup
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.interfaces.OnItemClickListener
import com.deyvidandrades.meusfeeds.objetos.FeedGroup
import com.google.gson.JsonParser
import java.net.URL

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

        val url = intent.getStringExtra("url")


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

        carregarFuncaoAssincrona(URL(url))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun carregarFeedGroups(array: ArrayList<FeedGroup>) {
        arrayFeedsGroups.clear()
        arrayFeedsGroups.addAll(array)
        adaptadorFeedGroup.notifyDataSetChanged()
    }

    private fun carregarFuncaoAssincrona(url: URL) {
        loading.visibility = View.VISIBLE
        kotlinx.coroutines.runBlocking {

            val array = ArrayList<FeedGroup>()
            val result = RequestManager.fazerRequisicao(url)

            if (result != "") {
                val jsonObject = JsonParser.parseString("""{"data": $result}""").getAsJsonObject()

                for (joArray in jsonObject["data"].asJsonArray) {
                    val item = joArray.asJsonObject

                    array.add(
                        FeedGroup(
                            item["title"].toString().replace("\"", ""),
                            item["description"].toString().replace("\"", ""),
                            item["favicon"].toString().replace("\"", ""),
                            item["is_podcast"].toString().replace("\"", "").toBoolean(),
                            item["item_count"].toString().replace("\"", "").toInt(),
                            item["last_updated"].toString().replace("\"", ""),
                            item["score"].toString().replace("\"", "").toInt(),
                            item["site_name"].toString().replace("\"", ""),
                            item["site_url"].toString().replace("\"", ""),
                            item["url"].toString().replace("\"", "")
                        )
                    )
                }

                array.sortByDescending { it.score }
                carregarFeedGroups(array)
            }
        }

        loading.visibility = View.GONE
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