package com.deyvidandrades.meusfeeds.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListPopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adaptadoes.AdaptadorPreviewArtigos
import com.deyvidandrades.meusfeeds.assistentes.NotificacoesUtil
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.assistentes.RssParser
import com.deyvidandrades.meusfeeds.assistentes.WorkManagerUtil
import com.deyvidandrades.meusfeeds.dialogos.DialogoAdicionarFeedGroup
import com.deyvidandrades.meusfeeds.dialogos.DialogoConfigurarNotificacoes
import com.deyvidandrades.meusfeeds.interfaces.OnItemClickListener
import com.deyvidandrades.meusfeeds.objetos.Artigo
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.util.Calendar

class MainActivity : AppCompatActivity(), OnItemClickListener {
    private val arrayArtigos = ArrayList<Artigo>()

    private lateinit var adaptadorPreviewArtigos: AdaptadorPreviewArtigos
    private lateinit var loading: RelativeLayout

    private var categoriaSelecionada: String = ""

    private lateinit var btnEscolherCategorias: Button
    private lateinit var listPopupWindow: ListPopupWindow
    private lateinit var reMenu: RelativeLayout
    private lateinit var liNenhumArtigo: LinearLayout
    private lateinit var tvUltimaAtualizacao: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loading = findViewById(R.id.loading)
        btnEscolherCategorias = findViewById(R.id.btn_filtro)
        reMenu = findViewById(R.id.re_menu)
        liNenhumArtigo = findViewById(R.id.li_nenhum_item)
        tvUltimaAtualizacao = findViewById(R.id.tv_ultima_atualizacao)

        listPopupWindow = ListPopupWindow(this, null, androidx.transition.R.attr.listPopupWindowStyle)

        val btnAdicionar: Button = findViewById(R.id.btn_add)
        val btnReload: Button = findViewById(R.id.btn_reload)
        val btnOpcoes: Button = findViewById(R.id.btn_opcoes)
        val btnNotificacoes: Button = findViewById(R.id.btn_notificacoes)

        val btnGerenciarFeeds: Button = findViewById(R.id.btn_gerenciar_feeds)
        val btnTemaEscuro: Button = findViewById(R.id.btn_mudar_tema)
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
            recarregarArtigos()
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

        btnTemaEscuro.setOnClickListener {
            Persistencia.setDarkTheme()
            mudarTema()
        }

        btnNotificacoes.setOnClickListener {
            val customBottomSheet = DialogoConfigurarNotificacoes()
            customBottomSheet.show(
                supportFragmentManager,
                DialogoConfigurarNotificacoes().javaClass.name
            )
        }

        reMenu.setOnClickListener {
            carregarMenu()
        }

        loading.visibility = View.VISIBLE

        mudarTema()
        carregarFiltros()
        carregarArtigos()

        configurarPermissoesNotificacao()

        WorkManagerUtil.iniciarWorker(this, WorkManagerUtil.Tipo.ARTIGOS)
    }

    private fun configurarPermissoesNotificacao() {

        val notificationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false) -> {
                        configurarNotificacoes()
                    }
                }
            }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) -> {
                configurarNotificacoes()
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    )
                }
            }
        }
    }

    private fun configurarNotificacoes() {
        NotificacoesUtil.cancelarNotificacao(this)
        NotificacoesUtil.criarCanalDeNotificacoes(this)
    }

    private fun mudarTema() {
        AppCompatDelegate.setDefaultNightMode(
            if (Persistencia.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
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

            carregarArtigos(categoriaSelecionada)
        }

        btnEscolherCategorias.setOnClickListener {
            listPopupWindow.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun carregarArtigos(filtro: String = "") {
        arrayArtigos.clear()
        Persistencia.getArrayArtigos { artigos, data ->
            if (filtro == "")
                arrayArtigos.addAll(artigos)
            else
                for (item in artigos)
                    if (item.feedGroup.titulo == filtro)
                        arrayArtigos.add(item)

            arrayArtigos.sortBy { it.data }

            adaptadorPreviewArtigos.notifyDataSetChanged()
            tvUltimaAtualizacao.text = "${getString(R.string.ultima_atualiza_o_em)} $data"

            liNenhumArtigo.visibility = if (arrayArtigos.isEmpty()) View.VISIBLE else View.GONE
            loading.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun recarregarArtigos() {
        loading.visibility = View.VISIBLE

        val arrayUpdate = ArrayList<Artigo>()
        for (feedGroup in Persistencia.getFeedGroups()) {
            runBlocking {
                val result = RequestManager.fazerRequisicao(URL(feedGroup.url))

                if (result != "")
                    RssParser.getArrayArtigos(feedGroup, result) {
                        arrayUpdate.addAll(it)
                    }
            }
        }

        Persistencia.setArrayArtigos(arrayUpdate, Calendar.getInstance().timeInMillis)
        carregarArtigos()

        loading.visibility = View.GONE
    }

    override fun onItemClicked(item: Any, remover: Boolean) {
        Persistencia.ARTIGO_ATUAL = item as Artigo
        startActivity(Intent(this@MainActivity, ArtigoActivity::class.java))
    }
}