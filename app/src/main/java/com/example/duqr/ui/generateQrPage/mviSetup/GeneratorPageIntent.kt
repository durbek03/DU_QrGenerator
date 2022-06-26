package com.example.duqr.ui.generateQrPage.mviSetup

import android.graphics.Bitmap
import android.net.Uri
import com.example.duqr.mviSetup.Intent


sealed class GeneratorPageIntent : Intent {
    data class TextFieldStateChanged(val textToEmbed: String) : GeneratorPageIntent()
    data class AvaImagePicked(val newImage: Uri?) : GeneratorPageIntent()
    data class ColorPicked(val color: String) : GeneratorPageIntent()
    object GenerateQr : GeneratorPageIntent()
    data class QrCodeBitmapGenerated(val newQr: Bitmap) : GeneratorPageIntent()
    data class QrCodeUrlFetched(val qrCodeUrl: String) : GeneratorPageIntent()
    data class ChangeLoaderVisibility(val isVisible: Boolean) : GeneratorPageIntent()
}
