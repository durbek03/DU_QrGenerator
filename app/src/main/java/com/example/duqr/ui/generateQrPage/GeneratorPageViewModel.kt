package com.example.duqr.ui.generateQrPage

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duqr.mviSetup.Reducer
import com.example.duqr.mviSetup.Store
import com.example.duqr.ui.generateQrPage.mviSetup.GenerateQrMiddleware
import com.example.duqr.ui.generateQrPage.mviSetup.GeneratorPageIntent
import com.example.duqr.ui.generateQrPage.mviSetup.GeneratorPageReducer
import com.example.duqr.ui.generateQrPage.mviSetup.GeneratorPageState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneratorPageViewModel @Inject constructor(
    middleware: GenerateQrMiddleware,
    reducer: GeneratorPageReducer,
) : ViewModel() {
    private val TAG = "GeneratorPageViewModel"

    private val _state = MutableStateFlow(GeneratorPageState(null, ""))
    val state: Flow<GeneratorPageState> = _state

    private val store = Store<GeneratorPageIntent, GeneratorPageState>(
        _state.value,
        reducer,
        middlewares = listOf(middleware),
        this
    )

    fun onTextFieldChange(newText: String) {
        store.dispatch(GeneratorPageIntent.TextFieldChanged(newText = newText))
    }

    fun onAvaImagePicked(uri: Uri?) {
        store.dispatch(GeneratorPageIntent.AvaImagePicked(uri))
    }

    fun onColorPicked(color: String) {
        store.dispatch(GeneratorPageIntent.ColorPicked(color))
    }

    fun onGenerateButtonClicked() {
        store.dispatch(GeneratorPageIntent.GenerateQrButtonClicked)
    }

    fun showProgressBar() {
        store.dispatch(GeneratorPageIntent.ChangeLoaderVisibility(true))
    }

    fun onSaveButtonClicked() {

    }

    init {
        viewModelScope.launch {
            store.state.collectLatest {
                Log.d(TAG, "newStateTracker: ${it.toString()}")
                _state.emit(it)
            }
        }
    }
}