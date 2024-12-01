package fhnw.emoba.blockbuster.ui.themes

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

public val mainColorScheme = lightColorScheme(
    //Background colors
    primary          = Color(0xff8fbcd9),
    primaryContainer = Color(0xff7d4785),
    secondary        = Color(0xfaecbbff),

    secondaryContainer = Color(0xfff6d8b8),
    background       = Color(0x00000000),
    surface          = Color(0xff4d2b52),

    error            = amber900,

    //Typography and icon colors
    onPrimary        = lightBlue50,
    onSecondary      = Color.Black,
    onBackground     = Color.Black,
    onSurface        = Color.Black,
    onError          = Color.White,
)
