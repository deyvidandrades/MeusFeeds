package com.deyvidandrades.meusfeeds.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.deyvidandrades.meusfeeds.dataclasses.Fonte
import com.deyvidandrades.meusfeeds.dialogs.DialogoFonte
import com.deyvidandrades.meusfeeds.interfaces.OnFontesClickListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView


class AdaptadorFontes(private val context: Context, arrayList: ArrayList<Fonte>, listener: OnFontesClickListener) :
    RecyclerView.Adapter<AdaptadorFontes.ViewHolder>() {

    private val listener: OnFontesClickListener
    private var arrayList: ArrayList<Fonte> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.item_fonte, parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        RequestManager.carregarImagem(context, holder.ivFonteFavicon, item.favicon)
        holder.tvFonteTitulo.text = item.titulo

        holder.btnChecked.visibility = if (item.isActive) View.VISIBLE else View.GONE
        holder.btnUnchecked.visibility = if (item.isActive) View.GONE else View.VISIBLE

        holder.btnChecked.setOnClickListener {
            listener.onFonteSelected(item.id, false)
        }

        holder.btnUnchecked.setOnClickListener {
            listener.onFonteSelected(item.id, true)
        }
        holder.liInfoHolder.setOnClickListener {
            Persistencia.FONTE_ATUAL = item
            val customBottomSheet = DialogoFonte()
            customBottomSheet.show(
                (context as AppCompatActivity).supportFragmentManager, DialogoFonte().javaClass.name
            )
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFonteFavicon: ShapeableImageView = itemView.findViewById(R.id.iv_fonte_favicon)
        var tvFonteTitulo: TextView = itemView.findViewById(R.id.tv_fonte_titulo)

        var btnChecked: MaterialButton = itemView.findViewById(R.id.btn_checked)
        var btnUnchecked: MaterialButton = itemView.findViewById(R.id.btn_unchecked)

        var liInfoHolder:LinearLayout = itemView.findViewById(R.id.li_info_holder)
    }

    init {
        this.arrayList = arrayList
        this.listener = listener
    }
}