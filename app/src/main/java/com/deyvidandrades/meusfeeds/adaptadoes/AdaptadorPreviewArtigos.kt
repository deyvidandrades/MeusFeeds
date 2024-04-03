package com.deyvidandrades.meusfeeds.adaptadoes

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.interfaces.OnItemClickListener
import com.deyvidandrades.meusfeeds.objetos.Artigo
import com.google.android.material.imageview.ShapeableImageView


class AdaptadorPreviewArtigos(context: Context, arrayList: ArrayList<Artigo>, listener: OnItemClickListener) :
    RecyclerView.Adapter<AdaptadorPreviewArtigos.ViewHolder>() {

    private val context: Context
    private val listener: OnItemClickListener
    private var arrayList: ArrayList<Artigo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_preview_artigo, parent, false
        )

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artigo = arrayList[position]

        val descricao = Html.fromHtml(
            Html.fromHtml(artigo.descricao, Html.FROM_HTML_MODE_COMPACT).toString(),
            Html.FROM_HTML_MODE_COMPACT
        ).toString()

        holder.tvArtigoTitulo.text = Html.fromHtml(artigo.titulo, Html.FROM_HTML_MODE_COMPACT)
        holder.tvArtigoDescricao.text = if (descricao.length > 120) descricao.substring(0, 120) + "..." else descricao
        holder.tvArtigoCategoria.text = Html.fromHtml(artigo.categoria, Html.FROM_HTML_MODE_COMPACT)
        holder.btnContinuarLendo.setOnClickListener {

            //Persistencia.ARTIGO_ATUAL = artigo
            //context.startActivity(Intent(context, ArtigoActivity::class.java))

            listener.onItemClicked(artigo)
        }

        holder.tvFeedGroupTitulo.text = artigo.feedGroup.titulo
        holder.tvArtigoCategoria.visibility = if (artigo.categoria == "") View.GONE else View.VISIBLE

        RequestManager.carregarImagem(context, holder.ivFeedGroupFavicon, artigo.feedGroup.favicon)
        RequestManager.carregarImagem(context, holder.ivArtigoCapa, artigo.imagem)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFeedGroupFavicon: ShapeableImageView
        var tvFeedGroupTitulo: TextView

        var ivArtigoCapa: ShapeableImageView
        var tvArtigoTitulo: TextView
        var tvArtigoDescricao: TextView
        var tvArtigoCategoria: TextView
        var btnContinuarLendo: TextView

        init {
            ivFeedGroupFavicon = itemView.findViewById(R.id.iv_feed_group_favicon)
            tvFeedGroupTitulo = itemView.findViewById(R.id.tv_feed_group_titulo)

            ivArtigoCapa = itemView.findViewById(R.id.iv_artigo_capa)
            tvArtigoTitulo = itemView.findViewById(R.id.tv_artigo_titulo)
            tvArtigoDescricao = itemView.findViewById(R.id.tv_artigo_descricao)
            tvArtigoCategoria = itemView.findViewById(R.id.tv_artigo_categoria)
            btnContinuarLendo = itemView.findViewById(R.id.btn_continuar_lendo)
        }
    }

    init {
        this.context = context
        this.arrayList = arrayList
        this.listener = listener
    }
}