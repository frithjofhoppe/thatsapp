package fhnw.emoba.thatsapp.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fhnw.emoba.R
import fhnw.emoba.blockbuster.ui.themes.mainColorScheme
import fhnw.emoba.thatsapp.model.Screen
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.screens.ChatOverviewScreen
import fhnw.emoba.thatsapp.ui.screens.ChatScreen
import fhnw.emoba.thatsapp.ui.screens.ProfileScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUI(model: ThatsAppModel) {
    with(model){
        MaterialTheme (
            colorScheme = mainColorScheme
        ) {
            Scaffold(
                topBar = { TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if(isChatViewDisplay && selectedChat != null){
                                Row (verticalAlignment = Alignment.CenterVertically) {
                                    IconButton (onClick = {
                                        selectedScreen = Screen.CHATS
                                        isChatViewDisplay = false
                                    }) {
                                        Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = Color.White)
                                    }
                                    Text(selectedChat!!.user.name, color = Color.White)
                                }
                                Image(
                                    painter = painterResource(id = avatars[selectedChat!!.user.avatar] ?: R.drawable.bob),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, Color.Black, CircleShape)
                                        .background(Color.White)
                                        .padding(8.dp),
                                )
                            } else {
                                Text(selectedScreen.title, color = Color.White)
                            }
                        }
                    }
                )},
                content = {
                    Box(
                        modifier = Modifier.padding(it).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when(selectedScreen){
                            Screen.CHATS -> ChatOverviewScreen(model)
                            Screen.PROFILE -> ProfileScreen(model)
                            Screen.CHAT -> ChatScreen(model)
                        }
                    }
                },
                bottomBar = {
                    if(!isChatViewDisplay) {
                        NavigationBar {
                            listOf(Screen.CHATS, Screen.PROFILE).map { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Text(
                                            screen.icon,
                                            style = TextStyle(fontSize = 24.sp)
                                        )
                                    },
                                    label = { Text(screen.title) },
                                    selected = selectedScreen == screen,
                                    onClick = {
                                        selectedScreen = screen
                                    }
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}