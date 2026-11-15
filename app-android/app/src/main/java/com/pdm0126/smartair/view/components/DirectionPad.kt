package com.pdm0126.smartair.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.model.CarCommand
import com.pdm0126.smartair.ui.AppColors


@Composable
private fun DirectionButton(
  icon: ImageVector,
  description: String,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    modifier = Modifier.size(92.dp),
    shape = RoundedCornerShape(24.dp),
    contentPadding = PaddingValues(0.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = AppColors.Blue,
      contentColor = Color.White
    )
  ) {
    Icon(imageVector = icon, contentDescription = description, modifier = Modifier.size(36.dp))
  }
}

@Composable
fun DirectionPad(onCommand: (CarCommand) -> Unit) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    DirectionButton(Icons.Filled.KeyboardArrowUp, "Adelante") { onCommand(CarCommand.FORWARD) }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      DirectionButton(Icons.AutoMirrored.Filled.ArrowBack, "Izquierda") { onCommand(CarCommand.LEFT) }
      Button(
        onClick = { onCommand(CarCommand.STOP) },
        modifier = Modifier.size(92.dp),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = AppColors.Danger,
          contentColor = Color.White
        )
      ) {
        Text(text = "STOP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      }
      DirectionButton(Icons.AutoMirrored.Filled.ArrowForward, "Derecha") { onCommand(CarCommand.RIGHT) }
    }
    DirectionButton(Icons.Filled.KeyboardArrowDown, "Atrás") { onCommand(CarCommand.BACK) }
  }
}
