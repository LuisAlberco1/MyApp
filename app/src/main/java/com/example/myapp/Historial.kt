
package com.example.myapp

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val HistVerdeOscuro = Color(0xFF103D32)
private val HistVerde = Color(0xFF237653)
private val HistVerdeClaro = Color(0xFFE6F2EC)
private val HistFondo = Color(0xFFF8FAF9)
private val HistTexto = Color(0xFF18352C)
private val HistSecundario = Color(0xFF738079)
private val HistBlanco = Color.White

private data class EventoCentinela(
    val id: String,
    val titulo: String,
    val detalle: String,
    val timestamp: Long,
    val activa: Boolean
)

@Composable
fun CentinelaHistorial(
    onVolver: () -> Unit = {}
) {
    val eventos = remember {
        mutableStateListOf<EventoCentinela>()
    }

    var cargando by remember { mutableStateOf(true) }
    var errorFirebase by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val referencia = FirebaseDatabase.getInstance(
            "https://appalarma-a4a53-default-rtdb.firebaseio.com/"
        ).reference
            .child("centinela")
            .child("historial")

        val consulta = referencia
            .orderByChild("timestamp")
            .limitToLast(100)

        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                eventos.clear()

                snapshot.children
                    .mapNotNull { elemento ->
                        val titulo = elemento.child("titulo")
                            .getValue(String::class.java)
                            ?: return@mapNotNull null

                        val detalle = elemento.child("detalle")
                            .getValue(String::class.java)
                            ?: ""

                        val timestamp = elemento.child("timestamp")
                            .getValue(Long::class.java)
                            ?: 0L

                        val activa = elemento.child("activa")
                            .getValue(Boolean::class.java)
                            ?: false

                        EventoCentinela(
                            id = elemento.key ?: "",
                            titulo = titulo,
                            detalle = detalle,
                            timestamp = timestamp,
                            activa = activa
                        )
                    }
                    .sortedByDescending { it.timestamp }
                    .forEach { eventos.add(it) }

                cargando = false
                errorFirebase = false
            }

            override fun onCancelled(error: DatabaseError) {
                cargando = false
                errorFirebase = true
            }
        }

        consulta.addValueEventListener(listener)

        onDispose {
            consulta.removeEventListener(listener)
        }
    }

    val formatoHora = remember {
        SimpleDateFormat("HH:mm", Locale("es", "CL"))
    }

    val grupos = eventos.groupBy {
        etiquetaFecha(it.timestamp)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HistFondo)
    ) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HistVerdeOscuro)
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color(0xFF285B4C)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = HistBlanco
                        )
                    }
                }

                Spacer(modifier = Modifier.size(13.dp))

                Column {
                    Text(
                        text = "Alarma de Seguridad",
                        color = HistBlanco,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Seguridad para tu hogar",
                        color = Color(0xFFC5D8D0),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Historial completo",
                color = HistBlanco,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Consulta los eventos de tu sistema de alarma",
                color = Color(0xFFC5D8D0),
                fontSize = 13.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    tint = HistVerde,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.size(9.dp))

                Text(
                    text = "Registro de actividad",
                    color = HistTexto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Eventos ordenados del más reciente al más antiguo",
                color = HistSecundario,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                cargando -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 35.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = HistVerde)
                    }
                }

                errorFirebase -> {
                    Text(
                        text = "No se pudo cargar el historial. Revisa tu conexión y los permisos de Firebase.",
                        color = Color(0xFF9A3F35),
                        fontSize = 13.sp
                    )
                }

                eventos.isEmpty() -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(HistBlanco)
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = HistVerde,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.size(10.dp))

                        Text(
                            text = "Todavía no hay eventos registrados. Cuando conectemos las acciones de la alarma con el historial, aparecerán aquí.",
                            color = HistTexto,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }

                else -> {
                    grupos.forEach { (etiqueta, listaEventos) ->
                        Text(
                            text = etiqueta.uppercase(Locale("es", "CL")),
                            color = HistSecundario,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(HistBlanco)
                                .padding(horizontal = 15.dp)
                        ) {
                            listaEventos.forEachIndexed { indice, evento ->
                                EventoHistorial(
                                    titulo = evento.titulo,
                                    detalle = evento.detalle,
                                    hora = formatoHora.format(
                                        Date(evento.timestamp)
                                    ),
                                    activa = evento.activa
                                )

                                if (indice < listaEventos.lastIndex) {
                                    SeparadorHistorial()
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private fun etiquetaFecha(timestamp: Long): String {
    val hoy = Calendar.getInstance()

    val fechaEvento = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }

    if (mismoDia(hoy, fechaEvento)) {
        return "Hoy"
    }

    hoy.add(Calendar.DAY_OF_YEAR, -1)

    if (mismoDia(hoy, fechaEvento)) {
        return "Ayer"
    }

    return SimpleDateFormat(
        "dd 'de' MMMM yyyy",
        Locale("es", "CL")
    ).format(Date(timestamp))
}

private fun mismoDia(
    primero: Calendar,
    segundo: Calendar
): Boolean {
    return primero.get(Calendar.YEAR) == segundo.get(Calendar.YEAR) &&
            primero.get(Calendar.DAY_OF_YEAR) ==
            segundo.get(Calendar.DAY_OF_YEAR)
}

@Composable
private fun EventoHistorial(
    titulo: String,
    detalle: String,
    hora: String,
    activa: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    if (activa) HistVerdeClaro
                    else Color(0xFFF3EEE5)
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
                tint = if (activa) HistVerde
                else Color(0xFF9A7544),
                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                color = HistTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = detalle,
                color = HistSecundario,
                fontSize = 11.sp
            )
        }

        Text(
            text = hora,
            color = HistSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SeparadorHistorial() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFEDF1EE))
    )
}
