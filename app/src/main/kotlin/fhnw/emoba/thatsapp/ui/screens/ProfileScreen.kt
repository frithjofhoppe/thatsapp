package fhnw.emoba.thatsapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.R.drawable
import androidx.compose.ui.res.painterResource

@Composable
fun ProfileScreen(appModel: ThatsAppModel) {
    with(appModel) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = txtUserName,
                    onValueChange = { txtUserName = it },
                    label = { Text("User Name") },
                    singleLine = true,
                    enabled = !isSubscribed
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = txtGreeting,
                    onValueChange = { txtGreeting = it },
                    label = { Text("Greeting") },
                    singleLine = true,
                    enabled = !isSubscribed
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(avatars.size) { index ->
                        val avatar = avatars.keys.toList()[index]
                        Image(
                            painter = painterResource(id = avatars[avatar]!!),
                            contentDescription = null,
                            modifier = Modifier
                                .size(128.dp)
                                .clickable {
                                    if (!isSubscribed) {
                                        imgAvatar = avatar
                                    }
                                }
                                .border(
                                    width = 2.dp,
                                    color = if (imgAvatar == avatar) Color.Blue else Color.Transparent
                                )
                        )
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { updateProfile() },
                    enabled = txtUserName.isNotBlank() && txtGreeting.isNotBlank()  && imgAvatar.isNotBlank() && !isSubscribed
                ) {
                    Text("Register profile")
                }
            }
        }
    }
}