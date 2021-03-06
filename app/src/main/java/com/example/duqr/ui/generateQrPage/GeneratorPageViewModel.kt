package com.example.duqr.ui.generateQrPage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duqr.mviSetup.Store
import com.example.duqr.ui.generateQrPage.mviSetup.*
import com.example.duqr.ui.useCase.SaveQrUseCase
import com.example.duqr.ui.useCase.ShareQrUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratorPageViewModel @Inject constructor(
    fetchUrlMiddleware: FetchQrCodeUrlMiddleware,
    fetchBitmapMiddleware: FetchQrCodeBitmapMiddleware,
    val saveQrUseCase: SaveQrUseCase,
    val shareQrUseCase: ShareQrUseCase,
    reducer: GeneratorPageReducer
) : ViewModel() {
    private val TAG = "GeneratorPageViewModel"

    private val _state = MutableStateFlow(GeneratorPageState())
    val state: Flow<GeneratorPageState> = _state

    private val store = Store(
        _state.value,
        reducer,
        middlewares = listOf(fetchUrlMiddleware, fetchBitmapMiddleware),
        this
    )

    fun onTextFieldChange(newText: String) {
        store.dispatch(GeneratorPageIntent.TextFieldStateChanged(newText))
    }

    fun onAvaImagePicked(uri: Uri?) {
        store.dispatch(GeneratorPageIntent.AvaImagePicked(uri))
    }

    fun onColorPicked(color: String) {
        store.dispatch(GeneratorPageIntent.ColorPicked(color))
    }

    fun generateQrCode() {
        store.dispatch(GeneratorPageIntent.GenerateQr)
    }

    fun showProgressBar() {
        store.dispatch(GeneratorPageIntent.ChangeLoaderVisibility(true))
    }

    fun saveQrImageToExternalStorage(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        return saveQrUseCase.saveQrToExternalStorage(context, bitmap, fileName)
    }

    fun onShareButtonClicked(context: Context, bitmap: Bitmap) {
        shareQrUseCase.shareQr(context, bitmap)
    }


    init {
        viewModelScope.launch {
            store.state.collectLatest {
                Log.d(TAG, "newStateTracker: ${it.toString()}")
                _state.value = it
            }
        }
    }
}