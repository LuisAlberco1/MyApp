
package com.example.myapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val URL_FIREBASE =
    "https://appalarma-a4a53-default-rtdb.firebaseio.com/"

private val VerdeCentinela = Color(0xFF103D32)
private val FondoCentinela = Color(0xFFF8FAF9)
private val TextoCentinela = Color(0xFF172D29)
private val TextoSecundario = Color(0xFF62726E)
private val BordeCentinela = Color(0xFFDCE5E0)

@Composable
fun CentinelaAjustesGenerales(
    onVolver: () -> Unit = {}
) {
    val context = LocalContext.current

    val preferencias = remember {
        context.getSharedPreferences("centinela_ajustes", 0)
    }

    val referenciaBase = remember {
        FirebaseDatabase.getInstance(URL_FIREBASE).reference
    }

    var conectado by remember { mutableStateOf<Boolean?>(null) }
    var alarmaArmada by remember { mutableStateOf(false) }
    var ledEncendido by remember { mutableStateOf(false) }
    var buzzerEncendido by remember { mutableStateOf(false) }

    var mostrarConexion by remember { mutableStateOf(false) }
    var mostrarHogar by remember { mutableStateOf(false) }
    var mostrarNotificaciones by remember { mutableStateOf(false) }
    var mostrarAyuda by remember { mutableStateOf(false) }
    var mostrarBiometria by remember { mutableStateOf(false) }

    var notificarAlarma by remember {
        mutableStateOf(
            preferencias.getBoolean("notificar_alarma", true)
        )
    }

    var notificarDesconexion by remember {
        mutableStateOf(
            preferencias.getBoolean("notificar_desconexion", true)
        )
    }

    var enviandoCorreo by remember { mutableStateOf(false) }

    // Comprueba la conexión con Firebase.
    DisposableEffect(referenciaBase) {
        val referenciaConexion = referenciaBase.child(".info/connected")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                conectado =
                    snapshot.getValue(Boolean::class.java) == true
            }

            override fun onCancelled(error: DatabaseError) {
                conectado = false
            }
        }

        referenciaConexion.addValueEventListener(listener)

        onDispose {
            referenciaConexion.removeEventListener(listener)
        }
    }

    // Escucha el estado compartido de la alarma, LED y buzzer.
    DisposableEffect(referenciaBase) {
        val referenciaEstado = referenciaBase
            .child("centinela")
            .child("estado")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                alarmaArmada =
                    snapshot.child("alarmaArmada")
                        .getValue(Boolean::class.java) ?: false

                ledEncendido =
                    snapshot.child("ledEncendido")
                        .getValue(Boolean::class.java) ?: false

                buzzerEncendido =
                    snapshot.child("buzzerEncendido")
                        .getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "No se pudo leer el estado de Firebase",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        referenciaEstado.addValueEventListener(listener)

        onDispose {
            referenciaEstado.removeEventListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCentinela)
    ) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeCentinela)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SISTEMA DE ALARMA",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Seguridad para tu hogar",
                        color = Color(0xFFC0D8CE),
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes generales",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, top = 22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Ajustes generales",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Mi hogar",
                color = TextoCentinela,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Configura tu conexión, seguridad y acceso.",
                color = TextoSecundario,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Conexión y hogar",
                color = TextoCentinela,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsCard {
                SettingsRow(
                    icono = Icons.Default.Wifi,
                    titulo = "Tipo de conexión",
                    descripcion = when (conectado) {
                        true -> "Firebase · Conectado"
                        false -> "Firebase · Sin conexión"
                        null -> "Comprobando conexión..."
                    },
                    onClick = { mostrarConexion = true }
                )

                SettingsDivider()

                SettingsRow(
                    icono = Icons.Default.Devices,
                    titulo = "Hogar y dispositivos",
                    descripcion = "Consultar el estado del sistema",
                    onClick = { mostrarHogar = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Seguridad y acceso",
                color = TextoCentinela,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsCard {
                SettingsRow(
                    icono = Icons.Default.Notifications,
                    titulo = "Notificaciones de seguridad",
                    descripcion = "Configurar preferencias de alertas",
                    onClick = { mostrarNotificaciones = true }
                )

                SettingsDivider()

                SettingsRow(
                    icono = Icons.Default.Lock,
                    titulo = "Cambiar contraseña",
                    descripcion = "Recibir un correo de recuperación",
                    onClick = {
                        val usuario = FirebaseAuth.getInstance().currentUser
                        val correo = usuario?.email

                        if (correo.isNullOrBlank()) {
                            Toast.makeText(
                                context,
                                "No se encontró el correo de la cuenta",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            enviandoCorreo = true

                            FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(correo)
                                .addOnCompleteListener { tarea ->
                                    enviandoCorreo = false

                                    val mensaje = if (tarea.isSuccessful) {
                                        "Si la cuenta existe, recibirás un correo para restablecer la contraseña."
                                    } else {
                                        "No se pudo enviar el correo. Revisa tu conexión e inténtalo nuevamente."
                                    }

                                    Toast.makeText(
                                        context,
                                        mensaje,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                )

                SettingsDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp)
                ) {
                    SettingsIcon(Icons.Default.Security)

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Acceso biométrico",
                            color = TextoCentinela,
                            fontSize = 15.sp
                        )

                        Text(
                            text = "Disponible en una futura versión",
                            color = TextoSecundario,
                            fontSize = 12.sp
                        )
                    }

                    TextButton(onClick = { mostrarBiometria = true }) {
                        Text("Info", color = VerdeCentinela)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ayuda",
                color = TextoCentinela,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            SettingsCard {
                SettingsRow(
                    icono = Icons.Default.HelpOutline,
                    titulo = "Ayuda y soporte",
                    descripcion = "Guía para utilizar el sistema",
                    onClick = { mostrarAyuda = true }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Tu hogar, en buenas manos.",
                color = TextoSecundario,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }

    // Ventana de conexión
    if (mostrarConexion) {
        VentanaInformacion(
            titulo = "Estado de conexión",
            contenido = when (conectado) {
                true -> "La aplicación está conectada a Firebase y puede sincronizar los datos."
                false -> "Firebase no informa una conexión activa. Revisa el Wi-Fi o los datos móviles."
                null -> "La aplicación está comprobando la conexión con Firebase."
            },
            onCerrar = { mostrarConexion = false }
        )
    }

    // Ventana de estado del hogar
    if (mostrarHogar) {
        AlertDialog(
            onDismissRequest = { mostrarHogar = false },
            title = { Text("Hogar y dispositivos") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EstadoDispositivo(
                        "Alarma",
                        if (alarmaArmada) "Activada" else "Desactivada"
                    )

                    EstadoDispositivo(
                        "LED",
                        if (ledEncendido) "Encendido" else "Apagado"
                    )

                    EstadoDispositivo(
                        "Buzzer",
                        if (buzzerEncendido) "Encendido" else "Apagado"
                    )

                    Text(
                        text = "Estos valores se leen de Firebase y se actualizan cuando cambia el estado compartido.",
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarHogar = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Preferencias locales de notificación
    if (mostrarNotificaciones) {
        AlertDialog(
            onDismissRequest = { mostrarNotificaciones = false },
            title = { Text("Notificaciones de seguridad") },
            text = {
                Column {
                    Text(
                        text = "Elige qué alertas quieres recibir en este teléfono.",
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PreferenciaNotificacion(
                        titulo = "Alertas de seguridad",
                        descripcion = "Intrusiones detectadas y activaciones manuales.",
                        marcada = notificarAlarma,
                        onCambiar = {
                            notificarAlarma = it
                            preferencias.edit()
                                .putBoolean("notificar_alarma", it)
                                .apply()
                        }
                    )

                    PreferenciaNotificacion(
                        titulo = "Avisos de desconexión",
                        descripcion = "Alertas cuando se detecte una pérdida de conexión.",
                        marcada = notificarDesconexion,
                        onCambiar = {
                            notificarDesconexion = it
                            preferencias.edit()
                                .putBoolean("notificar_desconexion", it)
                                .apply()
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarNotificaciones = false }) {
                    Text("Listo")
                }
            }
        )
    }

    // Ventana de ayuda
    if (mostrarAyuda) {
        VentanaInformacion(
            titulo = "Ayuda y soporte",
            contenido = "• Activa o desactiva la alarma desde la pantalla principal.\n\n" +
                    "• Entra en Controlar dispositivo para manejar el LED y el buzzer cuando corresponda.\n\n" +
                    "• Consulta el historial para revisar los eventos registrados.\n\n" +
                    "• Los cambios de estado se comparten mediante Firebase cuando hay conexión.\n\n" +
                    "• Si no puedes acceder a tu cuenta, utiliza Cambiar contraseña para recibir un correo de recuperación.",
            onCerrar = { mostrarAyuda = false }
        )
    }

    // Información de acceso biométrico
    if (mostrarBiometria) {
        VentanaInformacion(
            titulo = "Acceso biométrico",
            contenido = "Esta función todavía no está implementada. La huella y el reconocimiento facial no están activados en esta versión.",
            onCerrar = { mostrarBiometria = false }
        )
    }

    // Indicador de envío del correo de recuperación
    if (enviandoCorreo) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Enviando correo") },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = VerdeCentinela
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text("Solicitando el correo de recuperación...")
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun EstadoDispositivo(
    nombre: String,
    estado: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(nombre, color = TextoCentinela)

        Text(
            estado,
            color = VerdeCentinela,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PreferenciaNotificacion(
    titulo: String,
    descripcion: String,
    marcada: Boolean,
    onCambiar: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCambiar(!marcada) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = titulo,
                color = TextoCentinela,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = descripcion,
                color = TextoSecundario,
                fontSize = 12.sp
            )
        }

        Switch(
            checked = marcada,
            onCheckedChange = onCambiar,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VerdeCentinela
            )
        )
    }
}

@Composable
private fun VentanaInformacion(
    titulo: String,
    contenido: String,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(titulo) },
        text = { Text(contenido) },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun SettingsCard(
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = BordeCentinela,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp),
        content = contenido
    )
}

@Composable
private fun SettingsRow(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        SettingsIcon(icono)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = titulo,
                color = TextoCentinela,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descripcion,
                color = TextoSecundario,
                fontSize = 12.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Abrir $titulo",
            tint = TextoSecundario
        )
    }
}

@Composable
private fun SettingsIcon(icono: ImageVector) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(end = 12.dp)
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEAF2EE))
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = VerdeCentinela,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(color = BordeCentinela)
}
