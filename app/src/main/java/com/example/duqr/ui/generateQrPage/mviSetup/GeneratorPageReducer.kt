package com.example.duqr.ui.generateQrPage.mviSetup

import com.example.duqr.mviSetup.Reducer
import javax.inject.Inject

class GeneratorPageReducer @Inject constructor() :
    Reducer<GeneratorPageIntent, GeneratorPageState> {
    override fun reduce(
        intent: GeneratorPageIntent,
        state: GeneratorPageState
    ): GeneratorPageState {
        when (intent) {
            is GeneratorPageIntent.TextFieldStateChanged -> {
                return state.copy(textToEmbed = intent.textToEmbed)
            }
            is GeneratorPageIntent.AvaImagePicked -> {
                return state.copy(avaImage = intent.newImage)
            }
            is GeneratorPageIntent.ColorPicked -> {
                return state.copy(color = intent.color)
            }
            is GeneratorPageIntent.QrCodeBitmapGenerated -> {
                return state.copy(qrCode = intent.newQr, loaderOn = false)
            }
            is GeneratorPageIntent.ChangeLoaderVisibility -> {
                return state.copy(loaderOn = intent.isVisible)
            }
            else -> {
                return state
            }
        }
    }
}