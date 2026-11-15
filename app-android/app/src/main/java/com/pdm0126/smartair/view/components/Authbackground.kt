package com.pdm0126.smartair.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val BgCoral = Color(0xFFF26B4F)
private val BgCoralLight = Color(0xFFFF8A6B)
private val BgLila = Color(0xFFBBA0E5)
private val BgCeleste = Color(0xFFAECBF2)
private val BgMenta = Color(0xFFB0DDC7)
private val BgAmarillo = Color(0xFFF7D68A)

// Fondo con manchas de colores, igual que la pantalla de bienvenida.
// variant 0 = inicio de sesión, variant 1 = registro (distinta distribución).
@Composable
fun AuthBackground(variant: Int = 0) {
    Box(modifier = Modifier.fillMaxSize().clipToBounds()) {

        Canvas(modifier = Modifier.fillMaxSize().clipToBounds()) {
            val w = size.width
            val h = size.height

            if (variant == 0) {
                // Arriba
                drawCircle(BgCoral.copy(alpha = 0.9f), radius = w * 0.36f, center = Offset(w * 1.00f, h * 0.00f))
                drawCircle(BgCoralLight.copy(alpha = 0.55f), radius = w * 0.20f, center = Offset(w * 0.64f, h * -0.01f))
                drawCircle(BgLila.copy(alpha = 0.85f), radius = w * 0.26f, center = Offset(w * 0.00f, h * 0.10f))

                // Costado
                drawCircle(BgAmarillo.copy(alpha = 0.85f), radius = w * 0.14f, center = Offset(w * 1.00f, h * 0.52f))

                // Abajo
                drawCircle(BgCeleste.copy(alpha = 0.95f), radius = w * 0.36f, center = Offset(w * 0.00f, h * 1.00f))
                drawCircle(BgMenta.copy(alpha = 0.95f), radius = w * 0.24f, center = Offset(w * 0.46f, h * 1.04f))
                drawCircle(BgLila.copy(alpha = 0.75f), radius = w * 0.22f, center = Offset(w * 1.00f, h * 0.96f))
            } else {
                // Arriba
                drawCircle(BgCeleste.copy(alpha = 0.95f), radius = w * 0.34f, center = Offset(w * 1.00f, h * 0.00f))
                drawCircle(BgMenta.copy(alpha = 0.90f), radius = w * 0.24f, center = Offset(w * 0.00f, h * 0.02f))
                drawCircle(BgAmarillo.copy(alpha = 0.80f), radius = w * 0.14f, center = Offset(w * 0.52f, h * -0.01f))

                // Costado
                drawCircle(BgCoral.copy(alpha = 0.85f), radius = w * 0.16f, center = Offset(w * 0.00f, h * 0.48f))

                // Abajo
                drawCircle(BgLila.copy(alpha = 0.85f), radius = w * 0.34f, center = Offset(w * 1.00f, h * 1.00f))
                drawCircle(BgAmarillo.copy(alpha = 0.90f), radius = w * 0.22f, center = Offset(w * 0.12f, h * 1.02f))
                drawCircle(BgCoralLight.copy(alpha = 0.60f), radius = w * 0.18f, center = Offset(w * 0.58f, h * 1.05f))
            }
        }

        // Burbujitas de adorno
        if (variant == 0) {
            AuthBubble(Modifier.align(Alignment.TopStart).offset(x = 80.dp, y = 70.dp), 18.dp, BgCoral)
            AuthBubble(Modifier.align(Alignment.TopEnd).offset(x = (-30).dp, y = 130.dp), 12.dp, BgLila)
            AuthBubble(Modifier.align(Alignment.BottomEnd).offset(x = (-70).dp, y = (-120).dp), 16.dp, BgMenta)
        } else {
            AuthBubble(Modifier.align(Alignment.TopStart).offset(x = 110.dp, y = 90.dp), 16.dp, BgLila)
            AuthBubble(Modifier.align(Alignment.TopEnd).offset(x = (-50).dp, y = 120.dp), 12.dp, BgCoral)
            AuthBubble(Modifier.align(Alignment.BottomStart).offset(x = 40.dp, y = (-130).dp), 18.dp, BgCeleste)
        }
    }
}

@Composable
private fun AuthBubble(modifier: Modifier, size: Dp, color: Color) {
    Box(
        modifier = modifier
            .size(size)
            .border(3.dp, color, CircleShape)
    )
}