package com.example.myapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapp.ui.theme.MyAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {

    private val solicitarPermisoNotificaciones =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CentinelaServicioNotificaciones.crearCanal(this)

        com.google.firebase.messaging.FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    android.util.Log.d(
                        "FCM",
                        "Token de notificaciones obtenido"
                    )
                } else {
                    android.util.Log.e(
                        "FCM",
                        "Error al obtener el token",
                        tarea.exception
                    )
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                solicitarPermisoNotificaciones.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }

        setContent {
            MyAppTheme {
                val autenticacion = remember {
                    FirebaseAuth.getInstance()
                }

                val referenciaEstado = remember {
                    FirebaseDatabase.getInstance(
                        "https://appalarma-a4a53-default-rtdb.firebaseio.com/"
                    ).reference
                        .child("centinela")
                        .child("estado")
                }

                val referenciaHistorial = remember {
                    referenciaEstado.parent!!.child("historial")
                }

                var sesionIniciada by remember {
                    mutableStateOf(autenticacion.currentUser != null)
                }

                var errorInicioSesion by remember {
                    mutableStateOf("")
                }

                // Mensaje para la recuperación de contraseña.
                var mensajeRecuperacion by remember {
                    mutableStateOf("")
                }

                var mostrarHistorial by remember {
                    mutableStateOf(false)
                }

                var mostrarControl by remember {
                    mutableStateOf(false)
                }

                var mostrarControlManual by remember {
                    mutableStateOf(false)
                }

                var mostrarAjustes by remember {
                    mutableStateOf(false)
                }

                var alarmaArmada by remember {
                    mutableStateOf(true)
                }

                var ledEncendido by remember {
                    mutableStateOf(false)
                }

                var buzzerEncendido by remember {
                    mutableStateOf(false)
                }

                var deteccionSimuladaActiva by remember {
                    mutableStateOf(false)
                }

                /*
                 * Inicia el monitoreo cuando hay sesión y lo detiene
                 * al cerrar sesión.
                 */
                LaunchedEffect(sesionIniciada) {
                    val intentMonitoreo = Intent(
                        this@MainActivity,
                        CentinelaMonitoreoServicio::class.java
                    )

                    if (sesionIniciada) {
                        ContextCompat.startForegroundService(
                            this@MainActivity,
                            intentMonitoreo
                        )
                    } else {
                        stopService(intentMonitoreo)
                    }
                }

                fun actualizarEstado(cambios: Map<String, Any>) {
                    referenciaEstado.updateChildren(cambios)
                        .addOnFailureListener { error ->
                            android.util.Log.e(
                                "Firebase",
                                "No se pudo actualizar el estado de la alarma",
                                error
                            )
                        }
                }

                fun registrarEvento(
                    titulo: String,
                    detalle: String,
                    activa: Boolean
                ) {
                    val evento = mapOf(
                        "titulo" to titulo,
                        "detalle" to detalle,
                        "timestamp" to System.currentTimeMillis(),
                        "activa" to activa
                    )

                    referenciaHistorial.push()
                        .setValue(evento)
                        .addOnFailureListener { error ->
                            android.util.Log.e(
                                "Firebase",
                                "No se pudo guardar el historial",
                                error
                            )
                        }
                }

                fun procesarDeteccion() {
                    if (!alarmaArmada || deteccionSimuladaActiva) {
                        return
                    }

                    deteccionSimuladaActiva = true

                    actualizarEstado(
                        mapOf(
                            "alarmaArmada" to true,
                            "ledEncendido" to true,
                            "buzzerEncendido" to true
                        )
                    )

                    registrarEvento(
                        titulo = "Intrusión detectada",
                        detalle = "Se simuló una detección con la alarma armada.",
                        activa = true
                    )
                }

                DisposableEffect(sesionIniciada, referenciaEstado) {
                    if (!sesionIniciada) {
                        onDispose { }
                    } else {
                        val listener = object : ValueEventListener {

                            override fun onDataChange(snapshot: DataSnapshot) {
                                alarmaArmada =
                                    snapshot.child("alarmaArmada")
                                        .getValue(Boolean::class.java) ?: true

                                ledEncendido =
                                    snapshot.child("ledEncendido")
                                        .getValue(Boolean::class.java) ?: false

                                buzzerEncendido =
                                    snapshot.child("buzzerEncendido")
                                        .getValue(Boolean::class.java) ?: false

                                if (!alarmaArmada) {
                                    mostrarControlManual = false
                                    deteccionSimuladaActiva = false
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                android.util.Log.e(
                                    "Firebase",
                                    "No se pudo leer el estado de la alarma",
                                    error.toException()
                                )
                            }
                        }

                        referenciaEstado.runTransaction(
                            object : Transaction.Handler {

                                override fun doTransaction(
                                    currentData: MutableData
                                ): Transaction.Result {
                                    if (currentData.value == null) {
                                        currentData.child("alarmaArmada").value = true
                                        currentData.child("ledEncendido").value = false
                                        currentData.child("buzzerEncendido").value = false
                                    }

                                    return Transaction.success(currentData)
                                }

                                override fun onComplete(
                                    error: DatabaseError?,
                                    committed: Boolean,
                                    currentData: DataSnapshot?
                                ) {
                                    if (error != null) {
                                        android.util.Log.e(
                                            "Firebase",
                                            "Error al inicializar el estado",
                                            error.toException()
                                        )
                                    }
                                }
                            }
                        )

                        referenciaEstado.addValueEventListener(listener)

                        onDispose {
                            referenciaEstado.removeEventListener(listener)
                        }
                    }
                }

                fun desarmarAlarma() {
                    actualizarEstado(
                        mapOf(
                            "alarmaArmada" to false,
                            "ledEncendido" to false,
                            "buzzerEncendido" to false
                        )
                    )

                    registrarEvento(
                        titulo = "Alarma desactivada",
                        detalle = "Desde la aplicación",
                        activa = false
                    )

                    mostrarControlManual = false
                    deteccionSimuladaActiva = false
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when {
                            !sesionIniciada -> {
                                CentinelaInicioDeSesion(
                                    onErrorInicioSesion = errorInicioSesion,
                                    mensajeRecuperacion = mensajeRecuperacion,

                                    onRecuperarContrasena = { correo ->
                                        errorInicioSesion = ""
                                        mensajeRecuperacion = ""

                                        val correoLimpio = correo.trim()

                                        when {
                                            correoLimpio.isBlank() -> {
                                                errorInicioSesion =
                                                    "Introduce tu correo para recuperar la contraseña."
                                            }

                                            !android.util.Patterns.EMAIL_ADDRESS
                                                .matcher(correoLimpio)
                                                .matches() -> {
                                                errorInicioSesion =
                                                    "Introduce un correo electrónico válido."
                                            }

                                            else -> {
                                                autenticacion
                                                    .sendPasswordResetEmail(correoLimpio)
                                                    .addOnCompleteListener { tarea ->
                                                        if (tarea.isSuccessful) {
                                                            mensajeRecuperacion =
                                                                "Si la cuenta existe, recibirás un correo con las instrucciones para recuperar tu contraseña."
                                                        } else {
                                                            mensajeRecuperacion =
                                                                "No se pudo procesar la solicitud. Revisa tu conexión e inténtalo nuevamente."

                                                            android.util.Log.e(
                                                                "Firebase",
                                                                "Error al solicitar recuperación de contraseña",
                                                                tarea.exception
                                                            )
                                                        }
                                                    }
                                            }
                                        }
                                    },

                                    onIniciarSesion = { correo, contrasena ->
                                        errorInicioSesion = ""
                                        mensajeRecuperacion = ""

                                        autenticacion
                                            .signInWithEmailAndPassword(
                                                correo.trim(),
                                                contrasena
                                            )
                                            .addOnCompleteListener { tarea ->
                                                if (tarea.isSuccessful) {
                                                    sesionIniciada = true
                                                    errorInicioSesion = ""
                                                    mensajeRecuperacion = ""
                                                    mostrarHistorial = false
                                                    mostrarControl = false
                                                    mostrarControlManual = false
                                                    mostrarAjustes = false
                                                } else {
                                                    errorInicioSesion =
                                                        when (tarea.exception) {
                                                            is com.google.firebase.auth.FirebaseAuthInvalidUserException,
                                                            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                                                                "Correo o contraseña incorrectos."

                                                            else ->
                                                                "No se pudo iniciar sesión. Revisa tu conexión e inténtalo nuevamente."
                                                        }
                                                }
                                            }
                                    }
                                )
                            }

                            mostrarAjustes -> {
                                CentinelaAjustesGenerales(
                                    onVolver = {
                                        mostrarAjustes = false
                                    }
                                )
                            }

                            mostrarHistorial -> {
                                CentinelaHistorial(
                                    onVolver = {
                                        mostrarHistorial = false
                                    }
                                )
                            }

                            mostrarControlManual && alarmaArmada -> {
                                CentinelaControlManual(
                                    alarmaArmada = alarmaArmada,
                                    ledEncendido = ledEncendido,
                                    buzzerEncendido = buzzerEncendido,

                                    onCambiarAlarma = { encender ->
                                        if (alarmaArmada) {
                                            actualizarEstado(
                                                mapOf(
                                                    "ledEncendido" to encender,
                                                    "buzzerEncendido" to encender
                                                )
                                            )

                                            registrarEvento(
                                                titulo = if (encender) {
                                                    "Alarma activada"
                                                } else {
                                                    "Alarma desactivada"
                                                },
                                                detalle = "Control manual desde la aplicación",
                                                activa = encender
                                            )
                                        }
                                    },

                                    onVolver = {
                                        mostrarControlManual = false
                                    }
                                )
                            }

                            mostrarControl -> {
                                CentinelaControlarDispositivo(
                                    alarmaArmada = alarmaArmada,

                                    onArmarAlarma = {
                                        if (!alarmaArmada) {
                                            actualizarEstado(
                                                mapOf(
                                                    "alarmaArmada" to true,
                                                    "ledEncendido" to false,
                                                    "buzzerEncendido" to false
                                                )
                                            )

                                            deteccionSimuladaActiva = false

                                            registrarEvento(
                                                titulo = "Alarma activada",
                                                detalle = "Desde la aplicación",
                                                activa = true
                                            )
                                        }
                                    },

                                    onDesarmarAlarma = {
                                        if (alarmaArmada) {
                                            desarmarAlarma()
                                        }
                                    },

                                    onControlManual = {
                                        if (alarmaArmada) {
                                            mostrarControlManual = true
                                        }
                                    },

                                    onVolver = {
                                        mostrarControl = false
                                    }
                                )
                            }

                            else -> {
                                CentinelaEstadoDeLaAlarma(
                                    alarmaArmada = alarmaArmada,

                                    onCerrarSesion = {
                                        autenticacion.signOut()
                                        sesionIniciada = false
                                        errorInicioSesion = ""
                                        mensajeRecuperacion = ""
                                        mostrarHistorial = false
                                        mostrarControl = false
                                        mostrarControlManual = false
                                        mostrarAjustes = false
                                        deteccionSimuladaActiva = false
                                    },

                                    onControlarDispositivo = {
                                        mostrarControl = true
                                    },

                                    onVerHistorial = {
                                        mostrarHistorial = true
                                    },

                                    onAbrirAjustes = {
                                        mostrarAjustes = true
                                    }
                                )
                            }
                        }
                    }

                    if (
                        sesionIniciada &&
                        mostrarControl &&
                        !mostrarControlManual &&
                        !mostrarHistorial &&
                        !mostrarAjustes
                    ) {
                        Button(
                            onClick = {
                                procesarDeteccion()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp),
                            enabled = alarmaArmada && !deteccionSimuladaActiva
                        ) {
                            Text(
                                text = when {
                                    !alarmaArmada -> "Alarma desarmada"
                                    deteccionSimuladaActiva -> "Detección simulada"
                                    else -> "Simular detección"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
