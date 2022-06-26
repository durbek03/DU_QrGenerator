package com.example.duqr.ui.generateQrPage.mviSetup

import android.graphics.Bitmap
import android.net.Uri
import com.example.duqr.mviSetup.State

data class GeneratorPageState(
    var qrCode: Bitmap? = null,
    var textToEmbed: String = "",
    var color: String = "#ffffff",
    var avaImage: Uri? = null,
    var loaderOn: Boolean = false
) : State