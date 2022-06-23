package com.example.duqr.mviSetup

interface Reducer<I: Intent, S: State> {
    fun reduce(intent: I, state: S) : S
}