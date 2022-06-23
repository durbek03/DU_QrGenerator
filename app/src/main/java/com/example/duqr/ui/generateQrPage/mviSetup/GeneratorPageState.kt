package com.example.duqr.ui.generateQrPage.mviSetup

import android.net.Uri
import com.example.duqr.mviSetup.State

data class GeneratorPageState(
    var qrCode: String? = null,
    var url: String = "",
    var color: String = "#ffffff",
    var avaImage: Uri? = null,
    var loaderOn: Boolean = false
) : State