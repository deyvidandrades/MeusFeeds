package com.deyvidandrades.meusfeeds.adaptadoes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.interfaces.OnItemClickListener
import com.deyvidandrades.meusfeeds.objetos.FeedGroup
import com.google.android.material.imageview.ShapeableImageView


class AdaptadorFeedGroup(private val context: Context, arrayList: ArrayList<FeedGroup>, listener: OnItemClickListener) :
    RecyclerView.Adapter<AdaptadorFeedGroup.ViewHolder>() {

    private val listener: OnItemClickListener
    private var arrayList: ArrayList<FeedGroup> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_feed_group, parent, false
        )

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedGroup = arrayList[position]

        RequestManager.carregarImagem(context, holder.ivFavicon, feedGroup.favicon)

        holder.tvFeedGroupTitulo.text = feedGroup.titulo
        holder.tvFeedGroupDescricao.text = feedGroup.descricao
        holder.tvFeedGroupCount.text = "${feedGroup.itemCount} artigos."

        holder.btnAdicionarFeedGroup.visibility = if (feedGroup.adicionado) View.GONE else View.VISIBLE
        holder.btnRemoverFeedGroup.visibility = if (feedGroup.adicionado) View.VISIBLE else View.GONE
        holder.tvFeedGroupDescricao.visibility = if (feedGroup.descricao != "") View.VISIBLE else View.GONE

        holder.btnAdicionarFeedGroup.setOnClickListener {
            listener.onItemClicked(feedGroup)
        }

        holder.btnRemoverFeedGroup.setOnClickListener {
            listener.onItemClicked(feedGroup, true)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFavicon: ShapeableImageView = itemView.findViewById(R.id.iv_favicon)
        var tvFeedGroupTitulo: TextView = itemView.findViewById(R.id.id_feed_group_titulo)
        var tvFeedGroupDescricao: TextView = itemView.findViewById(R.id.id_feed_group_descricao)
        var tvFeedGroupCount: TextView = itemView.findViewById(R.id.id_feed_group_item_count)
        var btnAdicionarFeedGroup: Button = itemView.findViewById(R.id.btn_adicionar_feed_group)
        var btnRemoverFeedGroup: Button = itemView.findViewById(R.id.btn_remover_feed_group)
    }

    init {
        this.arrayList = arrayList
        this.listener = listener
    }
}