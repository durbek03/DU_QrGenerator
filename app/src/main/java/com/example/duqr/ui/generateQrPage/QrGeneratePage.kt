package com.example.duqr.ui.generateQrPage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.duqr.ConnectivityHelper
import com.example.duqr.R
import com.example.duqr.constants.collectFlowAsState
import com.example.duqr.ui.generateQrPage.mviSetup.GeneratorPageState
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import kotlinx.coroutines.*

private val TAG = "QrGeneratePage"

@Composable
fun GeneratorPage(viewModel: GeneratorPageViewModel = hiltViewModel()) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val colorPickerVisible = remember {
        mutableStateOf(false)
    }
    val state = viewModel.state.collectFlowAsState(initial = GeneratorPageState())
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp, start = 30.dp, end = 30.dp)
            .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                if (colorPickerVisible.value) {
                    colorPickerVisible.value = false
                }
                focusManager.clearFocus()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        QrCode(
            qrBitmap = state.value.qrCode,
            loaderOn = state.value.loaderOn,
            modifier = Modifier.fillMaxHeight(0.23f),
            viewModel = viewModel,
            fileName = state.value.textToEmbed
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface),
            onClick = {
                generate(state = state.value, viewModel = viewModel, context, coroutineScope)
            }) {
            Text(text = "Generate", color = MaterialTheme.colors.onSurface)
        }
        Spacer(modifier = Modifier.height(20.dp))
        UrlTextField(
            focusRequester = focusRequester,
            initialValue = state.value.textToEmbed
        ) { newText ->
            viewModel.onTextFieldChange(newText)
        }
        Spacer(modifier = Modifier.height(20.dp))
        ColorPickerButton(
            pickedColor = Color(android.graphics.Color.parseColor(state.value.color)),
            colorPickerOpen = colorPickerVisible.value,
        ) {
            colorPickerVisible.value = !colorPickerVisible.value
        }
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedVisibility(visible = colorPickerVisible.value) {
            ClassicColorPicker(
                modifier = Modifier.padding(bottom = 30.dp),
                onColorChanged = { _color: HsvColor ->
                    val color = _color.toColor().toArgb()
                    val hexColor = String.format("#%06X", 0xFFFFFF and color)
                    viewModel.onColorPicked(hexColor)
                }
            )
        }
        AddPhoto(state = state.value) {
            viewModel.onAvaImagePicked(it)
        }
    }
}

fun generate(
    state: GeneratorPageState,
    viewModel: GeneratorPageViewModel,
    context: Context,
    coroutineScope: CoroutineScope
) {
    val connectivityHelper = ConnectivityHelper(context)

    if (state.textToEmbed.isEmpty()) {
        Toast.makeText(
            context,
            "No proper information provided to embed with QR code",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        coroutineScope.launch(Dispatchers.IO) {
            if (connectivityHelper.internetAvailable()) {
                Log.d(TAG, "generate: hasInternet")
                withContext(Dispatchers.Main) {
                    viewModel.showProgressBar()
                    viewModel.generateQrCode()
                    Toast.makeText(
                        context,
                        "Generating..., please wait a moment",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.d(TAG, "generate: not internet")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Internet connection not available",
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }
    }
}

@Composable
fun QrCode(
    modifier: Modifier = Modifier,
    qrBitmap: Bitmap?,
    fileName: String,
    loaderOn: Boolean,
    viewModel: GeneratorPageViewModel
) {
    val context = LocalContext.current
    val askForWritePermission =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                saveQrImageToExternalStorage(context, qrBitmap, viewModel = viewModel, fileName = fileName)
            } else {
                Toast.makeText(context, "Cannot save without permission", Toast.LENGTH_SHORT).show()
            }
        }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        ActionIcon(
            modifier = Modifier.offset(x = (-15).dp),
            iconId = R.drawable.ic_share,
            isVisible = qrBitmap != null
        ) {
            if (qrBitmap != null) {
                viewModel.onShareButtonClicked(context, qrBitmap)
            }
        }
        //qrBox
        Box(
            contentAlignment = Alignment.Center
        ) {
            if (loaderOn) {
                CircularProgressIndicator(
                    modifier = Modifier.offset(y = (-50).dp),
                    color = MaterialTheme.colors.surface
                )
            } else if (qrBitmap != null) {
                Image(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f, true),
                    contentDescription = "QR",
                    bitmap = qrBitmap.asImageBitmap()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_dashedrec),
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f, true)
                        .align(Alignment.Center),
                    contentDescription = "dashed rec"
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Here will be your generated QR code",
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        ActionIcon(
            modifier = Modifier.offset(x = (15).dp),
            iconId = R.drawable.ic_save,
            qrBitmap != null
        ) {
            val writePermissionGranted = checkIfHasWritePermission(context)
            if (writePermissionGranted) {
                saveQrImageToExternalStorage(context, bitmap = qrBitmap, viewModel = viewModel, fileName = fileName)
            } else {
                askForWritePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}

fun saveQrImageToExternalStorage(
    context: Context,
    bitmap: Bitmap?,
    fileName: String,
    viewModel: GeneratorPageViewModel
) {
    if (bitmap != null) {
        if (viewModel.saveQrImageToExternalStorage(context, bitmap, fileName)) {
            Toast.makeText(context, "Successfully saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Couldn't save image", Toast.LENGTH_SHORT).show()
        }
    }
}

fun checkIfHasWritePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

@Composable
fun RowScope.ActionIcon(
    modifier: Modifier = Modifier,
    iconId: Int,
    isVisible: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(visible = isVisible, modifier = Modifier.align(Alignment.Bottom)) {
        Icon(
            modifier = modifier
                .width(30.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colors.surface)
                .clickable {
                    onClick.invoke()
                }
                .padding(5.dp),
            painter = painterResource(id = iconId),
            contentDescription = "icon share",
            tint = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun UrlTextField(
    initialValue: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colors.primaryVariant,
        backgroundColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.4f)
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface, shape = RoundedCornerShape(5.dp))
    ) {
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = initialValue,
                onValueChange = {
                    onValueChange.invoke(it)
                },
                label = {
                    Text(
                        text = "Url to embed with QR",
                        color = MaterialTheme.colors.onSurface
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onSurface,
                    cursorColor = MaterialTheme.colors.onBackground,
                    backgroundColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }
    }
}

@Composable
fun ColorPickerButton(
    pickedColor: Color = MaterialTheme.colors.primaryVariant,
    textColor: Color = MaterialTheme.colors.onPrimary,
    colorPickerOpen: Boolean,
    onClick: () -> Unit
) {
    val openText = "Open color picker"
    val closeText = "Close color picker"
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(
            text = "??? Pick color",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colors.onPrimary
        )
        Button(
            onClick = { onClick.invoke() }, colors = ButtonDefaults.buttonColors(
                backgroundColor = pickedColor
            )
        ) {
            Text(text = if (colorPickerOpen) closeText else openText, color = textColor)
        }
    }
}

@Composable
fun AddPhoto(
    modifier: Modifier = Modifier,
    state: GeneratorPageState,
    photoPicked: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val readPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val getImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            it ?: return@rememberLauncherForActivityResult
            photoPicked.invoke(it)
        })

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                getImageLauncher.launch("image/*")
            }
            readPermissionGranted.value = it
        }
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            text = "??? Pick photo to embed with QR code",
            color = MaterialTheme.colors.onPrimary,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .clickable {
                        if (!readPermissionGranted.value) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            getImageLauncher.launch("image/*")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = state.avaImage,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    fallback = painterResource(id = R.drawable.ic_dashedrec),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                )

                androidx.compose.animation.AnimatedVisibility(state.avaImage == null) {
                    Text(
                        text = "Select image",
                        color = MaterialTheme.colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.Bottom),
                visible = state.avaImage != null
            ) {
                Icon(
                    modifier = Modifier
                        .width(25.dp)
                        .height(25.dp)
                        .clickable {
                            photoPicked.invoke(null)
                        },
                    painter = painterResource(id = R.drawable.ic_cancel),
                    contentDescription = "icon cancel",
                    tint = Color.Red
                )
            }
        }
    }
}