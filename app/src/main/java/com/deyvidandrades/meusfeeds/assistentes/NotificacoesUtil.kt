package com.deyvidandrades.meusfeeds.assistentes

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.Html
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.deyvidandrades.meusfeeds.R
import com.deyvidandrades.meusfeeds.activities.ArtigoActivityNotificacao
import com.deyvidandrades.meusfeeds.activities.MainActivity
import com.google.gson.Gson

object NotificacoesUtil {
    private const val CHANNEL_ID = "meus_feeds_1"
    private const val NOTIFICATION_ID = 5
    fun criarCanalDeNotificacoes(context: Context) {

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), importance).apply {
            description = context.getString(R.string.notificacao_novo_artigo)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun enviarNotificacao(context: Context) {
        Persistencia.getInstance(context)

        Persistencia.getArrayArtigos { artigos, _ ->
            artigos.shuffle()
            val artigo = artigos[0]

            //ACAO DE CONTINUAR LENDO
            val intentContinuarLendo = Intent(context, ArtigoActivityNotificacao::class.java)
            intentContinuarLendo.putExtra("artigo", Gson().toJson(artigo))

            Persistencia.ARTIGO_ATUAL = artigo
            val pendingIntentContinuarLendo = PendingIntent.getActivity(
                context,
                0,
                intentContinuarLendo,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val actionContinuarLendo: NotificationCompat.Action = NotificationCompat.Action.Builder(
                R.drawable.round_arrow_outward_24,
                context.getString(R.string.continuar_lendo),
                pendingIntentContinuarLendo
            ).build()

            //ACAO DE VER MAIS ARTIGOS
            val intentMaisArtigos = Intent(context, MainActivity::class.java)

            val pendingIntentMaisArtigos = PendingIntent.getActivity(
                context,
                0,
                intentMaisArtigos,
                PendingIntent.FLAG_IMMUTABLE
            )

            val actionMaisArtigos: NotificationCompat.Action = NotificationCompat.Action.Builder(
                R.drawable.round_rss_feed_24,
                context.getString(R.string.mais_artigos),
                pendingIntentMaisArtigos
            ).build()

            RequestManager.carregarImagem(context, artigo.imagem) { bitmap ->
                //CRIANDO A NOTIFICACAO
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setColorized(true)
                    .setShowWhen(true)
                    .setColor(context.getColor(R.color.accent))
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentTitle(Html.fromHtml(artigo.titulo, Html.FROM_HTML_MODE_COMPACT))
                    .setContentText(
                        Html.fromHtml(
                            Html.fromHtml(
                                artigo.descricao,
                                Html.FROM_HTML_MODE_COMPACT
                            ).toString(),
                            Html.FROM_HTML_MODE_COMPACT
                        )
                    )
                    .setSubText(context.getString(R.string.notificacao_novo_artigo))
                    .setSmallIcon(R.drawable.rounded_space_dashboard_24)
                    .setContentIntent(
                        PendingIntent.getActivity(
                            context,
                            0,
                            Intent(context, MainActivity::class.java).apply {
                                //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                // flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            },
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    .addAction(actionContinuarLendo)
                    .addAction(actionMaisArtigos)

                if (bitmap != null) {
                    builder
                        .setLargeIcon(bitmap)
                        .setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                        )
                }

                with(NotificationManagerCompat.from(context)) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@carregarImagem
                    }
                    notify(NOTIFICATION_ID, builder.build())
                }
            }
        }
    }

    fun cancelarNotificacao(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}