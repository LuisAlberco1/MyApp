
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeOscuro = Color(0xFF103D32)
private val VerdePrincipal = Color(0xFF237653)
private val VerdeClaro = Color(0xFFE6F2EC)
private val FondoPantalla = Color(0xFFF5F8F6)
private val TextoPrincipal = Color(0xFF18352C)
private val TextoSecundario = Color(0xFF738079)
private val Blanco = Color.White
private val RojoAlarma = Color(0xFFC62828)
private val FondoRojo = Color(0xFFFFE7E7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentinelaControlarDispositivo(
    alarmaArmada: Boolean,
    onArmarAlarma: () -> Unit,
    onDesarmarAlarma: () -> Unit,
    onControlManual: () -> Unit,
    onVolver: () -> Unit = {}
) {
    Scaffold(
        containerColor = FondoPantalla,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Control del sistema",
                            color = Blanco,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Administra la seguridad de tu hogar",
                            color = Color(0xFFC5D8D0),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeOscuro
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Encabezado del hogar
            Column {
                Text(
                    text = "Mi hogar",
                    color = TextoPrincipal,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Gestiona el estado de tu alarma y sus controles.",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }

            // Tarjeta principal de estado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (alarmaArmada) VerdeOscuro else Color(0xFF612E30)
                    )
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ESTADO DE SEGURIDAD",
                            color = Color(0xFFC5D8D0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (alarmaArmada) {
                                "Alarma armada"
                            } else {
                                "Alarma desarmada"
                            },
                            color = Blanco,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = if (alarmaArmada) {
                                "Tu hogar está bajo vigilancia."
                            } else {
                                "La vigilancia se encuentra desactivada."
                            },
                            color = Color(0xFFE0E9E4),
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(
                                if (alarmaArmada) {
                                    Color(0xFF285B4C)
                                } else {
                                    Color(0xFF803D3D)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (alarmaArmada) {
                                Icons.Default.Shield
                            } else {
                                Icons.Default.ShieldMoon
                            },
                            contentDescription = null,
                            tint = Blanco,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (alarmaArmada) {
                        Color(0xFF285B4C)
                    } else {
                        Color(0xFF803D3D)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 13.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (alarmaArmada) {
                                Icons.Default.CheckCircle
                            } else {
                                Icons.Default.AlarmOff
                            },
                            contentDescription = null,
                            tint = Blanco,
                            modifier = Modifier.size(19.dp)
                        )

                        Spacer(modifier = Modifier.width(9.dp))

                        Text(
                            text = if (alarmaArmada) {
                                "Vigilancia habilitada"
                            } else {
                                "Vigilancia deshabilitada"
                            },
                            color = Blanco,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Acciones principales
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Administrar alarma",
                    color = TextoPrincipal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Selecciona qué acción deseas realizar.",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )

                BotonControl(
                    texto = "Armar alarma",
                    descripcion = "Habilitar los sensores e iniciar la vigilancia.",
                    icono = Icons.Default.AlarmOn,
                    activo = !alarmaArmada,
                    alPulsar = onArmarAlarma,
                    colorPrincipal = VerdePrincipal,
                    colorFondoIcono = VerdeClaro
                )

                BotonControl(
                    texto = "Desarmar alarma",
                    descripcion = "Deshabilitar la vigilancia y apagar los actuadores.",
                    icono = Icons.Default.AlarmOff,
                    activo = alarmaArmada,
                    alPulsar = onDesarmarAlarma,
                    colorPrincipal = RojoAlarma,
                    colorFondoIcono = FondoRojo
                )
            }

            // Control manual
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Control manual",
                    color = TextoPrincipal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Blanco
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (alarmaArmada) VerdeClaro
                                        else Color(0xFFF0F1F0)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = null,
                                    tint = if (alarmaArmada) {
                                        VerdePrincipal
                                    } else {
                                        Color.Gray
                                    },
                                    modifier = Modifier.size(25.dp)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            ) {
                                Text(
                                    text = "Alarma",
                                    color = TextoPrincipal,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = if (alarmaArmada) {
                                        "Control de actuadores disponible"
                                    } else {
                                        "Control bloqueado"
                                    },
                                    color = TextoSecundario,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Text(
                            text = if (alarmaArmada) {
                                "Accede al control para encender o apagar el LED y el buzzer juntos."
                            } else {
                                "Primero debes armar la alarma para acceder al control manual."
                            },
                            color = TextoSecundario,
                            fontSize = 13.sp
                        )

                        Button(
                            onClick = onControlManual,
                            enabled = alarmaArmada,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VerdePrincipal,
                                disabledContainerColor = Color(0xFFE4E8E5),
                                disabledContentColor = Color.Gray
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(9.dp))

                            Text(
                                text = if (alarmaArmada) {
                                    "Abrir control manual"
                                } else {
                                    "Control no disponible"
                                },
                                fontWeight = FontWeight.SemiBold
                            )

                            if (alarmaArmada) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Aviso informativo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F1FE)
                )
            ) {
                Row(
                    modifier = Modifier.padding(17.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF245BA8),
                        modifier = Modifier.size(23.dp)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = "Importante",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF245BA8),
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Al desarmar el sistema, se deshabilita la vigilancia y se apagan el LED y el buzzer. Al volver a armarlo, los actuadores permanecen apagados hasta que deban activarse.",
                            color = Color(0xFF24364B),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun BotonControl(
    texto: String,
    descripcion: String,
    icono: ImageVector,
    activo: Boolean,
    alPulsar: () -> Unit,
    colorPrincipal: Color,
    colorFondoIcono: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Blanco
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        if (activo) colorFondoIcono
                        else Color(0xFFF0F1F0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = if (activo) colorPrincipal else Color.Gray,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = texto,
                    color = TextoPrincipal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = descripcion,
                    color = TextoSecundario,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Button(
                onClick = alPulsar,
                enabled = activo,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorPrincipal,
                    disabledContainerColor = Color(0xFFE4E8E5),
                    disabledContentColor = Color.Gray
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 12.dp,
                    vertical = 10.dp
                )
            ) {
                Text(
                    text = if (activo) "Ejecutar" else "Inactivo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
