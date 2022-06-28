package com.example.duqr.ui.qrGalleryPage.useCases

import android.content.ContentUris
import android.content.Context
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import com.example.duqr.constants.Constants
import com.example.duqr.constants.sdk29AndUp
import com.example.duqr.ui.qrGalleryPage.models.QrImage

class GetQrImagesUseCase {

    fun getQrImages(context: Context) : List<QrImage> {
        val imageList = mutableListOf<QrImage>()
        val contentResolver = context.contentResolver
        val imageCollection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
        )

        contentResolver.query(imageCollection, projection, null, null, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                var displayName = cursor.getString(displayNameColumn)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    if (!displayName.contains(Constants.GALLERY_IMAGE_ENDKEY)) {
                        continue
                    }
                }
                displayName = displayName.substring(Constants.GALLERY_IMAGE_ENDKEY.length)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id,
                )
                val bitmap = sdk29AndUp {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } ?: MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imageList.add(QrImage(displayName, bitmap, uri))
            }
        }
        return imageList.toList()
    }
}