
package com.example.myapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeOscuro = Color(0xFF103D32)
private val VerdePrincipal = Color(0xFF16745B)
private val FondoPantalla = Color(0xFFF8FAF9)
private val TextoPrincipal = Color(0xFF172D29)
private val TextoSecundario = Color(0xFF62726E)
private val BordeTarjeta = Color(0xFFDCE5E0)
private val RojoAlarma = Color(0xFFC62828)
private val FondoRojo = Color(0xFFFFE7E7)

@Composable
fun CentinelaControlManual(
    alarmaArmada: Boolean,
    ledEncendido: Boolean,
    buzzerEncendido: Boolean,
    onCambiarAlarma: (Boolean) -> Unit,
    onVolver: () -> Unit = {}
) {
    val actuadoresEncendidos = ledEncendido && buzzerEncendido

    val colorEstado = if (alarmaArmada) {
        VerdePrincipal
    } else {
        RojoAlarma
    }

    val fondoEstado = if (alarmaArmada) {
        Color(0xFFE6F2EC)
    } else {
        FondoRojo
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
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
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
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Row(
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 18.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = "Control manual",
                    color = Color.White,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

        // Contenido
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Estado de la alarma
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mi hogar",
                        color = TextoPrincipal,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(7.dp)
                                .clip(RoundedCornerShape(50))
                                .background(VerdePrincipal)
                        )

                        Text(
                            text = "En línea",
                            color = VerdePrincipal,
                            fontSize = 12.sp
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = fondoEstado
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (alarmaArmada) {
                                    Icons.Default.AlarmOn
                                } else {
                                    Icons.Default.AlarmOff
                                },
                                contentDescription = null,
                                tint = colorEstado,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(48.dp)
                            )

                            Column {
                                Text(
                                    text = "ESTADO DE PROTECCIÓN",
                                    color = colorEstado,
                                    fontSize = 12.sp
                                )

                                Text(
                                    text = if (alarmaArmada) {
                                        "Alarma armada"
                                    } else {
                                        "Alarma desarmada"
                                    },
                                    color = TextoPrincipal,
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (alarmaArmada) {
                                "Tu hogar está bajo vigilancia. Los sensores están habilitados."
                            } else {
                                "La vigilancia está desactivada. Los actuadores permanecen apagados."
                            },
                            color = TextoPrincipal,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Control manual
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Alarma sonora y visual",
                    color = TextoPrincipal,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Activa o desactiva la alarma manualmente.",
                    color = TextoSecundario,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = BordeTarjeta,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Estado de la alarma",
                                    color = TextoPrincipal,
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (!alarmaArmada) {
                                        "No disponible"
                                    } else if (actuadoresEncendidos) {
                                        "Activada"
                                    } else {
                                        "Desactivada"
                                    },
                                    color = if (alarmaArmada && actuadoresEncendidos) {
                                        VerdePrincipal
                                    } else {
                                        TextoSecundario
                                    },
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Icon(
                                imageVector = if (actuadoresEncendidos) {
                                    Icons.Default.Bolt
                                } else {
                                    Icons.Default.PowerSettingsNew
                                },
                                contentDescription = null,
                                tint = if (actuadoresEncendidos) {
                                    VerdePrincipal
                                } else {
                                    TextoSecundario
                                },
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(BordeTarjeta)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = if (ledEncendido) {
                                            VerdePrincipal
                                        } else {
                                            TextoSecundario
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Text(
                                        text = if (ledEncendido) {
                                            "Estado visual: activo"
                                        } else {
                                            "Estado visual: inactivo"
                                        },
                                        modifier = Modifier.padding(start = 8.dp),
                                        color = TextoPrincipal,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = if (buzzerEncendido) {
                                            VerdePrincipal
                                        } else {
                                            TextoSecundario
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Text(
                                        text = if (buzzerEncendido) {
                                            "Estado sonoro: activo"
                                        } else {
                                            "Estado sonoro: inactivo"
                                        },
                                        modifier = Modifier.padding(start = 8.dp),
                                        color = TextoPrincipal,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Switch(
                                checked = actuadoresEncendidos,
                                onCheckedChange = { encender ->
                                    if (alarmaArmada) {
                                        onCambiarAlarma(encender)
                                    }
                                },
                                enabled = alarmaArmada
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (alarmaArmada) {
                                "Usa el interruptor para activar o desactivar la alarma."
                            } else {
                                "Debes armar la alarma desde el control del dispositivo para habilitar este interruptor."
                            },
                            color = TextoSecundario,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (alarmaArmada) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.AlarmOff
                        },
                        contentDescription = null,
                        tint = colorEstado,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .size(16.dp)
                    )

                    Text(
                        text = if (alarmaArmada) {
                            "Control manual disponible."
                        } else {
                            "Control manual bloqueado mientras la alarma esté desarmada."
                        },
                        color = colorEstado,
                        fontSize = 12.sp
                    )
                }
            }

            // Aclaración
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = VerdePrincipal,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(22.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Apagar no desarma la alarma",
                            color = TextoPrincipal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Cuando la alarma está armada, puedes desactivar el aviso sonoro y visual sin desactivar la vigilancia. Al desarmarla, ambos se apagan.",
                            color = TextoSecundario,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Aviso de demostración
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = TextoSecundario,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Control local de demostración",
                    color = TextoSecundario,
                    fontSize = 12.sp
                )
            }
        }
    }
}
