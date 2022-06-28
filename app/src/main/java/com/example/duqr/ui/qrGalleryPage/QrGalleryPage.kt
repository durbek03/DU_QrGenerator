package com.example.duqr.ui.qrGalleryPage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.duqr.constants.collectFlowAsState
import com.example.duqr.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QrGalleryPage() {
    val viewModel: GalleryPageViewModel = hiltViewModel()
    val images = viewModel.qrCodeList.collectFlowAsState(initial = emptyList())
    val context = LocalContext.current
    val askForReadPermission =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                viewModel.initContentObserver()
                viewModel.getQrImages()
            } else {
                Toast.makeText(
                    context,
                    "Cannot load images without permission which you just denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    val rememberIntentSender =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == 0) {
                Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Couldn't delete image", Toast.LENGTH_SHORT).show()
            }
        }
    LaunchedEffect(key1 = true) {
        Log.d("TAG", "QrGalleryPage: ${readPermissionGranted(context)}")
        if (readPermissionGranted(context)) {
            viewModel.initContentObserver()
            viewModel.getQrImages()
        } else {
            askForReadPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    val lazyGridState = rememberLazyListState()
    LazyVerticalGrid(cells = GridCells.Fixed(2), state = lazyGridState) {
        itemsIndexed(items = images.value) { index, item ->
            QrImageItem(qrImage = item.bitmap, name = item.fileName,
                onShare = {
                    viewModel.shareImage(item.bitmap, context)
                }, onDelete = {
                    viewModel.deleteImage(item.uri) { intentSender ->
                        rememberIntentSender.launch(
                            IntentSenderRequest.Builder(intentSender).build()
                        )
                    }
                })
        }
    }
}

@Composable
fun QrImageItem(qrImage: Bitmap, name: String, onShare: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = qrImage.asImageBitmap(),
            contentDescription = "qrimage",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f, true)
                .clip(RoundedCornerShape(15.dp)),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = name,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .padding(start = 5.dp, top = 5.dp)
                .fillMaxWidth(),
            maxLines = 1
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp), horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colors.primary)
                    .clickable {
                        onShare.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(5.dp),
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "icshare",
                    tint = MaterialTheme.colors.surface
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colors.primary)
                    .clickable {
                        onDelete.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .padding(5.dp),
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "icdelete",
                    tint = MaterialTheme.colors.surface
                )
            }
        }
    }
}

fun readPermissionGranted(context: Context): Boolean {
    val hasPermissionSDK28 = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    return hasPermissionSDK28 || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}