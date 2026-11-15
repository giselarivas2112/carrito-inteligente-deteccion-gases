package com.pdm0126.smartair.view.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.utils.toFraction

@Composable
fun SensorCard(
  name: String,
  description: String,
  value: Int,
  barColor: Color,
  modifier: Modifier = Modifier
) {
  InfoCard(modifier = modifier) {
    Text(text = description, style = MaterialTheme.typography.labelSmall)
    Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Text(text = value.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(6.dp))
    LevelBar(fraction = value.toFraction(), color = barColor)
  }
}
