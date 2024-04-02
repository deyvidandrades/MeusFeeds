package com.deyvidandrades.meusfeeds.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListPopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adaptadoes.AdaptadorPreviewArtigos
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.dialogos.DialogoAdicionarFeedGroup
import com.deyvidandrades.meusfeeds.interfaces.OnItemClickListener
import com.deyvidandrades.meusfeeds.objetos.Artigo
import com.deyvidandrades.meusfeeds.objetos.FeedGroup
import java.net.URL

class MainActivity : AppCompatActivity(), OnItemClickListener {
    private val arrayArtigos = ArrayList<Artigo>()

    private lateinit var adaptadorPreviewArtigos: AdaptadorPreviewArtigos
    private lateinit var loading: RelativeLayout

    private var categoriaSelecionada: String = ""

    private lateinit var btnEscolherCategorias: Button
    private lateinit var listPopupWindow: ListPopupWindow
    private lateinit var reMenu: RelativeLayout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loading = findViewById(R.id.loading)
        btnEscolherCategorias = findViewById(R.id.btn_filtro)
        reMenu = findViewById(R.id.re_menu)
        listPopupWindow = ListPopupWindow(this, null, androidx.transition.R.attr.listPopupWindowStyle)

        val btnAdicionar: Button = findViewById(R.id.btn_add)
        val btnReload: Button = findViewById(R.id.btn_reload)
        val btnOpcoes: Button = findViewById(R.id.btn_opcoes)

        val btnGerenciarFeeds: Button = findViewById(R.id.btn_gerenciar_feeds)
        val btnTermos: Button = findViewById(R.id.btn_termos)
        val tvVersao: TextView = findViewById(R.id.tv_versao)

        val info = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)

        tvVersao.text = "v${info.versionName}"

        Persistencia.getInstance(this)

        if (Persistencia.isFirstTime) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        //Recycler artigo
        val recyclerHabitos: RecyclerView = findViewById(R.id.recycler_artigos)
        adaptadorPreviewArtigos = AdaptadorPreviewArtigos(this, arrayArtigos, this)

        recyclerHabitos.setHasFixedSize(true)
        recyclerHabitos.adapter = adaptadorPreviewArtigos
        recyclerHabitos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        btnAdicionar.setOnClickListener {
            val customBottomSheet = DialogoAdicionarFeedGroup()
            customBottomSheet.show(
                supportFragmentManager,
                DialogoAdicionarFeedGroup().javaClass.name
            )
        }

        btnReload.setOnClickListener {
            loading.visibility = View.VISIBLE
            baixarArtigos(Persistencia.getFeedGroups())
        }

        btnOpcoes.setOnClickListener {
            carregarMenu()
        }

        btnGerenciarFeeds.setOnClickListener {
            startActivity(Intent(this@MainActivity, GerenciarFeedsActivity::class.java))
        }

        btnTermos.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://deyvidandrades.github.io/MeusFeeds/termos/")))
        }

        reMenu.setOnClickListener {
            carregarMenu()
        }

        loading.visibility = View.VISIBLE

        carregarFiltros()
        baixarArtigos(Persistencia.getFeedGroups())
    }

    private fun carregarMenu(duration: Long = 200) {
        fun fadeIn() {
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.duration = duration
            reMenu.visibility = View.VISIBLE
            reMenu.startAnimation(fadeIn)

        }

        fun fadeOut() {
            val fadeOut = AlphaAnimation(1f, 0f)
            fadeOut.duration = duration
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    reMenu.visibility = View.GONE
                }
            })
            reMenu.startAnimation(fadeOut)
        }

        if (reMenu.visibility == View.GONE) {
            fadeIn()
        } else {
            fadeOut()
        }
    }

    private fun carregarFiltros() {
        listPopupWindow.anchorView = btnEscolherCategorias

        val items = ArrayList<String>()
        items.add(getString(R.string.tudo))

        for (item in Persistencia.getFeedGroups())
            items.add(item.titulo)

        val adapter = android.widget.ArrayAdapter(this, R.layout.list_popup_window_item, items)
        listPopupWindow.setAdapter(adapter)

        listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, view: View?, position: Int, _: Long ->
            loading.visibility = View.VISIBLE
            btnEscolherCategorias.text = ((view) as TextView).text.toString()
            categoriaSelecionada = items[position]
            listPopupWindow.dismiss()

            val arrayFiltro = ArrayList<FeedGroup>()
            if (categoriaSelecionada != getString(R.string.tudo)) {
                for (item in Persistencia.getFeedGroups())
                    if (item.titulo == categoriaSelecionada)
                        arrayFiltro.add(item)
                baixarArtigos(arrayFiltro)
            } else
                baixarArtigos(Persistencia.getFeedGroups())
        }

        btnEscolherCategorias.setOnClickListener {
            listPopupWindow.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun baixarArtigos(array: ArrayList<FeedGroup>, numMaximo: Int = 2) {
        val arrayNovosFeeds = ArrayList<Artigo>()
        arrayArtigos.clear()
        for (feedGroup in array) {

            kotlinx.coroutines.runBlocking {
                val result =
                    RequestManager.fazerRequisicao(URL(feedGroup.url))

                if (result != "") {

                    val regexItem = """.*?<item>(?<item>.+?)</item>.*?"""
                    val matches = Regex(regexItem).findAll(result)

                    var index = numMaximo
                    matches.forEach { matchResult ->
                        if (index >= 1) {

                            val titulo = Regex("""<title>(.+?)</title>""").find(matchResult.value)
                            val data = Regex("""<pubDate>(.+?)</pubDate>""").find(matchResult.value)
                            val descricao = Regex("""<description>(.+?)</description>""").find(matchResult.value)
                            val conteudo = Regex("""<content:encoded>(.+?)</content:encoded>""").find(matchResult.value)
                            val categoria = Regex("""<category>(.+?)</category>""").find(matchResult.value)
                            val imagem = Regex("""<media:content url="(.+?)"""").find(matchResult.value)

                            val artigo = Artigo(
                                (titulo?.value ?: "").replace("<title>", "").replace("</title>", ""),
                                (descricao?.value ?: "").replace("<description>", "").replace("</description>", ""),
                                (categoria?.value ?: "").replace("<category>", "").replace("</category>", ""),
                                (data?.value ?: "").replace("<pubDate>", "").replace("</pubDate>", ""),
                                (conteudo?.value ?: "").replace("<content:encoded>", "")
                                    .replace("</content:encoded>", ""),
                                (imagem?.value ?: "").replace("<media:content url=\"", "").replace("\"", ""),
                                feedGroup
                            )

                            arrayNovosFeeds.add(artigo)
                        }
                        index -= 1
                    }
                }
            }
        }

        arrayArtigos.addAll(arrayNovosFeeds)
        arrayArtigos.sortBy { it.data }
        //arrayArtigos.shuffle()
        adaptadorPreviewArtigos.notifyDataSetChanged()
        loading.visibility = View.GONE
    }

    override fun onItemClicked(item: Any, remover: Boolean) {
        Persistencia.ARTIGO_ATUAL = item as Artigo
        startActivity(Intent(this@MainActivity, ArtigoActivity::class.java))
    }
}