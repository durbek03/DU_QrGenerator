package com.example.duqr.ui.generateQrPage.mviSetup

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.example.duqr.mviSetup.Middleware
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class FetchQrCodeBitmapMiddleware @Inject constructor() :
    Middleware<GeneratorPageIntent, GeneratorPageState> {
    private val TAG = "FetchQrCodeBitmapMiddle"
    override suspend fun dispatch(
        intent: GeneratorPageIntent,
        state: GeneratorPageState,
        newIntent: (GeneratorPageIntent) -> Unit
    ) {
        if (intent is GeneratorPageIntent.QrCodeUrlFetched) {
            Log.d(TAG, "dispatch: Dispatch started -> ${intent.qrCodeUrl}")
            val target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    Log.d(TAG, "onBitmapLoaded: bitmapLoaded")
                    if (bitmap != null) {
                        newIntent.invoke(GeneratorPageIntent.QrCodeBitmapGenerated(bitmap))
                    }
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            }
            withContext(Dispatchers.Main) {
                Picasso.get().load(intent.qrCodeUrl).into(target)
            }
        } else {
            newIntent.invoke(intent)
        }
    }
}