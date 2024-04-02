package com.deyvidandrades.meusfeeds.dialogos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider

class DialogoConfigurarNotificacoes : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dialogoView = inflater.inflate(R.layout.dialogo_configurar_notificacoes, container, false)

        val btnVoltar: Button = dialogoView.findViewById(R.id.btn_voltar)
        val btnConcluido: Button = dialogoView.findViewById(R.id.btn_concluido)

        val switchNotificacoes: MaterialSwitch = dialogoView.findViewById(R.id.switch_notificacoes)
        val sliderNotificacoes: Slider = dialogoView.findViewById(R.id.slider_notificacoes)

        val horarioNotificacoes = Persistencia.horarioNotificacao

        btnVoltar.setOnClickListener {
            dismiss()
        }

        btnConcluido.setOnClickListener {
            Persistencia.setNovoHorarioNotificacao(
                requireContext(),
                if (switchNotificacoes.isChecked) sliderNotificacoes.value.toInt() else -1
            )
            dismiss()
        }

        switchNotificacoes.isChecked = horarioNotificacoes != -1
        sliderNotificacoes.isEnabled = horarioNotificacoes != -1
        sliderNotificacoes.value = if (horarioNotificacoes != -1) horarioNotificacoes.toFloat() else 0.0F

        switchNotificacoes.setOnCheckedChangeListener { _, b ->
            sliderNotificacoes.isEnabled = b
            return@setOnCheckedChangeListener
        }

        return dialogoView
    }
}