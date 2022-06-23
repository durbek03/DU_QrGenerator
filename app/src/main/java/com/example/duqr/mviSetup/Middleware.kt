package com.example.duqr.mviSetup

interface Middleware<I : Intent, S: State> {
    suspend fun dispatch(
        intent: I,
        state: S,
        newIntent: (I) -> Unit
    )
}