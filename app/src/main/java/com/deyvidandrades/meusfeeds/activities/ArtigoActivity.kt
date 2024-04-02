package com.deyvidandrades.meusfeeds.activities

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.google.android.material.imageview.ShapeableImageView

class ArtigoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artigo)

        val artigo = Persistencia.ARTIGO_ATUAL

        if (artigo != null) {
            val btnVoltar: Button = findViewById(R.id.btn_voltar)
            val btnInfo: Button = findViewById(R.id.btn_info)
            val liInfoHolder: LinearLayout = findViewById(R.id.li_info_holder)

            val tvFeedGroupTitulo: TextView = findViewById(R.id.tv_feed_group_titulo)
            val ivFeedGroupFavicon: ShapeableImageView = findViewById(R.id.iv_feed_group_favicon)

            val tvArtigoData: TextView = findViewById(R.id.tv_artigo_data)
            val tvArtigoTitulo: TextView = findViewById(R.id.tv_artigo_titulo)
            val tvArtigoDescricao: TextView = findViewById(R.id.tv_artigo_descricao)
            val tvArtigoCategoria: TextView = findViewById(R.id.tv_artigo_categoria)
            val tvArtigoConteudo: TextView = findViewById(R.id.tv_artigo_conteudo)
            val ivArtigoCapa: ShapeableImageView = findViewById(R.id.iv_artigo_capa)

            tvFeedGroupTitulo.text = artigo.feedGroup.titulo
            tvArtigoData.text = Html.fromHtml(artigo.data, Html.FROM_HTML_MODE_COMPACT)
            tvArtigoTitulo.text = Html.fromHtml(artigo.titulo, Html.FROM_HTML_MODE_COMPACT)
            tvArtigoDescricao.text = Html.fromHtml(
                Html.fromHtml(artigo.descricao, Html.FROM_HTML_MODE_COMPACT).toString(), Html.FROM_HTML_MODE_COMPACT
            )
            tvArtigoCategoria.text = Html.fromHtml(artigo.categoria, Html.FROM_HTML_MODE_COMPACT)

            tvArtigoConteudo.text = Html.fromHtml(
                Html.fromHtml(artigo.conteudo, Html.FROM_HTML_MODE_COMPACT).toString(), Html.FROM_HTML_MODE_COMPACT
            )

            ivArtigoCapa.visibility = if (artigo.imagem != "") View.VISIBLE else View.GONE

            Glide.with(this).load(artigo.feedGroup.favicon).into(ivFeedGroupFavicon)
            Glide.with(this).load(artigo.imagem).into(ivArtigoCapa)

            tvArtigoCategoria.visibility = if (artigo.categoria == "") View.GONE else View.VISIBLE

            btnVoltar.setOnClickListener {
                finish()
            }

            btnInfo.setOnClickListener {
                liInfoHolder.visibility = if (liInfoHolder.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }
    }
}