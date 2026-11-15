package com.pdm0126.smartair.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.ui.AppColors

internal val Coral = Color(0xFFF26B4F)
internal val CoralLight = Color(0xFFFF8A6B)
internal val Cream = Color(0xFFFFF4E6)

// Borde inferior ondulado de la cabecera (el mismo de Home)
private val WaveShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(0f, 0f)
    lineTo(w, 0f)
    lineTo(w, h * 0.81f)
    cubicTo(w, h * 0.945f, w * 0.88f, h * 0.976f, w * 0.76f, h * 0.93f)
    cubicTo(w * 0.58f, h * 0.87f, w * 0.43f, h * 1.01f, w * 0.22f, h * 0.95f)
    cubicTo(w * 0.08f, h * 0.92f, 0f, h * 0.90f, 0f, h * 0.81f)
    close()
}

// Cabecera coral con título y subtítulo. Si onBack es null no muestra el botón de regresar.
@Composable
internal fun AuthHeader(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(WaveShape)
            .background(Brush.verticalGradient(listOf(CoralLight, Coral)))
    ) {
        AuthBubble(Modifier.align(Alignment.TopEnd).offset(x = (-30).dp, y = 70.dp), 22.dp)
        AuthBubble(Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 105.dp), 12.dp)
        AuthBubble(Modifier.align(Alignment.BottomStart).offset(x = 28.dp, y = (-26).dp), 14.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 52.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(Cream, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = AppColors.Ink
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AuthBubble(modifier: Modifier, size: Dp) {
    Box(
        modifier = modifier
            .size(size)
            .border(3.dp, Cream.copy(alpha = 0.8f), CircleShape)
    )
}

// Campo de texto con los colores de la app. Si isPassword es true agrega el ojito para mostrar/ocultar.
@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Purple,
            unfocusedBorderColor = AppColors.Ink.copy(alpha = 0.35f),
            focusedLabelColor = AppColors.Purple,
            unfocusedLabelColor = AppColors.Ink,
            focusedTextColor = AppColors.Ink,
            unfocusedTextColor = AppColors.Ink,
            cursorColor = AppColors.Ink,
            focusedLeadingIconColor = AppColors.Purple,
            unfocusedLeadingIconColor = AppColors.Ink,
            focusedTrailingIconColor = AppColors.Purple,
            unfocusedTrailingIconColor = AppColors.Ink,
            focusedContainerColor = Cream,
            unfocusedContainerColor = Cream
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// Texto de enlace, por ejemplo "¿No tienes cuenta? Regístrate"
@Composable
internal fun AuthLink(
    text: String,
    action: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$text ",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.Ink
        )

        Text(
            text = action,
            modifier = Modifier.clickable(onClick = onClick),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = Coral
        )
    }
}