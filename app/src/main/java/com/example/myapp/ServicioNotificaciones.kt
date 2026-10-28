
package com.example.myapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.Normalizer
import java.util.Locale

object CentinelaServicioNotificaciones {

    private const val CANAL_ID = "centinela_alarmas"
    private const val NOTIFICACION_ID = 1001
    private const val PREFERENCIAS = "centinela_ajustes"

    private const val NOTIFICAR_ALARMA = "notificar_alarma"
    private const val NOTIFICAR_ACTUADORES = "notificar_actuadores"
    private const val NOTIFICAR_DESCONEXION = "notificar_desconexion"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Alertas del sistema de alarma",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos sobre el estado del sistema de alarma"
                enableVibration(true)
            }

            val administrador = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            administrador.createNotificationChannel(canal)
        }
    }

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(
            texto.lowercase(Locale.ROOT),
            Normalizer.Form.NFD
        ).replace(Regex("\\p{Mn}+"), "")
    }

    private fun notificacionesPermitidas(
        context: Context,
        titulo: String,
        detalle: String
    ): Boolean {
        val preferencias = context.getSharedPreferences(
            PREFERENCIAS,
            Context.MODE_PRIVATE
        )

        val texto = normalizar("$titulo $detalle")

        val esDesconexion = listOf(
            "desconexion",
            "sin conexion",
            "desconectado",
            "desconectada",
            "perdida de conexion",
            "offline"
        ).any { it in texto }

        val esActuador = listOf(
            "led",
            "buzzer",
            "actuador"
        ).any { it in texto }

        val permitida = when {
            esDesconexion ->
                preferencias.getBoolean(NOTIFICAR_DESCONEXION, true)

            esActuador ->
                preferencias.getBoolean(NOTIFICAR_ACTUADORES, true)

            else ->
                preferencias.getBoolean(NOTIFICAR_ALARMA, true)
        }

        Log.d(
            "Notificaciones",
            "Título: $titulo | Permitida: $permitida | " +
                    "Desconexión: $esDesconexion | Actuador: $esActuador"
        )

        return permitida
    }

    fun mostrar(
        context: Context,
        titulo: String,
        detalle: String
    ) {
        if (!notificacionesPermitidas(context, titulo, detalle)) {
            Log.d(
                "Notificaciones",
                "Alerta omitida por preferencias: $titulo"
            )
            return
        }

        crearCanal(context)

        val notificacion = NotificationCompat.Builder(
            context,
            CANAL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(titulo)
            .setContentText(detalle)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(detalle)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICACION_ID, notificacion)
        } catch (e: SecurityException) {
            Log.e(
                "Notificaciones",
                "No se dispone del permiso para mostrar notificaciones",
                e
            )
        }
    }
}
