package com.pdm0126.smartair.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.ui.AppColors
import com.pdm0126.smartair.utils.toFraction


@Composable
fun MiniBarChart(
  values: List<Int>,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(60.dp),
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    verticalAlignment = Alignment.Bottom
  ) {
    values.forEach { value ->
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight(value.toFraction())
          .clip(RoundedCornerShape(4.dp))
          .background(AppColors.Blue)
      )
    }
  }
}
