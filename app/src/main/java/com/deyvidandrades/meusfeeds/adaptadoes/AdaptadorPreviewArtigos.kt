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


class AdaptadorPreviewArtigos(private val context: Context, arrayList: ArrayList<Artigo>, listener: OnItemClickListener) :
    RecyclerView.Adapter<AdaptadorPreviewArtigos.ViewHolder>() {

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
        var ivFeedGroupFavicon: ShapeableImageView = itemView.findViewById(R.id.iv_feed_group_favicon)
        var tvFeedGroupTitulo: TextView = itemView.findViewById(R.id.tv_feed_group_titulo)

        var ivArtigoCapa: ShapeableImageView = itemView.findViewById(R.id.iv_artigo_capa)
        var tvArtigoTitulo: TextView = itemView.findViewById(R.id.tv_artigo_titulo)
        var tvArtigoDescricao: TextView = itemView.findViewById(R.id.tv_artigo_descricao)
        var tvArtigoCategoria: TextView = itemView.findViewById(R.id.tv_artigo_categoria)
        var btnContinuarLendo: TextView = itemView.findViewById(R.id.btn_continuar_lendo)
    }

    init {
        this.arrayList = arrayList
        this.listener = listener
    }
}