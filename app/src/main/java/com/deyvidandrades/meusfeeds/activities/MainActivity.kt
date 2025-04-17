package com.deyvidandrades.meusfeeds.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.adapters.AdaptadorFragmentos
import com.deyvidandrades.meusfeeds.assistentes.NotificacoesUtil
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import com.deyvidandrades.meusfeeds.assistentes.WorkManagerUtil
import com.deyvidandrades.meusfeeds.dialogs.DialogoBuscarFonte
import com.deyvidandrades.meusfeeds.dialogs.DialogoConfiguracoes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //Inicializando a base de dados
        Persistencia.init(this)

        if (Persistencia.getFirstTime()) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        configurarBottomNav()
        configurarPermissoesNotificacao()

        val btnAdd: MaterialButton = findViewById(R.id.btn_add)
        val btnSettings: MaterialButton = findViewById(R.id.btn_settings)

        btnAdd.setOnClickListener {
            val customBottomSheet = DialogoBuscarFonte()
            customBottomSheet.show(supportFragmentManager, DialogoBuscarFonte().javaClass.name)
        }
        btnSettings.setOnClickListener {
            val customBottomSheet = DialogoConfiguracoes()
            customBottomSheet.show(supportFragmentManager, DialogoConfiguracoes().javaClass.name)
        }

        WorkManagerUtil.iniciarWorker(this, WorkManagerUtil.Tipo.ARTIGOS)
    }

    private fun configurarPermissoesNotificacao() {
        val notificationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false) -> {
                        configurarNotificacoes()
                    }
                }
            }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) -> {
                configurarNotificacoes()
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    )
                }
            }
        }
    }

    private fun configurarNotificacoes() {
        NotificacoesUtil.cancelarNotificacao(this)
        NotificacoesUtil.criarCanalDeNotificacoes(this)
    }

    private fun configurarBottomNav() {
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val bottomBar: BottomNavigationView = findViewById(R.id.bottom_navigation)

        val badgeReviewCurtidas = bottomBar.getOrCreateBadge(R.id.item_2)
        badgeReviewCurtidas.isVisible = true
        badgeReviewCurtidas.number = 2

        fun setVisibility(value: Int) {
            when (value) {
                1 -> {
                    bottomBar.selectedItemId = R.id.item_2
                }

                2 -> {
                    bottomBar.selectedItemId = R.id.item_3
                }

                else -> {
                    bottomBar.selectedItemId = R.id.item_1
                }
            }
        }

        setVisibility(0)

        bottomBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    viewPager.currentItem = 0
                    true
                }

                R.id.item_2 -> {
                    viewPager.currentItem = 1
                    badgeReviewCurtidas.isVisible = false
                    true
                }

                R.id.item_3 -> {
                    viewPager.currentItem = 2
                    true
                }

                else -> false
            }
        }

        viewPager.adapter = AdaptadorFragmentos(this)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setVisibility(position)
            }
        })

    }
}