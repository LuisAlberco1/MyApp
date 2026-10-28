
package com.example.myapp

import android.util.Patterns
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage

@Composable
private fun ImagenRemota(
    url: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
fun CentinelaInicioDeSesion(
    onIniciarSesion: (String, String) -> Unit = { _, _ -> },
    onRecuperarContrasena: (String) -> Unit = {},
    onContactarSoporte: () -> Unit = {},
    onErrorInicioSesion: String = "",
    mensajeRecuperacion: String = ""
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(onErrorInicioSesion) {
        error = onErrorInicioSesion
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF8FAF9))
                .verticalScroll(rememberScrollState())
        ) {
            // ENCABEZADO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF103D32))
                    .padding(bottom = 30.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "9:41",
                        color = Color.White,
                        fontSize = 13.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImagenRemota(
                            url = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/j4ZYSbYAJu/e9ku9c4o_expires_30_days.png",
                            modifier = Modifier
                                .padding(end = 7.dp)
                                .size(16.dp)
                        )

                        ImagenRemota(
                            url = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/j4ZYSbYAJu/7nyebx55_expires_30_days.png",
                            modifier = Modifier
                                .padding(end = 7.dp)
                                .size(16.dp)
                        )

                        Text(
                            text = "100 %",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )

                        ImagenRemota(
                            url = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/j4ZYSbYAJu/5zcc54mf_expires_30_days.png",
                            modifier = Modifier.size(
                                width = 20.dp,
                                height = 14.dp
                            )
                        )
                    }
                }

                Box {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImagenRemota(
                                url = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/j4ZYSbYAJu/n5dp9yug_expires_30_days.png",
                                modifier = Modifier
                                    .padding(start = 28.dp, end = 12.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .size(48.dp)
                            )

                            Column {
                                Text(
                                    text = "SISTEMA DE ALARMA",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                Text(
                                    text = "Seguridad para tu hogar",
                                    color = Color(0xFFC0D8CE),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 26.dp)
                        ) {
                            Text(
                                text = "Tu hogar,\nen buenas manos.",
                                color = Color.White,
                                fontSize = 32.sp,
                                lineHeight = 38.sp,
                                modifier = Modifier.padding(
                                    start = 28.dp,
                                    bottom = 10.dp
                                )
                            )

                            Text(
                                text = "Tu alarma. Tu control. Tu tranquilidad.",
                                color = Color(0xFFC0D8CE),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 28.dp)
                            )
                        }
                    }

                    ImagenRemota(
                        url = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/j4ZYSbYAJu/3y3wkd9p_expires_30_days.png",
                        modifier = Modifier
                            .offset(y = 28.dp)
                            .align(Alignment.TopEnd)
                            .padding(bottom = 28.dp)
                            .size(width = 168.dp, height = 200.dp)
                    )
                }
            }

            // FORMULARIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 30.dp,
                        start = 28.dp,
                        end = 28.dp
                    )
            ) {
                Text(
                    text = "Inicia sesión",
                    color = Color(0xFF172D29),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Accede para cuidar de lo que más importa.",
                    color = Color(0xFF62726E),
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // CORREO ELECTRÓNICO
                Text(
                    text = "Correo electrónico",
                    color = Color(0xFF172D29),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            1.dp,
                            Color(0xFFA5B4AE),
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    ImagenRemota(
                        url = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/j4ZYSbYAJu/lrukuoyj_expires_30_days.png",
                        modifier = Modifier
                            .padding(start = 16.dp, end = 12.dp)
                            .size(20.dp)
                    )

                    BasicTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            error = ""
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color(0xFF62726E),
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                            .padding(vertical = 18.dp),
                        decorationBox = { campo ->
                            Box {
                                if (correo.isEmpty()) {
                                    Text(
                                        text = "nombre@ejemplo.com",
                                        color = Color(0xFF62726E),
                                        fontSize = 16.sp
                                    )
                                }
                                campo()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CONTRASEÑA
                Text(
                    text = "Contraseña",
                    color = Color(0xFF172D29),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            1.dp,
                            Color(0xFFA5B4AE),
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    ImagenRemota(
                        url = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/j4ZYSbYAJu/wfki19kz_expires_30_days.png",
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(20.dp)
                    )

                    BasicTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            error = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 14.dp),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color(0xFF172D29),
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation = if (mostrarContrasena) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        decorationBox = { campo ->
                            Box {
                                if (contrasena.isEmpty()) {
                                    Text(
                                        text = "Introduce tu contraseña",
                                        color = Color(0xFF62726E),
                                        fontSize = 16.sp
                                    )
                                }
                                campo()
                            }
                        }
                    )

                    IconButton(
                        onClick = {
                            mostrarContrasena = !mostrarContrasena
                        }
                    ) {
                        Icon(
                            imageVector = if (mostrarContrasena) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = if (mostrarContrasena) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = Color(0xFF62726E)
                        )
                    }
                }

                // RECUPERAR CONTRASEÑA
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = Color(0xFF16745B),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp, bottom = 24.dp)
                        .clickable {
                            error = ""

                            when {
                                correo.isBlank() -> {
                                    error = "Introduce tu correo para recuperar la contraseña."
                                }

                                !Patterns.EMAIL_ADDRESS
                                    .matcher(correo.trim())
                                    .matches() -> {
                                    error = "Introduce un correo electrónico válido."
                                }

                                else -> {
                                    onRecuperarContrasena(correo.trim())
                                }
                            }
                        }
                )

                // MENSAJE DE ERROR
                if (error.isNotEmpty()) {
                    Text(
                        text = error,
                        color = Color(0xFFB3261E),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // MENSAJE DE RECUPERACIÓN
                if (mensajeRecuperacion.isNotEmpty()) {
                    Text(
                        text = mensajeRecuperacion,
                        color = Color(0xFF16745B),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // BOTÓN DE INICIO DE SESIÓN
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF16745B))
                        .clickable {
                            when {
                                correo.isBlank() -> {
                                    error = "Introduce tu correo electrónico."
                                }

                                !Patterns.EMAIL_ADDRESS
                                    .matcher(correo.trim())
                                    .matches() -> {
                                    error = "Introduce un correo electrónico válido."
                                }

                                contrasena.isBlank() -> {
                                    error = "Introduce tu contraseña."
                                }

                                else -> {
                                    error = ""
                                    onIniciarSesion(correo.trim(), contrasena)
                                }
                            }
                        }
                        .padding(vertical = 18.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 13.dp)
                    )

                    ImagenRemota(
                        url = "https://storage.googleapis.com/tagjs-prod.appspot.com/v1/j4ZYSbYAJu/ktif6mfj_expires_30_days.png",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(51.dp))

            // SOPORTE
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 27.dp)
            ) {
                Text(
                    text = "¿Necesitas ayuda?",
                    color = Color(0xFF62726E),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 7.dp)
                )

                Text(
                    text = "Contacta con soporte",
                    color = Color(0xFF16745B),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        onContactarSoporte()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
