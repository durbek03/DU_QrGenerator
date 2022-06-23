package com.example.duqr

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.duqr.ui.appPage.AppPage
import com.example.duqr.ui.generateQrPage.GeneratorPage
import com.example.duqr.ui.theme.DUQRTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private var exitable = false
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        handler = Handler(Looper.getMainLooper())
        setContent {
            DUQRTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.primary
                ) {
                    AppPage()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (exitable) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press one more to exit", Toast.LENGTH_SHORT).show()
            handler.postDelayed({ exitable = false }, 2000)
            exitable = true
        }
    }
}
