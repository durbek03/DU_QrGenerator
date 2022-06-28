package com.example.duqr.ui.useCase

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class ShareQrUseCase @Inject constructor() {
    private val TAG = "ShareQrUseCase"
    private val fileName = "shareImage"
    fun shareQr(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // don't forget to make the directory
            val stream =
                FileOutputStream("$cachePath/$fileName.png") // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val imagePath = File(context.cacheDir, "images")
            val newFile = File(imagePath, "$fileName.png")
            val contentUri =
                FileProvider.getUriForFile(context, "com.example.duqr.fileProvider", newFile)

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
            }
            context.startActivity(Intent.createChooser(intent, "Choose app"))
        } catch (e: Exception) {
            Log.d(TAG, "shareQr: Exception -> ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
}