
package com.example.myapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CentinelaMonitoreoServicio : Service() {

    companion object {
        private const val TAG = "MonitoreoAlarma"
        private const val CANAL_MONITOREO = "centinela_monitoreo"
        private const val ID_NOTIFICACION_SERVICIO = 1002

        const val ACCION_INICIAR =
            "com.example.myapp.INICIAR_MONITOREO"

        const val ACCION_DETENER =
            "com.example.myapp.DETENER_MONITOREO"
    }

    private val referenciaRaiz = FirebaseDatabase.getInstance(
        "https://appalarma-a4a53-default-rtdb.firebaseio.com/"
    ).reference

    private val referenciaBase = referenciaRaiz.child("centinela")
    private val referenciaHistorial = referenciaBase.child("historial")
    private val referenciaEstado =
        referenciaBase.child("estado").child("alarmaArmada")

    private var escuchadorHistorial: ChildEventListener? = null
    private var escuchadorEstado: ValueEventListener? = null
    private var escuchadorConexionFirebase: ValueEventListener? = null

    private var horaInicioServicio = 0L
    private var alarmaArmada = false

    private lateinit var administradorRed: ConnectivityManager
    private var callbackRed: ConnectivityManager.NetworkCallback? = null

    private var internetDisponible: Boolean? = null
    private var firebaseConectado: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        crearCanalMonitoreo()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        if (intent?.action == ACCION_DETENER) {
            detenerMonitoreo()
            stopSelf()
            return START_NOT_STICKY
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            Log.w(TAG, "No hay sesión iniciada. Se detiene el monitoreo.")
            detenerMonitoreo()
            stopSelf()
            return START_NOT_STICKY
        }

        iniciarServicioEnPrimerPlano()

        if (escuchadorEstado == null) {
            iniciarEscuchaEstado()
        }

        if (escuchadorHistorial == null) {
            iniciarEscuchaHistorial()
        }

        if (callbackRed == null) {
            iniciarMonitoreoInternet()
        }

        if (escuchadorConexionFirebase == null) {
            iniciarMonitoreoFirebase()
        }

        return START_STICKY
    }

    private fun iniciarServicioEnPrimerPlano() {
        val notificacion = crearNotificacionPermanente()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                ID_NOTIFICACION_SERVICIO,
                notificacion,
                android.content.pm.ServiceInfo
                    .FOREGROUND_SERVICE_TYPE_MANIFEST
            )
        } else {
            startForeground(
                ID_NOTIFICACION_SERVICIO,
                notificacion
            )
        }
    }

    private fun iniciarMonitoreoInternet() {
        administradorRed =
            getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        fun comprobarInternet() {
            val red = administradorRed.activeNetwork
            val capacidades = red?.let {
                administradorRed.getNetworkCapabilities(it)
            }

            val disponible = capacidades?.let {
                it.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) && it.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
            } ?: false

            if (internetDisponible != disponible) {
                val anterior = internetDisponible
                internetDisponible = disponible

                Log.d(TAG, "Internet disponible: $disponible")

                if (anterior != null && !disponible) {
                    CentinelaServicioNotificaciones.mostrar(
                        applicationContext,
                        "Desconexión de Internet",
                        "El celular perdió el acceso a Internet."
                    )
                } else if (anterior == false && disponible) {
                    CentinelaServicioNotificaciones.mostrar(
                        applicationContext,
                        "Internet restablecido",
                        "El celular recuperó el acceso a Internet."
                    )
                }
            }
        }

        callbackRed = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                comprobarInternet()
            }

            override fun onLost(network: Network) {
                comprobarInternet()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                comprobarInternet()
            }
        }

        try {
            administradorRed.registerDefaultNetworkCallback(callbackRed!!)
            comprobarInternet()
        } catch (e: Exception) {
            callbackRed = null
            Log.e(TAG, "No se pudo iniciar el monitoreo de red", e)
        }
    }

    private fun iniciarMonitoreoFirebase() {
        val referenciaConexion = referenciaRaiz.child(".info/connected")

        escuchadorConexionFirebase = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val conectado =
                    snapshot.getValue(Boolean::class.java) == true

                if (firebaseConectado != conectado) {
                    val anterior = firebaseConectado
                    firebaseConectado = conectado

                    Log.d(TAG, "Firebase conectado: $conectado")

                    if (anterior != null && !conectado) {
                        CentinelaServicioNotificaciones.mostrar(
                            applicationContext,
                            "Desconexión de Firebase",
                            "Se perdió la conexión entre la aplicación y Firebase."
                        )
                    } else if (anterior == false && conectado) {
                        CentinelaServicioNotificaciones.mostrar(
                            applicationContext,
                            "Firebase reconectado",
                            "La conexión con Firebase se ha recuperado."
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    TAG,
                    "Error comprobando la conexión con Firebase: ${error.message}"
                )
            }
        }

        referenciaConexion.addValueEventListener(
            escuchadorConexionFirebase!!
        )
    }

    private fun iniciarEscuchaEstado() {
        escuchadorEstado = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                alarmaArmada =
                    snapshot.getValue(Boolean::class.java) ?: false

                Log.d(
                    TAG,
                    "Estado recibido de Firebase: alarmaArmada=$alarmaArmada"
                )

                actualizarNotificacionSegunEstado()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    TAG,
                    "Error al consultar el estado de la alarma: ${error.message}"
                )
            }
        }

        referenciaEstado.addValueEventListener(escuchadorEstado!!)
    }

    private fun actualizarNotificacionSegunEstado() {
        if (alarmaArmada) {
            Log.d(TAG, "Alarma armada: mostrar notificación permanente")
            iniciarServicioEnPrimerPlano()
        } else {
            Log.d(TAG, "Alarma desarmada: retirar notificación permanente")
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    private fun iniciarEscuchaHistorial() {
        horaInicioServicio = System.currentTimeMillis()

        escuchadorHistorial = object : ChildEventListener {

            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                val timestamp = snapshot.child("timestamp")
                    .getValue(Long::class.java) ?: return

                if (timestamp < horaInicioServicio) return

                val titulo = snapshot.child("titulo")
                    .getValue(String::class.java) ?: return

                val detalle = snapshot.child("detalle")
                    .getValue(String::class.java) ?: return

                val activa = snapshot.child("activa")
                    .getValue(Boolean::class.java) ?: false

                val esActivacionManual =
                    titulo == "Alarma activada" &&
                            detalle == "Control manual desde la aplicación" &&
                            activa

                val esSimulacion =
                    titulo == "Intrusión detectada" &&
                            detalle == "Se simuló una detección con la alarma armada." &&
                            activa

                when {
                    esActivacionManual -> {
                        Log.d(TAG, "Activación manual detectada")

                        CentinelaServicioNotificaciones.mostrar(
                            applicationContext,
                            "Alarma activada manualmente",
                            "La alarma se ha activado desde el control manual."
                        )
                    }

                    esSimulacion -> {
                        Log.d(TAG, "Simulación de intrusión detectada")

                        CentinelaServicioNotificaciones.mostrar(
                            applicationContext,
                            "¡Intrusión detectada!",
                            "Se ha simulado una detección con la alarma armada."
                        )
                    }

                    else -> {
                        Log.d(TAG, "Evento ignorado: $titulo")
                    }
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) = Unit

            override fun onChildRemoved(snapshot: DataSnapshot) = Unit

            override fun onChildMoved(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) = Unit

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    TAG,
                    "Error escuchando el historial: ${error.message}"
                )
            }
        }

        referenciaHistorial.addChildEventListener(escuchadorHistorial!!)
    }

    private fun crearCanalMonitoreo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_MONITOREO,
                "Monitoreo del sistema de alarma",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description =
                    "Indica que el sistema está monitoreando los eventos de la alarma."
                setShowBadge(false)
            }

            val administrador =
                getSystemService(NotificationManager::class.java)

            administrador.createNotificationChannel(canal)
        }
    }

    private fun crearNotificacionPermanente(): Notification {
        return NotificationCompat.Builder(this, CANAL_MONITOREO)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("Sistema de alarma activo")
            .setContentText("Monitoreando los eventos de la alarma")
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun detenerMonitoreo() {
        escuchadorHistorial?.let {
            referenciaHistorial.removeEventListener(it)
        }
        escuchadorHistorial = null

        escuchadorEstado?.let {
            referenciaEstado.removeEventListener(it)
        }
        escuchadorEstado = null

        escuchadorConexionFirebase?.let {
            referenciaRaiz.child(".info/connected")
                .removeEventListener(it)
        }
        escuchadorConexionFirebase = null

        callbackRed?.let {
            try {
                administradorRed.unregisterNetworkCallback(it)
            } catch (e: Exception) {
                Log.w(TAG, "El monitoreo de red ya estaba detenido", e)
            }
        }
        callbackRed = null

        internetDisponible = null
        firebaseConectado = null

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        detenerMonitoreo()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
