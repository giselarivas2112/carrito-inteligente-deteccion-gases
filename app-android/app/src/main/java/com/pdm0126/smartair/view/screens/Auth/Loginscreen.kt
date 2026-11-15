package com.pdm0126.smartair.view.screens.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.R
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.view.components.AppButton
import com.pdm0126.smartair.view.components.AuthBackground
import com.pdm0126.smartair.view.components.AuthLink
import com.pdm0126.smartair.view.components.AuthTextField
import com.pdm0126.smartair.view.components.InfoCard

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    navigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Cream)
    ) {

        // Manchas de colores de fondo
        AuthBackground(variant = 0)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Mascota con círculo suave detrás
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .background(Color.White.copy(alpha = 0.65f), CircleShape)
                )

                Image(
                    painter = painterResource(id = R.drawable.tanque_gas),
                    contentDescription = "Nube mascota de Smart Air",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "¡Hola de nuevo!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.Ink
                )

                Text(
                    text = "Inicia sesión para revisar el área con Smart Air.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = AppColors.Ink
                )
            }

            InfoCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
                text = "Iniciar sesión",
                onClick = {
                    // Por ahora solo valida que no estén vacíos; aquí va la autenticación real
                    if (email.isBlank() || password.isBlank()) {
                        error = "Completa tu correo y contraseña."
                    } else {
                        onLoginSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                containerColor = Color(0xFFF26B4F),
                contentColor = Color.White
            )

            AuthLink(
                text = "¿No tienes cuenta?",
                action = "Regístrate",
                onClick = navigateToRegister
            )
        }
    }
}