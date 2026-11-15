package com.pdm0126.smartair.view.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pdm0126.smartair.R

@Composable
fun CloudImage(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    @DrawableRes image: Int = R.drawable.nubegris
) {
    Image(
        painter = painterResource(id = image),
        contentDescription = "Nube mascota de Smart Air",
        modifier = modifier.width(size),
        contentScale = ContentScale.Fit
    )
}