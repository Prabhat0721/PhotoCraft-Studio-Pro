package com.prabhat.photocraftstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.prabhat.photocraftstudio.core.designsystem.theme.PhotoCraftTheme
import com.prabhat.photocraftstudio.ui.PhotoCraftApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(0xFF101114.toInt()),
            navigationBarStyle = SystemBarStyle.dark(0xFF101114.toInt()),
        )
        setContent {
            PhotoCraftTheme {
                PhotoCraftApp()
            }
        }
    }
}
