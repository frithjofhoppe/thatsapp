package fhnw.emoba.thatsapp.ui


import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fhnw.emoba.blockbuster.ui.themes.mainColorScheme
import fhnw.emoba.thatsapp.model.Screen
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.screens.ChatOverviewScreen
import fhnw.emoba.thatsapp.ui.screens.ProfileScreen
import kotlin.math.roundToInt


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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedScreen.title, color = Color.White)
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
                        }
                    }
                },
                bottomBar = {
                    NavigationBar {
                        Screen.entries.map { screen ->
                            NavigationBarItem(
                                icon = { Text(screen.icon, style = TextStyle(fontSize = 24.sp)) },
                                label = { Text(screen.title) },
                                selected = selectedScreen == screen,
                                onClick = {
                                    selectedScreen = screen
                                }
                            )
                        }
                    }
                },
            )
        }
    }
}