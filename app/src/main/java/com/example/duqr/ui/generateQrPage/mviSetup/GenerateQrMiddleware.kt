package com.example.duqr.ui.generateQrPage.mviSetup

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import com.example.duqr.ConnectivityHelper
import com.example.duqr.MainActivity
import com.example.duqr.data.remote.ApiService
import com.example.duqr.mviSetup.Intent
import com.example.duqr.mviSetup.Middleware
import com.example.duqr.mviSetup.State
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.http.Multipart
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GenerateQrMiddleware @Inject constructor(
    @ApplicationContext private val context: Context,
    val apiService: ApiService,
) : Middleware<GeneratorPageIntent, GeneratorPageState> {
    private val TAG = "GenerateQrMiddleware"
    override suspend fun dispatch(
        intent: GeneratorPageIntent,
        state: GeneratorPageState,
        newIntent: (GeneratorPageIntent) -> Unit
    ) {
        if (intent is GeneratorPageIntent.GenerateQrButtonClicked) {
            val uri = state.avaImage
            val multipart = uri?.toMultiPart()

            try {
                val response = apiService.getQrCode(
                    state.url,
                    state.color.toString(),
                    multipart
                )
                newIntent.invoke(GeneratorPageIntent.QrCodeGenerated(response.url))
                Log.d(TAG, "dispatch: remote response -> ${response.url}")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "dispatch: remote exception ${e.message}")
            }
        }
    }

    private fun Uri.toMultiPart(): MultipartBody.Part {
        val openInputStream = context.contentResolver?.openInputStream(this)
        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(context.filesDir, fileName)
        val fileOutputStream = FileOutputStream(file)
        openInputStream?.copyTo(fileOutputStream)
        openInputStream?.close()
        fileOutputStream.close()
        val reqBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("user_image", file.name, reqBody)
    }
}