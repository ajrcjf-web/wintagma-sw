// /android/android/src/main/java/com/wintagma/android/ui/theme/Theme.kt
package com.wintagma.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = WintagmaPrimary,
    secondary = WintagmaSecondary,
    background = WintagmaBackground,
)

@Composable
fun WintagmaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}
