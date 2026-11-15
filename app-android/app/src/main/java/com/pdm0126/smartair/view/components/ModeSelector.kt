package com.pdm0126.smartair.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.model.CarMode
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.utils.label


@Composable
fun ModeSelector(
  selected: CarMode,
  onSelect: (CarMode) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(AppColors.Cream)
      .padding(6.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    listOf(CarMode.MANUAL, CarMode.AUTO).forEach { mode ->
      val active = mode == selected
      Button(
        onClick = { onSelect(mode) },
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (active) AppColors.Coral else Color.Transparent,
          contentColor = if (active) Color.White else AppColors.Ink
        )
      ) {
        Text(text = mode.label())
      }
    }
  }
}
