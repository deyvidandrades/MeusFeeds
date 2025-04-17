package com.deyvidandrades.meusfeeds.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.interfaces.OnCategoryClickListener


class AdaptadorCategories(
    private val context: Context,
    arrayList: ArrayList<String>,
    listener: OnCategoryClickListener
) :
    RecyclerView.Adapter<AdaptadorCategories.ViewHolder>() {

    private val listener: OnCategoryClickListener
    private var arrayList: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList[position]

        holder.tvCategoryTitle.text = Html.fromHtml(item, Html.FROM_HTML_MODE_COMPACT)

        holder.tvCategoryTitle.setOnClickListener {
            listener.onCategoryClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCategoryTitle: TextView = itemView.findViewById(R.id.tv_category_title)
    }

    init {
        this.arrayList = arrayList
        this.listener = listener
    }
}