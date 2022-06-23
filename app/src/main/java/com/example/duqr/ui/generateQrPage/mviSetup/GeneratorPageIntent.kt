package com.example.duqr.ui.generateQrPage.mviSetup

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.example.duqr.mviSetup.Intent


sealed class GeneratorPageIntent : Intent {
    data class TextFieldChanged(val newText: String) : GeneratorPageIntent()
    data class AvaImagePicked(val newImage: Uri?) : GeneratorPageIntent()
    data class ColorPicked(val color: String) : GeneratorPageIntent()
    object GenerateQrButtonClicked : GeneratorPageIntent()
    data class QrCodeGenerated(val newQr: String) : GeneratorPageIntent()
    data class ChangeLoaderVisibility(val isVisible: Boolean) : GeneratorPageIntent()
    data class ShareQrImage(val url: String) : GeneratorPageIntent()
    data class SaveQrImageToInternalStorage(val filename: String, val bitmap: Bitmap) : GeneratorPageIntent()
}
