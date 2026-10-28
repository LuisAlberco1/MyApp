package com.example.myapp

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CentinelaMensajeriaServicio : FirebaseMessagingService() {

    override fun onMessageReceived(mensaje: RemoteMessage) {
        super.onMessageReceived(mensaje)

        val titulo = mensaje.notification?.title
            ?: mensaje.data["titulo"]
            ?: "Alerta de seguridad"

        val detalle = mensaje.notification?.body
            ?: mensaje.data["detalle"]
            ?: "Se ha recibido una alerta de tu sistema de alarma."

        Log.d("CentinelaFCM", "Notificación recibida: $titulo")

        CentinelaServicioNotificaciones.mostrar(
            context = this,
            titulo = titulo,
            detalle = detalle
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Registra el token en Logcat para las pruebas.
        Log.d("CentinelaFCM", "Token FCM: $token")
    }
}
