package com.pdm0126.smartair.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.ui.AppColors

@Composable
fun StatusPill(
  text: String,
  modifier: Modifier = Modifier,
  containerColor: Color = AppColors.Mint,
  contentColor: Color = AppColors.Ink
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(50))
      .background(containerColor)
      .padding(horizontal = 12.dp, vertical = 4.dp)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelMedium,
      fontWeight = FontWeight.Medium,
      color = contentColor
    )
  }
}
