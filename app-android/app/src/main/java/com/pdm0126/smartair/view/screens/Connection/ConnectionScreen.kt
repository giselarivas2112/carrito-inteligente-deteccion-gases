package com.pdm0126.smartair.view.screens.Connection

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.view.components.AppButton
import com.pdm0126.smartair.view.components.InfoCard
import com.pdm0126.smartair.view.components.StatusPill

private val Coral = Color(0xFFF26B4F)
private val CoralLight = Color(0xFFFF8A6B)
private val Cream = Color(0xFFFFF4E6)
private val SkySoft = Color(0xFFD6E6FF)

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
fun ConnectionScreen(
  navigateBack: () -> Unit
) {
  var broker by remember { mutableStateOf("") }
  var user by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  val fieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AppColors.Purple,
    unfocusedBorderColor = AppColors.Ink.copy(alpha = 0.35f),
    focusedLabelColor = AppColors.Purple,
    unfocusedLabelColor = AppColors.Ink,
    focusedTextColor = AppColors.Ink,
    unfocusedTextColor = AppColors.Ink,
    cursorColor = AppColors.Ink,
    focusedLeadingIconColor = AppColors.Purple,
    unfocusedLeadingIconColor = AppColors.Ink,
    focusedContainerColor = Cream,
    unfocusedContainerColor = Cream
  )
  val fieldShape = RoundedCornerShape(20.dp)

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
        Bubble(Modifier.align(Alignment.TopEnd).offset(x = (-30).dp, y = 70.dp), 22.dp)
        Bubble(Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 110.dp), 12.dp)
        Bubble(Modifier.align(Alignment.BottomStart).offset(x = 28.dp, y = (-26).dp), 14.dp)

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 12.dp, end = 22.dp, top = 16.dp, bottom = 48.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            IconButton(
              onClick = navigateBack,
              modifier = Modifier.background(Cream, CircleShape)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = AppColors.Ink
              )
            }

            Text(
              text = "Ajustes de conexión",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(10.dp))


        }
      }

      // Contenido
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {

        // Estado de conexión
        InfoCard(containerColor = SkySoft) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .background(Cream, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                tint = AppColors.Blue
              )
            }

            Column(
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "Estado de conexión",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.Ink
              )

              Text(
                text = "El carrito todavía no está conectado.",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.Ink
              )
            }

            StatusPill(
              text = "Sin conexión",
              containerColor = AppColors.Sand
            )
          }
        }

        // Datos del servidor
        Text(
          text = "Datos del servidor",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = AppColors.Ink
        )

        InfoCard {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            OutlinedTextField(
              value = broker,
              onValueChange = { broker = it },
              label = { Text("Servidor (broker)") },
              placeholder = { Text("Ej. io.adafruit.com") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Cloud,
                  contentDescription = null
                )
              },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = fieldShape,
              colors = fieldColors
            )

            OutlinedTextField(
              value = user,
              onValueChange = { user = it },
              label = { Text("Usuario") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null
                )
              },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = fieldShape,
              colors = fieldColors
            )

            OutlinedTextField(
              value = password,
              onValueChange = { password = it },
              label = { Text("Contraseña") },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = null
                )
              },
              visualTransformation = PasswordVisualTransformation(),
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = fieldShape,
              colors = fieldColors
            )
          }
        }

        // Acciones
        Text(
          text = "Acciones",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = AppColors.Ink
        )

        AppButton(
          text = "Probar conexión",
          onClick = {},
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
          containerColor = Color(0xFFAECBF2),
          contentColor = AppColors.Ink
        )

        AppButton(
          text = "Guardar configuración",
          onClick = {},
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
          containerColor = Color(0xFFBBA0E5),
          contentColor = AppColors.Ink
        )



        Spacer(modifier = Modifier.height(16.dp))
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