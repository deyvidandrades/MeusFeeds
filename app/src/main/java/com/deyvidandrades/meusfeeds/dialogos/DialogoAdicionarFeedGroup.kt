package com.deyvidandrades.meusfeeds.dialogos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.activities.CarregarDadosActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class DialogoAdicionarFeedGroup : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dialogoView = inflater.inflate(R.layout.dialogo_adicionar_feed_group, container, false)

        val btnVoltar: Button = dialogoView.findViewById(R.id.btn_voltar)
        val btnInfo: Button = dialogoView.findViewById(R.id.btn_info)
        val btnAdicionarFeedGroup: Button = dialogoView.findViewById(R.id.btn_adicionar_feed_group)

        val liInfoHolder: LinearLayout = dialogoView.findViewById(R.id.li_info_holder)
        val etAdicionarFeedGroup: TextInputEditText = dialogoView.findViewById(R.id.et_feed_group_link)

        btnVoltar.setOnClickListener {
            dismiss()
        }

        btnInfo.setOnClickListener {
            liInfoHolder.visibility = if (liInfoHolder.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        btnAdicionarFeedGroup.setOnClickListener {
            val text = etAdicionarFeedGroup.text.toString()
            if (text != "" && text.contains(".") && text.contains("https://")) {

                val intent = Intent(requireContext(), CarregarDadosActivity::class.java)
                intent.putExtra(
                    "url",
                    "https://feedsearch.dev/api/v1/search?url=${text}&info=true&favicon=true&opml=false&skip_crawl=false"
                )
                requireContext().startActivity(intent)
                dismiss()
            }
        }

        return dialogoView
    }
}