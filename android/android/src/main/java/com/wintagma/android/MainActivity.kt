// /android/android/src/main/java/com/wintagma/android/MainActivity.kt
package com.wintagma.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wintagma.android.ui.theme.WintagmaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WintagmaTheme {
                WintagmaRoot()
            }
        }
    }
}

@Composable
fun WintagmaRoot() {
    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.background
        ) {
            Text(text = "Wintagma SW — Android Setup")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WintagmaRootPreview() {
    WintagmaTheme {
        WintagmaRoot()
    }
}
