package com.deyvidandrades.meusfeeds.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.assistentes.AnimacaoBotao
import com.deyvidandrades.meusfeeds.assistentes.Persistencia
import androidx.core.net.toUri

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnContinuar: Button = findViewById(R.id.btn_continuar)
        val tvTermos: TextView = findViewById(R.id.tv_termos)

        btnContinuar.setOnClickListener {
            Persistencia.setFirstTime(false)

            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            finish()
        }

        tvTermos.setOnClickListener {
            AnimacaoBotao.animar(it)
            startActivity(Intent(Intent.ACTION_VIEW, "https://deyvidandrades.github.io/MeusFeeds/termos/".toUri()))
        }
    }
}