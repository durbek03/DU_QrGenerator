package com.example.duqr.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.duqr.MainActivity
import com.example.duqr.R
import com.example.duqr.ui.theme.DUQRTheme

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private val TAG = "SplashActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: recreated")
        setContent {
            DUQRTheme {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(
                        R.raw.qr_lottie_animation
                    )
                )
                val progress by animateLottieCompositionAsState(composition = composition)
                if (progress == 1f) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.primary),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f, false)
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = progress,
                            contentScale = ContentScale.Fit
                        )
                    }
                    Text(
                        text = "DU QRCode Generator",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}