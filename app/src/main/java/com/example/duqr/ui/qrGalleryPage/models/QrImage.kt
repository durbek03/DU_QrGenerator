package com.example.duqr.ui.qrGalleryPage.models

import android.graphics.Bitmap
import android.net.Uri

data class QrImage (
    val fileName: String,
    val bitmap: Bitmap,
    val uri: Uri
)