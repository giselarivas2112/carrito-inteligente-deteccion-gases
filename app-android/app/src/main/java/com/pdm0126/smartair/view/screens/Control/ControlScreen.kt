package com.pdm0126.smartair.view.screens.Control

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.navigation.Routes
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.view.components.DirectionPad
import com.pdm0126.smartair.view.components.InfoCard
import com.pdm0126.smartair.view.components.StatusPill

private val Coral = Color(0xFFF26B4F)
private val CoralLight = Color(0xFFFF8A6B)
private val Cream = Color(0xFFFFF4E6)

// Borde inferior ondulado de la cabecera (el mismo de Home)
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
fun ControlScreen(
  onTabClick: (Routes) -> Unit
) {

  Scaffold(
    containerColor = AppColors.Cream,
    contentWindowInsets = WindowInsets(0, 0, 0, 0)
  ) { padding ->

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
    ) {

      // Cabecera coral a todo el ancho
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(WaveShape)
          .background(Brush.verticalGradient(listOf(CoralLight, Coral)))
      ) {
        Bubble(Modifier.align(Alignment.TopEnd).offset(x = (-30).dp, y = 80.dp), 22.dp)
        Bubble(Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 110.dp), 12.dp)
        Bubble(Modifier.align(Alignment.BottomStart).offset(x = 28.dp, y = (-26).dp), 14.dp)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 12.dp, end = 22.dp, top = 16.dp, bottom = 44.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = { onTabClick(Routes.Home) },
            modifier = Modifier.background(Cream, CircleShape)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Regresar",
              tint = AppColors.Ink
            )
          }

          Column(
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = "Control manual",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )

            Text(
              text = "Conduce tu carrito",
              style = MaterialTheme.typography.bodyMedium,
              color = AppColors.Ink
            )
          }

          StatusPill(
            text = "Sin conexión",
            containerColor = AppColors.Sand
          )
        }
      }

      // Contenido
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {

        // Recomendación de seguridad (texto reducido)
        InfoCard {
          Text(
            text = "Recomendación de seguridad",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Mantén el carrito bajo supervisión. Si se pierde la conexión, se detendrá solo.",
            style = MaterialTheme.typography.bodySmall
          )
        }

        // Controles de dirección
        InfoCard {
          Text(
            text = "Dirección del movimiento",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = "Selecciona una dirección para conducir el carrito.",
            style = MaterialTheme.typography.bodySmall
          )

          Spacer(modifier = Modifier.height(24.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            DirectionPad(
              onCommand = {}
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "STOP",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = AppColors.Danger,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )

          Text(
            text = "Utiliza el botón central para detener el movimiento.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
          )
        }

        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
private fun Bubble(modifier: Modifier, size: Dp) {
  Box(
    modifier = modifier
      .size(size)
      .border(3.dp, Cream.copy(alpha = 0.8f), CircleShape)
  )
}