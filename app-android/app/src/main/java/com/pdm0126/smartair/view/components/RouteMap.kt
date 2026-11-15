package com.pdm0126.smartair.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.model.RiskLevel
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.utils.color


// Dibuja el zigzag como filas que alternan de dirección (una fila = un tramo)
@Composable
fun RouteMap(
  total: Int,
  results: Map<Int, RiskLevel>,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    for (segment in 1..total) {
      val barColor = results[segment]?.color() ?: AppColors.Sand
      val goesRight = segment % 2 == 1
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (!goesRight) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp), tint = AppColors.Ink)
        }
        Box(
          modifier = Modifier
            .weight(1f)
            .height(14.dp)
            .clip(RoundedCornerShape(50))
            .background(barColor)
        )
        if (goesRight) {
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp), tint = AppColors.Ink)
        }
      }
    }
  }
}
