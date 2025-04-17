package com.deyvidandrades.meusfeeds.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.activities.ArtigoActivity
import com.deyvidandrades.meusfeeds.assistentes.DataUtil
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.dataclasses.Artigo
import com.deyvidandrades.meusfeeds.interfaces.OnArtigoClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView


class AdaptadorPreviewArtigos(
    private val context: Context,
    arrayList: ArrayList<Artigo>,
    listener: OnArtigoClickListener
) :
    RecyclerView.Adapter<AdaptadorPreviewArtigos.ViewHolder>() {

    private val listener: OnArtigoClickListener
    private var arrayList: ArrayList<Artigo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_artigo, parent, false
        )

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artigo = arrayList[position]

        holder.tvArtigoTitulo.text = Html.fromHtml(artigo.titulo, Html.FROM_HTML_MODE_COMPACT)
        holder.tvArtigoData.text = DataUtil.getDataFormatada(artigo.getDataMilli())
        holder.tvArtigoCategoria.text = " - ${
            Html.fromHtml(
                if (artigo.categorias.isEmpty()) "" else artigo.categorias.first(),
                Html.FROM_HTML_MODE_COMPACT
            )
        }"

        holder.tvFeedGroupTitulo.text = artigo.fonte.titulo
        holder.tvArtigoCategoria.visibility = if (artigo.categorias.isEmpty()) View.GONE else View.VISIBLE

        RequestManager.carregarImagem(context, holder.ivFeedGroupFavicon, artigo.fonte.favicon)
        RequestManager.carregarImagem(context, holder.ivArtigoCapa, artigo.imagem)

        holder.ivArtigoCapa.visibility = if (artigo.imagem != "") View.VISIBLE else View.GONE
        holder.btnArtigoSavedChecked.visibility = if (Persistencia.isArtigoSaved(artigo)) View.VISIBLE else View.GONE
        holder.btnArtigoSavedUnchecked.visibility = if (Persistencia.isArtigoSaved(artigo)) View.GONE else View.VISIBLE

        holder.ivArtigoCapa.setOnClickListener {
            Persistencia.ARTIGO_ATUAL = artigo
            context.startActivity(Intent(context, ArtigoActivity::class.java))
        }

        holder.tvArtigoTitulo.setOnClickListener {
            Persistencia.ARTIGO_ATUAL = artigo
            context.startActivity(Intent(context, ArtigoActivity::class.java))
        }

        holder.btnArtigoSavedChecked.setOnClickListener {
            listener.onArtigoUnSaved(artigo)

            holder.btnArtigoSavedChecked.visibility = View.GONE
            holder.btnArtigoSavedUnchecked.visibility = View.VISIBLE
        }
        holder.btnArtigoSavedUnchecked.setOnClickListener {
            listener.onArtigoSaved(artigo)

            holder.btnArtigoSavedChecked.visibility = View.VISIBLE
            holder.btnArtigoSavedUnchecked.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFeedGroupFavicon: ShapeableImageView = itemView.findViewById(R.id.iv_feed_group_favicon)
        var tvFeedGroupTitulo: TextView = itemView.findViewById(R.id.tv_feed_group_titulo)

        var ivArtigoCapa: ShapeableImageView = itemView.findViewById(R.id.iv_artigo_capa)
        var tvArtigoTitulo: TextView = itemView.findViewById(R.id.tv_artigo_titulo)
        var tvArtigoData: TextView = itemView.findViewById(R.id.tv_artigo_data)
        var tvArtigoCategoria: TextView = itemView.findViewById(R.id.tv_artigo_categoria)

        var btnArtigoSavedChecked: MaterialButton = itemView.findViewById(R.id.btn_artigo_saved_checked)
        var btnArtigoSavedUnchecked: MaterialButton = itemView.findViewById(R.id.btn_artigo_saved_unchecked)
    }

    init {
        this.arrayList = arrayList
        this.listener = listener
    }
}