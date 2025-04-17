package com.deyvidandrades.meusfeeds.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.dataclasses.Fonte
import com.deyvidandrades.meusfeeds.interfaces.OnFontesRecomendadasClickListener
import com.google.android.material.imageview.ShapeableImageView


class AdaptadorFontesRecomendadas(
    private val context: Context,
    arrayList: ArrayList<Fonte>,
    listener: OnFontesRecomendadasClickListener
) :
    RecyclerView.Adapter<AdaptadorFontesRecomendadas.ViewHolder>() {

    private val listener: OnFontesRecomendadasClickListener
    private var arrayList: ArrayList<Fonte> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_fonte_recomendada, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        RequestManager.carregarImagem(context, holder.ivFonteFavicon, item.favicon)
        holder.tvFonteTitulo.text = item.titulo
        holder.tvFonteDescricao.text = item.descricao

        holder.btnChecked.visibility = if (item.isActive) View.VISIBLE else View.GONE
        holder.btnUnchecked.visibility = if (item.isActive) View.GONE else View.VISIBLE

        holder.btnChecked.setOnClickListener {
            listener.onFonteRecomendadaSelected(item.id, false)
        }

        holder.btnUnchecked.setOnClickListener {
            listener.onFonteRecomendadaSelected(item.id, true)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFonteFavicon: ShapeableImageView = itemView.findViewById(R.id.iv_fonte_favicon)
        var tvFonteDescricao: TextView = itemView.findViewById(R.id.tv_fonte_descricao)
        var tvFonteTitulo: TextView = itemView.findViewById(R.id.tv_fonte_titulo)

        var btnChecked: Button = itemView.findViewById(R.id.btn_checked)
        var btnUnchecked: Button = itemView.findViewById(R.id.btn_unchecked)
    }

    init {
        this.arrayList = arrayList
        this.listener = listener
    }
}