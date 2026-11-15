package com.pdm0126.smartair.view.screens.Welcome

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.R
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.view.components.AppButton

// Paleta que usan las demás pantallas
private val Coral = Color(0xFFF26B4F)
private val CoralLight = Color(0xFFFF8A6B)
private val Lila = Color(0xFFBBA0E5)
private val Celeste = Color(0xFFAECBF2)
private val Menta = Color(0xFFB0DDC7)
private val Amarillo = Color(0xFFF7D68A)
private val Cream = Color(0xFFFFF4E6)

@Composable
fun WelcomeScreen(navigateToHome: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(AppColors.Cream)
  ) {

    // Manchas de colores de fondo
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Arriba a la derecha
      drawCircle(Coral.copy(alpha = 0.9f), radius = w * 0.40f, center = Offset(w * 1.00f, h * 0.02f))
      drawCircle(CoralLight.copy(alpha = 0.55f), radius = w * 0.22f, center = Offset(w * 0.62f, h * 0.00f))

      // Arriba a la izquierda
      drawCircle(Lila.copy(alpha = 0.85f), radius = w * 0.26f, center = Offset(w * 0.02f, h * 0.17f))

      // Centro derecha
      drawCircle(Amarillo.copy(alpha = 0.85f), radius = w * 0.16f, center = Offset(w * 0.96f, h * 0.46f))

      // Abajo a la izquierda
      drawCircle(Celeste.copy(alpha = 0.95f), radius = w * 0.38f, center = Offset(w * 0.00f, h * 0.98f))
      drawCircle(Menta.copy(alpha = 0.95f), radius = w * 0.24f, center = Offset(w * 0.40f, h * 1.02f))

      // Abajo a la derecha
      drawCircle(Lila.copy(alpha = 0.75f), radius = w * 0.20f, center = Offset(w * 1.00f, h * 0.93f))
    }

    // Burbujitas de adorno (las mismas de las cabeceras)
    Bubble(Modifier.align(Alignment.TopStart).offset(x = 70.dp, y = 90.dp), 20.dp, Coral)
    Bubble(Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = 190.dp), 12.dp, Lila)
    Bubble(Modifier.align(Alignment.CenterStart).offset(x = 24.dp, y = 60.dp), 14.dp, Celeste)
    Bubble(Modifier.align(Alignment.BottomEnd).offset(x = (-60).dp, y = (-170).dp), 16.dp, Menta)

    // Contenido
    Column(
      modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
        .padding(horizontal = 32.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {

      // Mascota con un círculo suave detrás para que resalte
      Box(contentAlignment = Alignment.Center) {
        Box(
          modifier = Modifier
            .size(300.dp)
            .background(Color.White.copy(alpha = 0.65f), CircleShape)
        )

        Image(
          painter = painterResource(id = R.drawable.tanque_gas),
          contentDescription = "Nube mascota de Smart Air",
          modifier = Modifier.width(300.dp),
          contentScale = ContentScale.Fit
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Smart Air",
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.ExtraBold,
        color = AppColors.Ink
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Tu carrito olfateador de gases. Revisa el área antes de entrar.",
        style = MaterialTheme.typography.titleMedium,
        color = AppColors.Ink,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(36.dp))

      AppButton(
        text = "Empezar",
        onClick = navigateToHome,
        modifier = Modifier
          .fillMaxWidth()
          .height(60.dp),
        containerColor = Coral,
        contentColor = Color.White
      )
    }
  }
}

@Composable
private fun Bubble(modifier: Modifier, size: Dp, color: Color) {
  Box(
    modifier = modifier
      .size(size)
      .border(3.dp, color, CircleShape)
  )
}