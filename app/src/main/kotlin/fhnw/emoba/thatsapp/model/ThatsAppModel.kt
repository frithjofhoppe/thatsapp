package fhnw.emoba.thatsapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.R
import fhnw.emoba.thatsapp.data.Chat
import fhnw.emoba.thatsapp.data.ChatStore
import fhnw.emoba.thatsapp.data.connectors.MqttConnector
import fhnw.emoba.thatsapp.data.models.User
import fhnw.emoba.thatsapp.data.services.MessageService
import fhnw.emoba.thatsapp.data.services.UserService
import fhnw.emoba.thatsapp.data.services.UserServiceConnection
import java.util.UUID

object ThatsAppModel {
    var title = "Hello ThatsApp"
    var selectedScreen by mutableStateOf(Screen.PROFILE)
    var connector = MqttConnector()
    var messageService = MessageService(connector)
    var userService = UserService(connector)
    var chatStore = ChatStore(connector)

    // Profile
    var userId by mutableStateOf(UUID.randomUUID())
    var txtGreeting by mutableStateOf("It's me, hi, i'm the problem it's me")
    var txtUserName by mutableStateOf(userId.toString())
    var imgAvatar by mutableStateOf("")
    var isSubscribed by mutableStateOf(false)
    var user by mutableStateOf(User(userId, txtUserName, txtGreeting, imgAvatar))
    var avatars = mapOf(
        "bob.png" to R.drawable.bob,
        "agnes.png" to R.drawable.agnes,
        "edith.png" to R.drawable.edith,
        "eduardo.png" to R.drawable.eduardo,
        "gru.png" to R.drawable.gru,
        "kevin.png" to R.drawable.kevin,
        "margo.png" to R.drawable.margo,
        "minion_america.png" to R.drawable.minion_america,
        "stuart.png" to R.drawable.stuart
    )
    var userServiceConnection by mutableStateOf(UserServiceConnection(user, connector))

    // Chat
    var users by mutableStateOf(emptyList<User>());
    var isChatViewDisplay by mutableStateOf(false);
    var selectedChat by mutableStateOf<Chat?>(null);

    fun selectChatView(chat: Chat) {
        selectedChat = chat
        isChatViewDisplay = true
        selectedScreen = Screen.CHAT
    }

    fun updateProfile() {
        user = User(
            id = userId,
            name = txtUserName,
            greeting = txtGreeting,
            avatar = imgAvatar
        )
        chatStore.login(user) {
            println("LOGGED IN")
            isSubscribed = true
        }
//        userService.connectWithUser(
//            currentUser = user,
//            onResolve = {
//                userServiceConnection = it
//                isSubscribed = true
//                userServiceConnection!!.onUserAdded { user ->
//                    println("User added: ${user.name} - ${user.id}")
//                    users += user
//                }
//                userServiceConnection!!.subscribe()
//            }
//        )
    }
}