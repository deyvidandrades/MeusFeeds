package com.deyvidandrades.meusfeeds.dialogos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.WorkManagerUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.materialswitch.MaterialSwitch

class DialogoConfigurarNotificacoes : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dialogoView = inflater.inflate(R.layout.dialogo_configurar_notificacoes, container, false)

        val btnVoltar: Button = dialogoView.findViewById(R.id.btn_voltar)
        val btnConcluido: Button = dialogoView.findViewById(R.id.btn_concluido)

        val switchNotificacoes: MaterialSwitch = dialogoView.findViewById(R.id.switch_notificacoes)

        btnVoltar.setOnClickListener {
            dismiss()
        }

        switchNotificacoes.isChecked = Persistencia.notificacao

        btnConcluido.setOnClickListener {
            if (switchNotificacoes.isChecked) {
                WorkManagerUtil.stopWorker(requireContext(), WorkManagerUtil.Tipo.ARTIGOS)
                WorkManagerUtil.iniciarWorker(requireContext(), WorkManagerUtil.Tipo.ARTIGOS)
            }

            if (switchNotificacoes.isChecked != Persistencia.notificacao)
                Persistencia.setNotificacoes()

            dismiss()
        }

        return dialogoView
    }
}