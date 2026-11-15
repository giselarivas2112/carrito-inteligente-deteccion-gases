
package com.pdm0126.smartair.view.screens.Alert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.view.components.AppButton
import com.pdm0126.smartair.view.components.InfoCard
import com.pdm0126.smartair.view.components.StatusPill

@Composable
fun AlertScreen(
  navigateToReport: () -> Unit,
  navigateBack: () -> Unit
) {


    // Encabezado
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = navigateBack
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Regresar",
          tint = Color.White
        )
      }

      Text(
        text = "Alerta de seguridad",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Indicador de alarma
    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      StatusPill(
        text = "ALERTA ACTIVA",
        containerColor = AppColors.Cream,
        contentColor = AppColors.Danger
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Icono de peligro
    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Filled.Warning,
        contentDescription = "Advertencia de peligro",
        tint = AppColors.Cream,
        modifier = Modifier.height(90.dp)
      )
    }

    // Mensaje principal
    Text(
      text = "¡Peligro detectado!",
      style = MaterialTheme.typography.displaySmall,
      fontWeight = FontWeight.ExtraBold,
      color = Color.White,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth()
    )

    Text(
      text = "Se ha identificado una condición peligrosa durante la inspección. El carrito se ha detenido.",
      style = MaterialTheme.typography.bodyLarge,
      color = Color.White,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth()
    )

    // Información de la detección
    InfoCard(
      containerColor = Color.White
    ) {
      Text(
        text = "Información de la alerta",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = AppColors.Ink
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Causa detectada",
        style = MaterialTheme.typography.labelMedium,
        color = Color.Gray
      )

      Text(
        text = "Nivel elevado de gas o presencia de llama",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = AppColors.Danger
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Tramo afectado",
        style = MaterialTheme.typography.labelMedium,
        color = Color.Gray
      )

      Text(
        text = "Tramo —",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = AppColors.Ink
      )

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Nivel de riesgo",
        style = MaterialTheme.typography.labelMedium,
        color = Color.Gray
      )

      StatusPill(
        text = "PELIGRO",
        containerColor = AppColors.Danger,
        contentColor = Color.White
      )
    }

    // Recomendación de seguridad
    InfoCard(
      containerColor = Color.White.copy(alpha = 0.16f)
    ) {
      Text(
        text = "Recomendación de seguridad",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "No ingreses a la zona inspeccionada. Mantén una distancia segura y espera a que el área sea evaluada por personal capacitado.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White
      )
    }

    // Acciones
    Text(
      text = "¿Qué deseas hacer?",
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.ExtraBold,
      color = Color.White
    )

    AppButton(
      text = "Silenciar alarma",
      onClick = {},
      modifier = Modifier.fillMaxWidth(),
      containerColor = AppColors.Cream,
      contentColor = AppColors.Ink
    )

    AppButton(
      text = "Ver reporte",
      onClick = navigateToReport,
      modifier = Modifier.fillMaxWidth(),
      containerColor = Color.White,
      contentColor = AppColors.Danger
    )

    AppButton(
      text = "Volver",
      onClick = navigateBack,
      modifier = Modifier.fillMaxWidth(),
      containerColor = Color.White.copy(alpha = 0.16f),
      contentColor = Color.White
    )

    Spacer(modifier = Modifier.height(12.dp))

}
