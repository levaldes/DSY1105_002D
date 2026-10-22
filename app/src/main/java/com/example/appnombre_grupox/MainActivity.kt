package com.example.appnombre_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appnombre_grupox.ui.MainScreen
import com.example.appnombre_grupox.ui.theme.AppNombre_GrupoXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNombre_GrupoXTheme {
                MainScreen()
            }
        }
    }
}