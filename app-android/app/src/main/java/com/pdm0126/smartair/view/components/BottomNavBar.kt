package com.pdm0126.smartair.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.navigation.Routes
import com.pdm0126.smartair.ui.AppColors


@Composable
fun BottomNavBar(
  selected: Routes,
  onTabClick: (Routes) -> Unit
) {
  val tabs = listOf<Pair<String, Routes>>(
    "Inicio" to Routes.Home,
    "Reportes" to Routes.History,
    "Mi perfil" to Routes.Profile
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
      .clip(RoundedCornerShape(26.dp))
      .background(Color.White)
      .padding(8.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    tabs.forEach { (label, route) ->
      val active = route == selected
      Button(
        onClick = { onTabClick(route) },
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(18.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (active) AppColors.Coral else Color.Transparent,
          contentColor = if (active) Color.White else AppColors.Ink
        )
      ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, maxLines = 1)
      }
    }
  }
}
