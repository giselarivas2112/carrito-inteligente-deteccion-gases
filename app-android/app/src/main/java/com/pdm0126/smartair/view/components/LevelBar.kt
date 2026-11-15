package com.pdm0126.smartair.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.ui.AppColors

@Composable
fun LevelBar(
  fraction: Float,
  color: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(10.dp)
      .clip(RoundedCornerShape(50))
      .background(AppColors.Sand)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(fraction)
        .fillMaxHeight()
        .background(color)
    )
  }
}
