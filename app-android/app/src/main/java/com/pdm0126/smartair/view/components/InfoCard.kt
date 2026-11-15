package com.pdm0126.smartair.view.components
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InfoCard(
  modifier: Modifier = Modifier,
  containerColor: Color = Color.White,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(24.dp)
  val colors = CardDefaults.cardColors(containerColor = containerColor)

  if (onClick == null) {
    Card(modifier = modifier.fillMaxWidth(), shape = shape, colors = colors) {
      Column(modifier = Modifier.padding(16.dp), content = content)
    }
  } else {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = shape, colors = colors) {
      Column(modifier = Modifier.padding(16.dp), content = content)
    }
  }
}
