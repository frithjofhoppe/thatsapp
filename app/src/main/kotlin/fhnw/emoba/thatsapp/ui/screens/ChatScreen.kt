package fhnw.emoba.thatsapp.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import fhnw.emoba.thatsapp.data.connectors.downloadBitmapFromFileIO
import fhnw.emoba.thatsapp.data.connectors.uploadBitmapToFileIO
import fhnw.emoba.thatsapp.data.models.Message
import fhnw.emoba.thatsapp.data.models.blocks.ImageBlock
import fhnw.emoba.thatsapp.data.models.blocks.LocationBlock
import fhnw.emoba.thatsapp.data.models.blocks.TextBlock
import fhnw.emoba.thatsapp.model.ThatsAppModel
import kotlinx.coroutines.delay
import java.io.File
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun ChatScreen(appModel: ThatsAppModel) {
    with(appModel) {
        val listState = rememberLazyListState()

        LaunchedEffect(appModel.selectedChat?.messages?.size) {
            // Delay to ensure the new message is added before scrolling
            delay(100)
            listState.animateScrollToItem(appModel.selectedChat?.messages?.size ?: 0)
        }

        fun sendTextMessage() {
            if (txtMessage.isNotEmpty()) {
                val message = Message(
                    UUID.randomUUID(),
                    user.id,
                    LocalDateTime.now(),
                    arrayListOf(
                        TextBlock(txtMessage)
                    )
                )
                chatStore.sendMessage(selectedChat!!.user, message)
                txtMessage = ""
            }
        }

        fun sendGeoLocation() {
            trackCurrentLocation()
            val message = Message(
                UUID.randomUUID(),
                user.id,
                LocalDateTime.now(),
                arrayListOf(
                    LocationBlock(
                        geoPosition = currentLocation
                    )
                )
            )
            chatStore.sendMessage(selectedChat!!.user, message)
        }

        fun openPhotoDialog() {
            isPhotoTaken = true
            takePhoto()
        }

        if (photo != null && isPhotoTaken) {
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { isPhotoTaken = false; photo = null }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    }
                    Photo(
                        bitmap = photo!!,
                        modifier = Modifier
                    )
                    MessageField(
                        onClick = {
                            uploadChatImage(
                                bitmap = photo!!,
                                onSuccess = { link ->
                                    println("Photo uploaded with message ${link}")
                                    downloadedImages = downloadedImages.plus(Pair(link, Pair(false, photo)))
                                    println("IMG Prevent downloading by adding manually")
                                    val message = Message(
                                        UUID.randomUUID(),
                                        user.id,
                                        LocalDateTime.now(),
                                        arrayListOf(
                                            ImageBlock(link),
                                            TextBlock(txtMessage)
                                        )
                                    )
                                    messageService.publish(selectedChat!!.user.id, message)
                                    selectedChat!!.messages.add(message)
                                    photo = null
                                    isPhotoTaken = false
                                })
                        },
                        appModel = appModel
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 120.dp)
                ) {
                    items(selectedChat!!.messages) { message ->
                        MessageTile(message, appModel)
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Color.White.copy(alpha = 0.8f))
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { openPhotoDialog() }) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Take picture")
                        }
                        IconButton(
                            onClick = { sendGeoLocation() },
                            enabled = isLocationPermissonGranted
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = "Send location")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(start = 8.dp)
                    ) {
                        TextField(
                            value = txtMessage,
                            onValueChange = { txtMessage = it },
                            modifier = Modifier.weight(1f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxHeight(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { sendTextMessage() }) {
                                Icon(Icons.Filled.Send, contentDescription = "Send location")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Photo(bitmap: Bitmap, modifier: Modifier) {
    Image(bitmap = bitmap.asImageBitmap(),
        contentDescription = "",
        modifier = modifier.clickable(onClick = { println("Rotating not implemented yet") })
    )
}

@Composable
fun MessageField(onClick: () -> Unit, appModel: ThatsAppModel, modifier: Modifier = Modifier) {
    with(appModel) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            TextField(
                value = txtMessage,
                onValueChange = { txtMessage = it },
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onClick() }) {
                    Icon(Icons.Filled.Send, contentDescription = "Send location")
                }
            }
        }
    }
}

@Composable
fun MessageTile(message: Message, appModel: ThatsAppModel) {
    val isOwnMessage = message.sender == appModel.user.id
    val starPadding = if (isOwnMessage) 40.dp else 8.dp
    val endPadding = if (isOwnMessage) 8.dp else 40.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = starPadding,
                end = endPadding,
                top = 8.dp
            )
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.LightGray)
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            message.blocks.forEach { block ->
                when (block) {
                    is TextBlock -> Text(block.text)
                    is LocationBlock -> MessageLocationTile(block)
                    is ImageBlock -> MessageImageTile(block, appModel)
                }
            }
            Text(text = message.timestamp.toString(), fontSize = 10.sp)
        }
    }
}

@Composable
fun MessageImageTile(imageBlock: ImageBlock, appModel: ThatsAppModel) {
    println("IMG Container for image ${imageBlock.url}")
    with(appModel) {
        loadChatImage(imageBlock.url)
        if (downloadedImages.containsKey(imageBlock.url) && !downloadedImages.get(imageBlock.url)!!.first) {
            val img = downloadedImages.get(imageBlock.url)!!.second;
            Photo(
                bitmap = img!!,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun MessageLocationTile(locationBlock: LocationBlock) {
    Row {
        Icon(Icons.Filled.LocationOn, contentDescription = "Location")
        Text("My current location: ${locationBlock.geoPosition.latitude}, ${locationBlock.geoPosition.longitude}")
    }
}


@Composable
fun CameraBox(appModel: ThatsAppModel) {
    val context = appModel.componentActivity
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView = it }
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                val photoFile =
                    File(context.externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                            appModel.currentPhoto = bitmap
                        }

                        override fun onError(exception: ImageCaptureException) {
                            // Handle error
                        }
                    }
                )
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Capture Photo")
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView?.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }
}