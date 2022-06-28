package com.example.duqr.ui.qrGalleryPage

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.IntentSender
import android.database.ContentObserver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.example.duqr.ui.qrGalleryPage.models.QrImage
import com.example.duqr.ui.qrGalleryPage.useCases.GetQrImagesUseCase
import com.example.duqr.ui.useCase.ShareQrUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class GalleryPageViewModel @Inject constructor(
    application: Application,
    val shareQrUseCase: ShareQrUseCase
) : AndroidViewModel(application) {
    lateinit var contentObserver: ContentObserver

    private val _qrCodeList = MutableStateFlow<List<QrImage>>(emptyList())
    val qrCodeList: Flow<List<QrImage>> = _qrCodeList

    fun initContentObserver() {
        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                getQrImages()
                super.onChange(selfChange)
            }
        }
        val contentResolver = getApplication<Application>().applicationContext.contentResolver
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    fun getQrImages() {
        val useCase = GetQrImagesUseCase()
        val qrImages = useCase.getQrImages(getApplication<Application>().applicationContext)
        _qrCodeList.value = qrImages
    }

    fun shareImage(bitmap: Bitmap, context: Context) {
        shareQrUseCase.shareQr(context, bitmap)
    }

    fun deleteImage(uri: Uri, _intentSender: (IntentSender) -> Unit): Boolean {
        val context = getApplication<Application>().applicationContext
        val contentResolver = context.contentResolver
        try {
            contentResolver.delete(uri, null, null)
            return true
        } catch (e: SecurityException) {
            val intentSender = when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> {
                    MediaStore.createDeleteRequest(contentResolver, listOf(uri)).intentSender
                }
                Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                    val recoverableSecurityException = e as RecoverableSecurityException
                    recoverableSecurityException.userAction.actionIntent.intentSender
                }
                else -> null
            }
            if (intentSender != null) {
                _intentSender.invoke(intentSender)
            }
        }
        return true
    }
}