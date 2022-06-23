package com.example.duqr.ui.generateQrPage.mviSetup

import android.content.Context
import com.example.duqr.mviSetup.Middleware
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SaveQrToInternalStorageMiddleware @Inject constructor(
    @ApplicationContext val context: Context
) : Middleware<GeneratorPageIntent, GeneratorPageState> {

    override suspend fun dispatch(
        intent: GeneratorPageIntent,
        state: GeneratorPageState,
        newIntent: (GeneratorPageIntent) -> Unit
    ) {
        if (intent is GeneratorPageIntent.SaveQrImageToInternalStorage) {
            try {
                context.openFileOutput(intent.filename, Context.MODE_PRIVATE).use {
                }
                //if no exception occurs, then show toast which indicates that image has been successfully saved
            } catch (e: Exception) {
                //if exception occurs who toast which indicates that there was an error
            }
        }
    }
}