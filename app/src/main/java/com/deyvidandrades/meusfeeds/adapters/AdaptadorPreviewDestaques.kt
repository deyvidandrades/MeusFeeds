package com.deyvidandrades.meusfeeds.adapters

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.activities.ArtigoActivity
import com.deyvidandrades.meusfeeds.assistentes.DataUtil
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.dataclasses.Artigo
import com.google.android.material.imageview.ShapeableImageView


class AdaptadorPreviewDestaques(private val context: Context, arrayList: ArrayList<Artigo>) :
    RecyclerView.Adapter<AdaptadorPreviewDestaques.ViewHolder>() {

    private var arrayList: ArrayList<Artigo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_destaque, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        RequestManager.carregarImagem(context, holder.ivFonteFavicon, item.fonte.favicon)
        RequestManager.carregarImagem(context, holder.ivArtigoCapa, item.imagem)

        holder.tvFonteTittle.text = item.fonte.titulo

        holder.tvArtigoTitulo.text = Html.fromHtml(item.titulo, Html.FROM_HTML_MODE_COMPACT)
        holder.tvArtigoDescricao.text = Html.fromHtml(item.descricao, Html.FROM_HTML_MODE_COMPACT)
        holder.tvArtigoData.text = DataUtil.getDataFormatada(item.getDataMilli())

        holder.reHolderDestaques.setOnClickListener {
            Persistencia.ARTIGO_ATUAL = item
            context.startActivity(Intent(context, ArtigoActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFonteFavicon: ShapeableImageView = itemView.findViewById(R.id.iv_fonte_favicon)
        val tvFonteTittle: TextView = itemView.findViewById(R.id.tv_fonte_titulo)

        val ivArtigoCapa: ShapeableImageView = itemView.findViewById(R.id.iv_artigo_capa)
        val tvArtigoTitulo: TextView = itemView.findViewById(R.id.tv_artigo_titulo)
        val tvArtigoDescricao: TextView = itemView.findViewById(R.id.tv_artigo_descricao)
        val tvArtigoData: TextView = itemView.findViewById(R.id.tv_artigo_data)

        val reHolderDestaques: RelativeLayout = itemView.findViewById(R.id.re_holder_destaque)
    }

    init {
        this.arrayList = arrayList
    }
}