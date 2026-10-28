
package com.example.myapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val VerdeOscuro = Color(0xFF103D32)
private val VerdePrincipal = Color(0xFF237653)
private val VerdeClaro = Color(0xFFE6F2EC)
private val FondoPantalla = Color(0xFFF8FAF9)
private val TextoPrincipal = Color(0xFF18352C)
private val TextoSecundario = Color(0xFF738079)
private val Blanco = Color.White
private val RojoAlarma = Color(0xFFC62828)
private val FondoRojo = Color(0xFFFFE7E7)

private data class ActividadCentinela(
    val id: String,
    val titulo: String,
    val detalle: String,
    val timestamp: Long,
    val activa: Boolean
)

@Composable
fun CentinelaEstadoDeLaAlarma(
    alarmaArmada: Boolean,
    onCerrarSesion: () -> Unit = {},
    onControlarDispositivo: () -> Unit = {},
    onVerHistorial: () -> Unit = {},
    onAbrirAjustes: () -> Unit = {}
) {
    val colorEstado = if (alarmaArmada) VerdePrincipal else RojoAlarma
    val fondoEstado = if (alarmaArmada) VerdeClaro else FondoRojo

    // Lista de eventos recientes obtenidos desde Firebase.
    val actividades = remember {
        mutableStateListOf<ActividadCentinela>()
    }

    val referenciaHistorial = remember {
        FirebaseDatabase.getInstance(
            "https://appalarma-a4a53-default-rtdb.firebaseio.com/"
        ).reference.child("centinela").child("historial")
    }

    // Escucha los cambios del historial en tiempo real.
    DisposableEffect(referenciaHistorial) {
        val consulta = referenciaHistorial
            .orderByChild("timestamp")
            .limitToLast(3)

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val nuevosEventos = snapshot.children.mapNotNull { evento ->
                    val timestamp = evento.child("timestamp")
                        .getValue(Long::class.java)

                    val titulo = evento.child("titulo")
                        .getValue(String::class.java)

                    if (timestamp == null || titulo == null) {
                        null
                    } else {
                        ActividadCentinela(
                            id = evento.key ?: "",
                            titulo = titulo,
                            detalle = evento.child("detalle")
                                .getValue(String::class.java) ?: "",
                            timestamp = timestamp,
                            activa = evento.child("activa")
                                .getValue(Boolean::class.java) ?: false
                        )
                    }
                }.sortedByDescending { it.timestamp }

                actividades.clear()
                actividades.addAll(nuevosEventos)
            }

            override fun onCancelled(error: DatabaseError) {
                // Si Firebase cancela la lectura, se conserva la pantalla.
            }
        }

        consulta.addValueEventListener(listener)

        onDispose {
            consulta.removeEventListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPantalla)
    ) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeOscuro)
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "9:41",
                    color = Blanco,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = Blanco,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "100%",
                        color = Blanco,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFF285B4C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = "Logo de Centinela",
                        tint = Blanco,
                        modifier = Modifier.size(29.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SISTEMA DE ALARMA",
                        color = Blanco,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Seguridad para tu hogar",
                        color = Color(0xFFC5D8D0),
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCerrarSesion() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = Blanco,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Salir",
                        color = Blanco,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Estado de la alarma",
                color = Blanco,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Revisa la seguridad de tu hogar",
                color = Color(0xFFC5D8D0),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Contenido desplazable
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 22.dp, bottom = 24.dp)
        ) {
            // Encabezado del hogar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(VerdeClaro),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Mi hogar",
                            tint = VerdePrincipal,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(11.dp))

                    Column {
                        Text(
                            text = "Mi hogar",
                            color = TextoPrincipal,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sistema de seguridad",
                            color = TextoSecundario,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(VerdeClaro)
                        .padding(horizontal = 11.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(VerdePrincipal)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "En línea",
                        color = VerdePrincipal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Ajustes generales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Blanco)
                    .clickable { onAbrirAjustes() }
                    .padding(horizontal = 15.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VerdeClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Ajustes generales",
                        tint = VerdePrincipal,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ajustes generales",
                        color = TextoPrincipal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Configura tu sistema de seguridad",
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Abrir ajustes",
                    tint = VerdePrincipal,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Tarjeta del estado actual
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(fondoEstado)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ESTADO ACTUAL",
                        color = colorEstado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Icon(
                        imageVector = if (alarmaArmada) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Filled.AlarmOff
                        },
                        contentDescription = null,
                        tint = colorEstado,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Blanco),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (alarmaArmada) {
                                Icons.Filled.AlarmOn
                            } else {
                                Icons.Filled.AlarmOff
                            },
                            contentDescription = null,
                            tint = colorEstado,
                            modifier = Modifier.size(33.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (alarmaArmada) {
                                "Alarma armada"
                            } else {
                                "Alarma desarmada"
                            },
                            color = TextoPrincipal,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (alarmaArmada) {
                                "Tu hogar está bajo vigilancia."
                            } else {
                                "La vigilancia está desactivada."
                            },
                            color = Color(0xFF49685A),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (alarmaArmada) {
                                Color(0xFFD5E9DD)
                            } else {
                                Color(0xFFFFDADA)
                            }
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (alarmaArmada) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Filled.AlarmOff
                        },
                        contentDescription = null,
                        tint = colorEstado,
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(modifier = Modifier.width(9.dp))

                    Text(
                        text = if (alarmaArmada) {
                            "Vigilancia habilitada. Sensores activos."
                        } else {
                            "Vigilancia desactivada. No se generan alertas."
                        },
                        color = if (alarmaArmada) {
                            Color(0xFF315D48)
                        } else {
                            Color(0xFF9A3030)
                        },
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Estado actualizado en la aplicación",
                    color = TextoSecundario,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Actividad reciente desde Firebase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Actividad reciente",
                    color = TextoPrincipal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Últimos eventos",
                    color = TextoSecundario,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Blanco)
                    .padding(horizontal = 15.dp)
            ) {
                if (actividades.isEmpty()) {
                    Text(
                        text = "Todavía no hay eventos registrados.",
                        color = TextoSecundario,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(
                            vertical = 20.dp,
                            horizontal = 4.dp
                        )
                    )
                } else {
                    actividades.forEachIndexed { index, actividad ->
                        ElementoActividad(
                            titulo = actividad.titulo,
                            detalle = actividad.detalle,
                            hora = formatearHora(actividad.timestamp),
                            activa = actividad.activa
                        )

                        if (index < actividades.lastIndex) {
                            SeparadorActividad()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onVerHistorial() }
                        .padding(vertical = 13.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "Ver historial",
                        tint = VerdePrincipal,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Ver historial completo",
                        color = VerdePrincipal,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de control del dispositivo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VerdeOscuro)
                    .clickable { onControlarDispositivo() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF285B4C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Alarm,
                        contentDescription = "Controlar alarma",
                        tint = Blanco,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(13.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Controlar dispositivo",
                        color = Blanco,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Administra tu sistema de alarma",
                        color = Color(0xFFC5D8D0),
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Continuar",
                    tint = Blanco,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Gestiona tu alarma en la siguiente pantalla.",
                color = TextoSecundario,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private fun formatearHora(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault())
        .format(Date(timestamp))
}

@Composable
private fun ElementoActividad(
    titulo: String,
    detalle: String,
    hora: String,
    activa: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (activa) VerdeClaro else Color(0xFFF3EEE5)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (activa) {
                    Icons.Filled.AlarmOn
                } else {
                    Icons.Filled.AlarmOff
                },
                contentDescription = titulo,
                tint = if (activa) {
                    VerdePrincipal
                } else {
                    Color(0xFF9A7544)
                },
                modifier = Modifier.size(21.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                color = TextoPrincipal,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = detalle,
                color = TextoSecundario,
                fontSize = 11.sp
            )
        }

        Text(
            text = hora,
            color = TextoSecundario,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SeparadorActividad() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFEDF1EE))
    )
}
