package fhnw.emoba.thatsapp.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fhnw.emoba.thatsapp.data.models.Message
import fhnw.emoba.thatsapp.data.models.blocks.ImageBlock
import fhnw.emoba.thatsapp.data.models.blocks.LocationBlock
import fhnw.emoba.thatsapp.data.models.blocks.TextBlock
import fhnw.emoba.thatsapp.model.ThatsAppModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun ChatScreen(appModel: ThatsAppModel) {
    with(appModel) {
        if (photo != null && isPhotoTaken) {
            PhotoDialog(appModel)
        } else {
            Body(appModel)
        }
    }
}

@Composable
private fun Body(appModel: ThatsAppModel) {
    with(appModel) {

        val listState = rememberLazyListState()

        LaunchedEffect(appModel.selectedChat?.messages?.size) {
            // Delay to ensure the new message is added before scrolling
            delay(100)
            listState.animateScrollToItem(appModel.selectedChat?.messages?.size ?: 0)
        }

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
                MessageField(onClick = { sendTextMessage() }, appModel = appModel, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
private fun PhotoDialog(appModel: ThatsAppModel) {
    with (appModel) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
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
                                    downloadedImages =
                                        downloadedImages.plus(Pair(link, Pair(false, photo)))
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
                                    txtMessage = ""
                                })
                        },
                        appModel = appModel
                    )
                }
            }
            if (isUploadingImage) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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
            Text(text = message.timestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")), fontSize = 10.sp)
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
        } else {
            LinearProgressIndicator()
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