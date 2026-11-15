package com.pdm0126.smartair.view.screens.AutoRoute

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.navigation.Routes
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.utils.TOTAL_SEGMENTS
import com.pdm0126.smartair.view.components.AppButton
import com.pdm0126.smartair.view.components.InfoCard
import com.pdm0126.smartair.view.components.LevelBar
import com.pdm0126.smartair.view.components.RouteMap
import com.pdm0126.smartair.view.components.StatusPill

private val Coral = Color(0xFFF26B4F)
private val CoralLight = Color(0xFFFF8A6B)
private val Cream = Color(0xFFFFF4E6)

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
fun AutoRouteScreen(
  onTabClick: (Routes) -> Unit,
  navigateToAlert: () -> Unit,
  viewModel: AutoRouteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

  // Estado de ejemplo
  var finished by remember { mutableStateOf(false) }

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
          .background(
            Brush.verticalGradient(
              listOf(CoralLight, Coral)
            )
          )
      ) {

        Bubble(
          Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-70).dp, y = 105.dp),
          22.dp
        )

        Bubble(
          Modifier
            .align(Alignment.TopEnd)
            .offset(x = (-20).dp, y = 110.dp),
          12.dp
        )

        Bubble(
          Modifier
            .align(Alignment.BottomStart)
            .offset(x = 28.dp, y = (-26).dp),
          14.dp
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(
              start = 12.dp,
              end = 22.dp,
              top = 16.dp,
              bottom = 44.dp
            ),
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

          // Título en dos líneas
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
          ) {

            Text(
              text = "Inspección",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              maxLines = 1
            )

            Text(
              text = "Automática",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              maxLines = 1
            )
          }

          // Estado de conexión
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
          .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {

        // Botón iniciar recorrido
        AppButton(
          text = "Iniciar recorrido",
          onClick = {},
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
          containerColor = Color(0xFFBBA0E5),
          contentColor = AppColors.Ink
        )

        // Botones pausar y terminar reporte
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

          AppButton(
            text = "Pausar",
            onClick = {},
            modifier = Modifier
              .weight(1f)
              .height(80.dp),
            containerColor = Color(0xFFAECBF2),
            contentColor = AppColors.Ink
          )

          AppButton(
            text = "Terminar reporte",
            onClick = { finished = true },
            modifier = Modifier
              .weight(1f)
              .height(80.dp),
            containerColor = Color(0xFFEBA79E),
            contentColor = AppColors.Ink
          )
        }

        // Aviso de inspección terminada
        if (finished) {

          InfoCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0xFFB0DDC7)
          ) {

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

              Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = AppColors.Ink
              )

              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
              ) {

                Text(
                  text = "Inspección terminada",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = AppColors.Ink
                )

                Text(
                  text = "Tu reporte ya está listo.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = AppColors.Ink
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mapa del recorrido
        InfoCard {

          Text(
            text = "Mapa del recorrido",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.Ink
          )

          Text(
            text = "Ruta de inspección por tramos",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.Ink
          )

          Spacer(modifier = Modifier.height(16.dp))

          RouteMap(
            total = TOTAL_SEGMENTS,
            results = emptyMap()
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Leyenda del mapa
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {

            StatusPill(
              text = "Seguro",
              containerColor = AppColors.Mint
            )

            StatusPill(
              text = "Precaución",
              containerColor = AppColors.Yellow
            )

            StatusPill(
              text = "Peligro",
              containerColor = AppColors.Danger,
              contentColor = Color.White
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Progreso de inspección",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = AppColors.Ink
          )

          LevelBar(
            fraction = if (finished) 1f else 0.03f,
            color = AppColors.Purple
          )

          Text(
            text = if (finished) {
              "$TOTAL_SEGMENTS de $TOTAL_SEGMENTS tramos completados"
            } else {
              "0 de $TOTAL_SEGMENTS tramos completados"
            },
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.Ink
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lecturas de sensores
        Text(
          text = "Lecturas en vivo",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = AppColors.Ink
        )

        StatusPill(
          text = "Esperando lecturas",
          containerColor = AppColors.Sand
        )

        // Sensor MQ-6
        SensorPreview(
          name = "MQ-6",
          description = "Gas LPG",
          cardColor = Color(0xFFB0DDC7)
        )

        // Sensor MQ-2
        SensorPreview(
          name = "MQ-2",
          description = "Humo y gas combustible",
          cardColor = Color(0xFFF7D68A)
        )

        // Sensor MQ-135
        SensorPreview(
          name = "MQ-135",
          description = "Calidad del aire",
          cardColor = Color(0xFFAECBF2)
        )

        // Sensor de llama
        SensorPreview(
          name = "Sensor de llama",
          description = "Detección de fuego",
          cardColor = Color(0xFFCBADE8)
        )

        // Evaluación del ambiente
        InfoCard {

          Text(
            text = "Evaluación del ambiente",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.Ink
          )

          Spacer(modifier = Modifier.height(8.dp))

          StatusPill(
            text = "Sin evaluación",
            containerColor = AppColors.Sand
          )

          Text(
            text = "El nivel de seguridad se mostrará cuando se reciban las lecturas de los sensores.",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.Ink
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón limpiar al final de la pantalla
        AppButton(
          text = "Limpiar",
          onClick = { finished = false },
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
          containerColor = Color(0xFFD6D6D6),
          contentColor = AppColors.Ink
        )
      }
    }
  }
}

@Composable
private fun Bubble(
  modifier: Modifier,
  size: Dp
) {
  Box(
    modifier = modifier
      .size(size)
      .border(
        3.dp,
        Cream.copy(alpha = 0.8f),
        CircleShape
      )
  )
}

// Tarjeta individual de cada sensor
@Composable
private fun SensorPreview(
  name: String,
  description: String,
  cardColor: Color
) {
  InfoCard(
    modifier = Modifier.fillMaxWidth(),
    containerColor = cardColor
  ) {

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

      Text(
        text = name,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = AppColors.Ink
      )

      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = AppColors.Ink
      )

      Text(
        text = "—",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = AppColors.Ink
      )

      StatusPill(
        text = "Sin datos",
        containerColor = AppColors.Sand
      )

      LevelBar(
        fraction = 0f,
        color = AppColors.Ink
      )
    }
  }
}
