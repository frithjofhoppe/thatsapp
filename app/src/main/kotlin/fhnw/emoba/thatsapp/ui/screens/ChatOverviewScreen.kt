package fhnw.emoba.thatsapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fhnw.emoba.R
import fhnw.emoba.thatsapp.data.Chat
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.model.ThatsAppModel.avatars

@Composable
fun ChatOverviewScreen(appModel: ThatsAppModel) {
    with(appModel) {
        if (!isSubscribed) {
            Text("Please subscribe to a chat first")
        } else if(chatStore.chats.isEmpty()) {
            Text("No users found. Tell your friends to join ThatsApp!")
        } else {
            LazyColumn (
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                items(chatStore.chats) { chat ->
                    ChatPreview(chat, appModel)
                }
            }
        }
    }
}

@Composable
fun ChatPreview(chat: Chat, appModel: ThatsAppModel) {
    with (appModel) {
        Column(
            Modifier.clickable {
                selectChatView(chat)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = avatars[chat.user.avatar] ?: R.drawable.bob),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                        .background(Color.White)
                        .padding(8.dp),
                )
                Spacer(modifier = Modifier.width(32.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = chat.user.name, fontWeight = FontWeight.Bold)
                    Text(text = chat.mostRecentMessage)
                }
            }
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}