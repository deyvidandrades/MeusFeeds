package com.deyvidandrades.meusfeeds.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class DialogoFonte : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialogo_fonte, container, false)

        val btnVoltar: MaterialButton = view.findViewById(R.id.btn_voltar)
        val btnRemoverFonte: MaterialButton = view.findViewById(R.id.btn_remove_fonte)
        val fonte = Persistencia.FONTE_ATUAL

        if (fonte != null) {
            val ivFonteFavicon: ShapeableImageView = view.findViewById(R.id.iv_fonte_favicon)
            val tvFonteDescription: TextView = view.findViewById(R.id.tv_fonte_descricao)
            val tvFonteTitle: TextView = view.findViewById(R.id.tv_fonte_titulo)
            val tvFonteLink: TextView = view.findViewById(R.id.tv_fonte_link)
            val tvTitle: TextView = view.findViewById(R.id.title)

            RequestManager.carregarImagem(requireContext(), ivFonteFavicon, fonte.favicon)
            tvTitle.text = getString(R.string.detalhes)
            tvFonteDescription.text = fonte.descricao
            tvFonteTitle.text = fonte.titulo
            tvFonteLink.text = fonte.url

            tvFonteDescription.visibility = if (fonte.descricao != "") View.VISIBLE else View.GONE

            btnRemoverFonte.setOnClickListener {
                Persistencia.removerFonte(fonte.id)
                Toast.makeText(requireContext(), "Fonte removida.", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        } else {
            Toast.makeText(requireContext(), "Nenhuma fonte carregada.", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnVoltar.setOnClickListener {
            dismiss()
        }

        return view
    }
}