package com.pdm0126.smartair.view.screens.Report

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.navigation.Routes
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.utils.TOTAL_SEGMENTS
import com.pdm0126.smartair.view.components.AppButton
import com.pdm0126.smartair.view.components.BottomNavBar
import com.pdm0126.smartair.view.components.InfoCard
import com.pdm0126.smartair.view.components.LevelBar
import com.pdm0126.smartair.view.components.RouteMap
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

// Nivel de riesgo de un tramo (el mismo criterio del recorrido automático)
private enum class Risk(val label: String, val fraction: Float) {
  Safe("Seguro", 0.33f),
  Caution("Precaución", 0.66f),
  Danger("Peligro", 1f)
}

// Lectura máxima registrada por un sensor durante el recorrido
private data class SensorSummary(
  val name: String,
  val description: String,
  val cardColor: Color,
  val maxValue: String? = null
)

@Composable
fun ReportScreen(
  reportId: Int,
  onTabClick: (Routes) -> Unit,
  // Fecha de ejemplo: pásale la fecha real del reporte cuando la tengas
  date: String = "08/11/2026 · 10:30 a. m."
) {

  // Datos de ejemplo: cuando conectes el sistema, reemplaza estas listas
  // con los resultados reales del reporte (un nivel por tramo y el máximo por sensor).
  val segments: List<Risk?> = List(TOTAL_SEGMENTS) { null }

  val sensors = listOf(
    SensorSummary("MQ-6", "Gas LPG", Color(0xFFB0DDC7)),
    SensorSummary("MQ-2", "Humo y gas combustible", Color(0xFFF7D68A)),
    SensorSummary("MQ-135", "Calidad del aire", Color(0xFFAECBF2)),
    SensorSummary("Sensor de llama", "Detección de fuego", Color(0xFFCBADE8))
  )

  val inspected = segments.count { it != null }
  val safeCount = segments.count { it == Risk.Safe }
  val cautionCount = segments.count { it == Risk.Caution }
  val dangerCount = segments.count { it == Risk.Danger }

  // Resultado general según el peor tramo encontrado
  val overall: Risk? = when {
    dangerCount > 0 -> Risk.Danger
    cautionCount > 0 -> Risk.Caution
    safeCount > 0 -> Risk.Safe
    else -> null
  }

  val overallColor = when (overall) {
    Risk.Safe -> AppColors.Mint
    Risk.Caution -> AppColors.Yellow
    Risk.Danger -> AppColors.Danger
    null -> AppColors.Sand
  }

  val overallTextColor = if (overall == Risk.Danger) Color.White else AppColors.Ink

  val overallTitle = when (overall) {
    Risk.Safe -> "Ambiente seguro"
    Risk.Caution -> "Ambiente con precaución"
    Risk.Danger -> "Ambiente peligroso"
    null -> "Sin evaluación"
  }

  val overallDescription = when (overall) {
    Risk.Safe -> "No se detectaron gases, humo ni llamas en el recorrido."
    Risk.Caution -> "Se detectaron lecturas elevadas en algunos tramos."
    Risk.Danger -> "Se detectaron niveles peligrosos en al menos un tramo."
    null -> "El nivel de seguridad se mostrará cuando se reciban las lecturas de los sensores."
  }

  val recommendation = when (overall) {
    Risk.Safe -> "El área puede considerarse segura. Mantén la ventilación habitual y repite la inspección periódicamente."
    Risk.Caution -> "Ventila el área y revisa los tramos marcados en amarillo. Repite la inspección para confirmar que los niveles bajan."
    Risk.Danger -> "Evita permanecer en el área. Ventila, corta posibles fuentes de gas o fuego y solicita revisión profesional."
    null -> "La recomendación estará disponible cuando se realice una inspección y se reciban las lecturas de los sensores."
  }

  Scaffold(
    containerColor = AppColors.Cream,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      BottomNavBar(
        selected = Routes.History,
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
            onClick = { onTabClick(Routes.History) },
            modifier = Modifier.background(Cream, CircleShape)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Regresar",
              tint = AppColors.Ink
            )
          }

          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = "Reporte #$reportId",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              maxLines = 1
            )

            Text(
              text = "Inspección automática",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White,
              maxLines = 1
            )
          }

          StatusPill(
            text = "Completada",
            containerColor = Cream
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

        // 1. Resultado general
        InfoCard(containerColor = overallColor) {
          Text(
            text = "Resultado de la inspección",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = overallTextColor
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = overallTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = overallTextColor
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = overallDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = overallTextColor
          )

          Spacer(modifier = Modifier.height(12.dp))

          StatusPill(
            text = if (overall == null) "Sin datos" else "Evaluación lista",
            containerColor = Color.White,
            contentColor = AppColors.Ink
          )
        }

        // 2. Resumen del recorrido
        Text(
          text = "Resumen del recorrido",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = AppColors.Ink
        )

        InfoCard {
          ReportDetail(label = "Fecha", value = date)
          ReportDetail(label = "Tipo de recorrido", value = "Automático")
          ReportDetail(
            label = "Tramos inspeccionados",
            value = "$inspected de $TOTAL_SEGMENTS"
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Conteo de tramos por nivel
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            StatusPill(
              text = "Seguro: $safeCount",
              containerColor = AppColors.Mint
            )

            StatusPill(
              text = "Precaución: $cautionCount",
              containerColor = AppColors.Yellow
            )

            StatusPill(
              text = "Peligro: $dangerCount",
              containerColor = AppColors.Danger,
              contentColor = Color.White
            )
          }
        }

        // 3. Mapa del recorrido
        InfoCard {
          Text(
            text = "Mapa del recorrido",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.Ink
          )

          Text(
            text = "Resultado de cada tramo inspeccionado",
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.Ink
          )

          Spacer(modifier = Modifier.height(16.dp))

          RouteMap(
            total = TOTAL_SEGMENTS,
            results = emptyMap()
          )
        }

        // 4. Detalle por tramos
        Text(
          text = "Detalle por tramos",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = AppColors.Ink
        )

        InfoCard {
          segments.forEachIndexed { index, risk ->
            val barColor = when (risk) {
              Risk.Safe -> AppColors.Mint
              Risk.Caution -> AppColors.Yellow
              Risk.Danger -> AppColors.Danger
              null -> AppColors.Sand
            }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Tramo ${index + 1}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.Ink,
                modifier = Modifier.width(80.dp)
              )

              LevelBar(
                fraction = risk?.fraction ?: 0f,
                color = barColor,
                modifier = Modifier.weight(1f)
              )

              Spacer(modifier = Modifier.width(10.dp))

              Text(
                text = risk?.label ?: "Sin datos",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.Ink,
                modifier = Modifier.width(78.dp)
              )
            }
          }
        }

        // 5. Lecturas máximas por sensor
        Text(
          text = "Lecturas máximas",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = AppColors.Ink
        )

        sensors.forEach { sensor ->
          SensorReportCard(sensor)
        }

        // 6. Recomendación
        InfoCard {
          Text(
            text = "Recomendación",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.Ink
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = recommendation,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.Ink
          )
        }

        // Botón de regreso
        AppButton(
          text = "Volver al historial",
          onClick = { onTabClick(Routes.History) },
          modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
          containerColor = Color(0xFFBBA0E5),
          contentColor = AppColors.Ink
        )

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

// Tarjeta de cada sensor con su lectura máxima
@Composable
private fun SensorReportCard(sensor: SensorSummary) {
  InfoCard(
    modifier = Modifier.fillMaxWidth(),
    containerColor = sensor.cardColor
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = sensor.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = AppColors.Ink
        )

        Text(
          text = sensor.description,
          style = MaterialTheme.typography.bodySmall,
          color = AppColors.Ink
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = sensor.maxValue ?: "—",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.ExtraBold,
          color = AppColors.Ink
        )

        Text(
          text = "máximo",
          style = MaterialTheme.typography.labelSmall,
          color = AppColors.Ink
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LevelBar(
      fraction = 0f,
      color = AppColors.Ink
    )

    Spacer(modifier = Modifier.height(8.dp))

    StatusPill(
      text = if (sensor.maxValue == null) "Sin datos" else "Registrado",
      containerColor = AppColors.Sand
    )
  }
}

@Composable
private fun ReportDetail(
  label: String,
  value: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = AppColors.Ink
    )

    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Bold,
      color = AppColors.Ink
    )
  }
}