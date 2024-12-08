package fhnw.emoba.thatsapp.data

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.thatsapp.data.connectors.MqttConnector
import fhnw.emoba.thatsapp.data.models.Message
import fhnw.emoba.thatsapp.data.models.MessageType
import fhnw.emoba.thatsapp.data.models.User
import fhnw.emoba.thatsapp.data.models.blocks.TextBlock
import fhnw.emoba.thatsapp.data.services.MessageService
import fhnw.emoba.thatsapp.data.services.UserService
import java.time.LocalDateTime
import java.util.UUID

class ChatStore(mqttConnector: MqttConnector, context: Context) {
    private val userService = UserService(mqttConnector)
    private val messageService = MessageService(mqttConnector)
    private val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val mediaPlayer: MediaPlayer = MediaPlayer.create(context, notificationUri)
    var chats by mutableStateOf(listOf<Chat>())
    var currentUser by mutableStateOf<User?>(null)


    fun login(user: User, onLoggedIn: () -> Unit) {
        currentUser = user
        userService.connectWithUser(user, {
            it.onUserAdded { user ->
                if (chats.any { it.user.id == user.id }) {
                    return@onUserAdded
                }
                Log.d("ThatsAppModel", "User added: ${user.name} - ${user.id}")
                chats += Chat(user)
            }
            it.subscribe()
            Log.d("ThatsAppModel", "init")
            monitorMessages()
            onLoggedIn()
        })
    }

    fun monitorMessages() {
        messageService.subscribe(
            currentUser!!.id,
            onReceiveMessage = {
                getChatByUserId(it.sender)?.addMessage(it)
                playNotification()
            },
        )
    }

    fun sendMessage(targetUser: User, message: Message) {
        messageService.publish(targetUser.id, message, onPublished = {
            val chat = getChatByUserId(targetUser.id)
            chat?.addMessage(message)
        }, onError = {
            Log.d("ChatStore", "Error while sending message")
        })
    }

    fun getChatByUserId(userId: UUID): Chat? {
        return chats.find { it.user.id == userId }
    }

    private fun playNotification() {
        mediaPlayer.start()
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}


class Chat(val user: User) {
    val messages = mutableStateListOf<Message>();
    var mostRecentMessage by mutableStateOf<String>("")
        private set;
    var timeStampRecentMessage by mutableStateOf(LocalDateTime.now())
    var unreadMessages by mutableIntStateOf(0)
        private set;

    fun addMessage(message: Message) {
        messages.add(message)
        var textMessage = message.blocks.find { it.type == MessageType.TEXT }
        if (textMessage == null) {
            return
        }
        textMessage = textMessage as TextBlock
        mostRecentMessage = textMessage.text
        timeStampRecentMessage = message.timestamp
        unreadMessages++
    }

    fun markAsRead() {
        unreadMessages = 0
    }
}