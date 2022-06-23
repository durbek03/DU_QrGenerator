package com.example.duqr.constants

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.collectFlowAsState(
    initial: T,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) : State<T> {
    val lifecycleAwareFlow = remember(key1 = this, key2 = lifecycleOwner) {
        this.flowWithLifecycle(
            lifecycle = lifecycleOwner.lifecycle,
            minActiveState = Lifecycle.State.STARTED
        )
    }
    return lifecycleAwareFlow.collectAsState(initial = initial)
}