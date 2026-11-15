package com.pdm0126.smartair.view.screens.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.R
import com.pdm0126.smartair.navigation.Routes
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.view.components.BottomNavBar

private val Coral = Color(0xFFF26B4F)
private val CoralLight = Color(0xFFFF8A6B)
private val Cream = Color(0xFFFFF4E6)
private val Lilac = Color(0xFFC9B2F5)
private val Sky = Color(0xFF9DC3FF)
private val Mint = Color(0xFF3FC5A8)
private val MintDark = Color(0xFF1F8A72)
private val Danger = Color(0xFFCF3F24)

// Borde inferior ondulado de la cabecera
private val WaveShape = GenericShape { size, _ ->
  val w = size.width
  val h = size.height
  moveTo(0f, 0f)
  lineTo(w, 0f)
  lineTo(w, h * 0.81f)
  cubicTo(w, h * 0.945f, w * 0.88f, h * 0.976f, w * 0.76f, h * 0.93f)
  cubicTo(w * 0.58f, h * 0.87f, w * 0.43f, h * 1.01f, w * 0.22f, h * 0.95f)
  cubicTo(w * 0.08f, h * 0.92f, 0f, h * 0.90f, 0f, h * 0.81f)
  close()
}

@Composable
fun HomeScreen(
  onTabClick: (Routes) -> Unit,
  navigateToControl: () -> Unit,
  navigateToRoute: () -> Unit,
  navigateToConnection: () -> Unit,
  isConnected: Boolean = false
) {
  Scaffold(
    containerColor = Cream,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      BottomNavBar(
        selected = Routes.Home,
        onTabClick = onTabClick
      )
    }
  ) { padding ->

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
    ) {

      // Cabecera a todo el ancho con la píldora de estado encima del borde
      Box(modifier = Modifier.fillMaxWidth()) {

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(WaveShape)
            .background(Brush.verticalGradient(listOf(CoralLight, Coral)))
        ) {
          Bubble(Modifier.align(Alignment.TopEnd).offset(x = (-70).dp, y = 90.dp), 22.dp)
          Bubble(Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 130.dp), 12.dp)
          Bubble(Modifier.align(Alignment.BottomStart).offset(x = 28.dp, y = (-30).dp), 14.dp)

          Column(
            modifier = Modifier
              .statusBarsPadding()
              .padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 52.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.Top
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Hola, Gisela",
                  style = MaterialTheme.typography.headlineLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White
                )
                Text(
                  text = "Tu entorno, bajo control.",
                  style = MaterialTheme.typography.bodyLarge,
                  color = AppColors.Ink
                )
              }

              IconButton(
                onClick = navigateToConnection,
                modifier = Modifier.background(Cream, CircleShape)
              ) {
                Icon(
                  imageVector = Icons.Filled.Settings,
                  contentDescription = "Configuración de conexión",
                  tint = AppColors.Ink
                )
              }
            }
          }
        }

        // Píldora de estado
        Row(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .offset(y = 24.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(50))
            .background(Color.White, RoundedCornerShape(50))
            .padding(horizontal = 20.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(14.dp)
              .clip(CircleShape)
              .background(if (isConnected) Mint else Danger)
          )
          Text(
            text = if (isConnected) "Carrito conectado" else "Carrito desconectado",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.Ink
          )
          Text(
            text = if (isConnected) "Aire seguro" else "Sin conexión",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isConnected) MintDark else Danger
          )
        }
      }

      Spacer(modifier = Modifier.height(44.dp))

      Text(
        text = "¿Qué deseas hacer?",
        modifier = Modifier.padding(horizontal = 24.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = AppColors.Ink
      )

      ActionCard(
        title = "Hacer un reporte",
        description = "Recorrido automático que revisa el área y genera tu reporte.",
        buttonText = "Inspeccionar área  →",
        containerColor = Lilac,
        imageRes = R.drawable.escudo,
        onClick = navigateToRoute
      )

      ActionCard(
        title = "Conducir el carrito",
        description = "Manéjalo con los controles, sin mediciones ni reportes.",
        buttonText = "Abrir controles  →",
        containerColor = Sky,
        imageRes = R.drawable.carrito,
        onClick = navigateToControl
      )

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun Bubble(modifier: Modifier, size: androidx.compose.ui.unit.Dp) {
  Box(
    modifier = modifier
      .size(size)
      .border(3.dp, Cream.copy(alpha = 0.8f), CircleShape)
  )
}

@Composable
private fun ActionCard(
  title: String,
  description: String,
  buttonText: String,
  containerColor: Color,
  imageRes: Int,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 20.dp, end = 20.dp, top = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(36.dp))
        .background(containerColor)
        .clickable(onClick = onClick)
        .padding(20.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth(0.62f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = AppColors.Ink
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = description,
          style = MaterialTheme.typography.bodyMedium,
          color = AppColors.Ink
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(AppColors.Ink)
            .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
          Text(
            text = buttonText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Cream
          )
        }
      }
    }

    Image(
      painter = painterResource(id = imageRes),
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .offset(x = (-8).dp, y = (-28).dp)
        .size(130.dp)
    )
  }
}