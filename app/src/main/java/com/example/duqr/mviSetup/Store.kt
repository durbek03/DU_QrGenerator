package com.example.duqr.mviSetup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Store<I : Intent, S : State>(
    initialState: S,
    private val reducer: Reducer<I, S>,
    private val middlewares: List<Middleware<I, S>> = emptyList(),
    private val viewModel: ViewModel
) {
    private val TAG = "Store"
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state

    fun dispatch(intent: I) {
        val oldState = _state.value
        Log.d(TAG, "dispatch: newStoreIntent -> $intent")

        viewModel.viewModelScope.launch(Dispatchers.IO) {
            middlewares.filterThrough(intent, oldState) { newIntent ->
                Log.d(TAG, "dispatch: middleWareIntent -> $newIntent")
                val newState = reducer.reduce(newIntent, oldState)
                Log.d(TAG, "dispatch: newState -> ${newState.toString()}")
                _state.value = newState
            }
        }
    }

    private suspend fun List<Middleware<I, S>>.filterThrough(
        intent: I,
        state: S,
        newIntent: (I) -> Unit
    ) {
        var previousIntent: I = intent
        for (i in this@filterThrough.indices) {
            this@filterThrough[i].dispatch(previousIntent, state) { filteredIntent ->
                if (i == this.lastIndex) {
                    newIntent.invoke(filteredIntent)
                } else {
                    previousIntent = filteredIntent
                }
            }
        }
    }
}