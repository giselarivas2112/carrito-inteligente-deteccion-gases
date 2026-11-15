package com.pdm0126.smartair.view.screens.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.view.components.AppButton
import com.pdm0126.smartair.view.components.AuthBackground
import com.pdm0126.smartair.view.components.AuthLink
import com.pdm0126.smartair.view.components.AuthTextField
import com.pdm0126.smartair.view.components.InfoCard

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Cream)
    ) {

        // Manchas de colores de fondo (otra distribución que en el login)
        AuthBackground(variant = 1)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Botón de regresar
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = navigateBack,
                    modifier = Modifier.background(Color(0xFFFFF4E6), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = AppColors.Ink
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.Ink
                )

                Text(
                    text = "Regístrate para guardar tus inspecciones.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = AppColors.Ink
                )
            }

            InfoCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    AuthTextField(
                        value = name,
                        onValueChange = { name = it; error = null },
                        label = "Nombre",
                        icon = Icons.Filled.Person
                    )

                    AuthTextField(
                        value = email,
                        onValueChange = { email = it; error = null },
                        label = "Correo electrónico",
                        icon = Icons.Filled.Email,
                        keyboardType = KeyboardType.Email
                    )

                    AuthTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        label = "Contraseña",
                        icon = Icons.Filled.Lock,
                        isPassword = true
                    )

                    AuthTextField(
                        value = confirm,
                        onValueChange = { confirm = it; error = null },
                        label = "Confirmar contraseña",
                        icon = Icons.Filled.Lock,
                        isPassword = true
                    )

                    error?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Danger
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            AppButton(
                text = "Crear cuenta",
                onClick = {
                    // Validaciones básicas; aquí va el registro real
                    error = when {
                        name.isBlank() || email.isBlank() || password.isBlank() || confirm.isBlank() ->
                            "Completa todos los campos."
                        !email.contains("@") ->
                            "Escribe un correo válido."
                        password.length < 6 ->
                            "La contraseña debe tener al menos 6 caracteres."
                        password != confirm ->
                            "Las contraseñas no coinciden."
                        else -> null
                    }

                    if (error == null) onRegisterSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                containerColor = Color(0xFFF26B4F),
                contentColor = Color.White
            )

            AuthLink(
                text = "¿Ya tienes cuenta?",
                action = "Inicia sesión",
                onClick = navigateToLogin
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}