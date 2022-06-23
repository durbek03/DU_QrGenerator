package com.example.duqr.ui.appPage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AppViewModel : ViewModel() {
    private val _appPageState = MutableStateFlow<BottomNavigationState>(BottomNavigationState.GeneratorPage)
    val appPageState : Flow<BottomNavigationState> = _appPageState

    fun onBottomNavigationItemSelected(item: BottomNavigationState) {
        if (item != _appPageState.value) {
            _appPageState.value = item
        }
    }
}