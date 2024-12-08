package fhnw.emoba.thatsapp.model

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.R
import fhnw.emoba.thatsapp.data.Chat
import fhnw.emoba.thatsapp.data.ChatStore
import fhnw.emoba.thatsapp.data.connectors.CameraAppConnector
import fhnw.emoba.thatsapp.data.connectors.MqttConnector
import fhnw.emoba.thatsapp.data.connectors.downloadBitmapFromFileIO
import fhnw.emoba.thatsapp.data.connectors.uploadBitmapToFileIO
import fhnw.emoba.thatsapp.data.models.GeoPosition
import fhnw.emoba.thatsapp.data.models.Message
import fhnw.emoba.thatsapp.data.models.User
import fhnw.emoba.thatsapp.data.models.blocks.ImageBlock
import fhnw.emoba.thatsapp.data.models.blocks.TextBlock
import fhnw.emoba.thatsapp.data.services.GPSConnector
import fhnw.emoba.thatsapp.data.services.MessageService
import fhnw.emoba.thatsapp.data.services.UserService
import fhnw.emoba.thatsapp.data.services.UserServiceConnection
import io.reactivex.internal.operators.single.SingleDoOnSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class ThatsAppModel(
    val gpsConnector: GPSConnector,
    val cameraAppConnector: CameraAppConnector,
    val componentActivity: ComponentActivity
) {
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
    var txtMessage by mutableStateOf("")
    var currentLocation by mutableStateOf(GeoPosition(0.0, 0.0, 0.0))
    var isLocationPermissonGranted by mutableStateOf(false)
    var currentPhoto by mutableStateOf<Bitmap?>(null)
    var isPhotoTaken by mutableStateOf(false)
    private val backgroundJob = SupervisorJob()
    private val modelScope = CoroutineScope(backgroundJob + Dispatchers.IO)
    var downloadedImages by mutableStateOf(emptyMap<String, Pair<Boolean, Bitmap?>>())

    fun uploadChatImage(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        modelScope.launch {
            uploadBitmapToFileIO(
                bitmap,
                onSuccess = onSuccess,
                onError = { error, mesg ->
                    println("Error occured while uploading photo")
                }
            )
        }
    }

    fun loadChatImage(url: String) {
        println("Start loading image $url")
        if(downloadedImages.containsKey(url)){
            println("IMG Image already loading $url")
            return
        } else {
            downloadedImages.plus(Pair(url, Pair(true,null)))
            modelScope.launch {
                println("IMG Star loading image $url")
                downloadBitmapFromFileIO(
                    url = url,
                    onSuccess = { image ->
                        println("IMG Image loaded!! $url")
                        downloadedImages = downloadedImages.plus(Pair(url, Pair(false, image)))
                    },
                    onError = { e ->
                        println("IMG Failed to download image")
                    }
                )
            }
        }
    }

    fun trackCurrentLocation() {
        gpsConnector.getLocation(
            onNewLocation = {
                currentLocation = it
                isLocationPermissonGranted = true
            },
            onFailure = {
                println("GPS: Failed to get location")
                isLocationPermissonGranted = false
            },
            onPermissionDenied = {
                println("GPS: Permission denied")
                isLocationPermissonGranted = false
            }
        )
    }

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
        selectedScreen = Screen.CHATS
    }

    var photo by mutableStateOf<Bitmap?>(null)

    var notificationMessage by mutableStateOf("")

    fun takePhoto() {
        cameraAppConnector.getBitmap(onSuccess = {
            println("Photo taken")
            photo = it
            txtMessage = ""
        },
            onCanceled = {
                isPhotoTaken = false
                txtMessage = ""
            })
    }
}