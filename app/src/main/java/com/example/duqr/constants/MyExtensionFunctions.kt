package com.example.duqr.constants

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.example.duqr.BuildConfig
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

fun <T> sdk29AndUp (onSdk29: () -> T) : T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29.invoke()
    } else {
        return null
    }
}