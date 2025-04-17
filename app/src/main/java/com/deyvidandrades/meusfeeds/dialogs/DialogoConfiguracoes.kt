package com.deyvidandrades.meusfeeds.dialogs

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.AnimacaoBotao
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.WorkManagerUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class DialogoConfiguracoes : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val dialogoView = inflater.inflate(R.layout.dialogo_configuracoes, container, false)

        val btnVoltar: MaterialButton = dialogoView.findViewById(R.id.btn_voltar)
        val btnSalvar: MaterialButton = dialogoView.findViewById(R.id.btn_salvar)

        val switchNotificacoes: MaterialSwitch = dialogoView.findViewById(R.id.switch_notificacoes)
        val switchTema: MaterialSwitch = dialogoView.findViewById(R.id.switch_tema)

        val tvTermos: TextView = dialogoView.findViewById(R.id.tv_termos)
        val tvVersao: TextView = dialogoView.findViewById(R.id.tv_versao)
        val tvFeedSearch: TextView = dialogoView.findViewById(R.id.tv_feedsearch)

        val tvTitle: TextView = dialogoView.findViewById(R.id.title)

        tvTitle.text = getString(R.string.configuracoes)

        switchNotificacoes.isChecked = Persistencia.getNotifications()
        switchTema.isChecked = Persistencia.getDarkMode()

        val info =
            requireContext().packageManager.getPackageInfo(requireContext().packageName, PackageManager.GET_ACTIVITIES)
        tvVersao.text = getString(
            R.string.version,
            getString(R.string.app_name),
            info.versionName,
            getString(R.string.desenvolvido_por)
        )

        btnVoltar.setOnClickListener {
            dismiss()
        }
        btnSalvar.setOnClickListener {
            if (switchNotificacoes.isChecked) {
                WorkManagerUtil.stopWorker(requireContext(), WorkManagerUtil.Tipo.ARTIGOS)
                WorkManagerUtil.iniciarWorker(requireContext(), WorkManagerUtil.Tipo.ARTIGOS)
            }

            if (switchNotificacoes.isChecked && !Persistencia.getNotifications())
                Persistencia.setNotifications(true)
            else if (!switchNotificacoes.isChecked && Persistencia.getNotifications())
                Persistencia.setNotifications(false)

            if (switchTema.isChecked && !Persistencia.getDarkMode())
                Persistencia.setDarkMode(true)
            else if(!switchTema.isChecked && Persistencia.getDarkMode())
                Persistencia.setDarkMode(false)

            AppCompatDelegate.setDefaultNightMode(
                if (Persistencia.getDarkMode()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            dismiss()
        }
        tvTermos.setOnClickListener {
            AnimacaoBotao.animar(it)
            startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.url_termos).toUri()))
        }
        tvFeedSearch.setOnClickListener {
            AnimacaoBotao.animar(it)
            startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.url_feedsearch_api).toUri()))
        }

        return dialogoView
    }
}