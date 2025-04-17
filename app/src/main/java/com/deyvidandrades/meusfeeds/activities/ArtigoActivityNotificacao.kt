package com.deyvidandrades.meusfeeds.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.NotificacoesUtil
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.dataclasses.Artigo
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson

class ArtigoActivityNotificacao : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artigo)

        NotificacoesUtil.cancelarNotificacao(this)

        val artigoRaw = intent.getStringExtra("artigo")
        val artigo = Gson().fromJson(artigoRaw, Artigo::class.java)

        if (artigo != null) {
            val btnVoltar: Button = findViewById(R.id.btn_voltar)
            val btnInfo: Button = findViewById(R.id.btn_info)
            val nested: NestedScrollView = findViewById(R.id.nested)
            nested.isSmoothScrollingEnabled = true

            val tvFeedGroupTitulo: TextView = findViewById(R.id.tv_feed_group_titulo)
            val ivFeedGroupFavicon: ShapeableImageView = findViewById(R.id.iv_feed_group_favicon)

            val tvArtigoData: TextView = findViewById(R.id.tv_artigo_data)
            val tvArtigoTitulo: TextView = findViewById(R.id.tv_artigo_titulo)
            val tvArtigoDescricao: TextView = findViewById(R.id.tv_artigo_descricao)
            val tvArtigoCategoria: TextView = findViewById(R.id.tv_artigo_categoria)
            val tvArtigoConteudo: TextView = findViewById(R.id.tv_artigo_conteudo)
            val ivArtigoCapa: ShapeableImageView = findViewById(R.id.iv_artigo_capa)

            tvFeedGroupTitulo.text = artigo.fonte.titulo
            tvArtigoData.text = Html.fromHtml(artigo.data, Html.FROM_HTML_MODE_COMPACT)
            tvArtigoTitulo.text = Html.fromHtml(artigo.titulo, Html.FROM_HTML_MODE_COMPACT)
            tvArtigoDescricao.text = Html.fromHtml(
                Html.fromHtml(artigo.descricao, Html.FROM_HTML_MODE_COMPACT).toString(), Html.FROM_HTML_MODE_COMPACT
            )
            tvArtigoCategoria.text = Html.fromHtml(artigo.categorias.joinToString(), Html.FROM_HTML_MODE_COMPACT)

            tvArtigoConteudo.text = Html.fromHtml(
                Html.fromHtml(artigo.conteudo, Html.FROM_HTML_MODE_COMPACT).toString(), Html.FROM_HTML_MODE_COMPACT
            )

            ivArtigoCapa.visibility = if (artigo.imagem != "") View.VISIBLE else View.GONE

            RequestManager.carregarImagem(this, ivFeedGroupFavicon, artigo.fonte.favicon)
            RequestManager.carregarImagem(this, ivArtigoCapa, artigo.imagem)

            tvArtigoCategoria.visibility = if (artigo.categorias.isEmpty()) View.GONE else View.VISIBLE

            btnVoltar.setOnClickListener {
                finish()
                startActivity(Intent(this@ArtigoActivityNotificacao, MainActivity::class.java))
            }

            btnInfo.setOnClickListener {
                nested.fullScroll(View.FOCUS_DOWN)
            }
        }
    }
}