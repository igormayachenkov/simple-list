package ru.igormayachenkov.list.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary         = Color(0xFF101F7F),
    primaryVariant  = Purple700,
    secondary       = Teal200,
    background      = Color.Black,
    surface         = Color(0xFF333333),
    onPrimary       = Color.White,
    onSurface       = Color.White,
    error           = Color.Yellow//(0xFFCF6679),
    )

private val LightColorPalette = lightColors(
    primary         = Color(0xFF3F51B5),
    primaryVariant  = Color(0xFF303F9F),
    secondary       = Teal200,
    background      = Color(0xFFE0E0E0),
    surface         = Color.White,
    onPrimary       = Color.White,
    onSurface       = Color.Black,
    error           = Color(0xFFB00020)
)

// Use with MaterialTheme.colors.snackbarAction
val Colors.onSurfaceDisabled: Color
    get() = if (isLight) Color(0xFF888888)
            else         Color(0xFFAAAAAA)


@Composable
fun ListTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}