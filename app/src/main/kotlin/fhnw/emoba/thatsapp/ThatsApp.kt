package fhnw.emoba.thatsapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fhnw.emoba.EmobaApp
import fhnw.emoba.thatsapp.data.Chat
import fhnw.emoba.thatsapp.data.connectors.CameraAppConnector
import fhnw.emoba.thatsapp.data.models.User
import fhnw.emoba.thatsapp.data.services.GPSConnector
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.AppUI
import fhnw.emoba.thatsapp.ui.PhotoBootUI
import java.util.UUID


object ThatsApp : EmobaApp {
    private lateinit var model: ThatsAppModel

    override fun initialize(activity: ComponentActivity) {
        model = ThatsAppModel(GPSConnector(activity), CameraAppConnector(activity), activity)
        model.trackCurrentLocation()
//        model.selectChatView(Chat(User(UUID.randomUUID(), "User", "Greeting", "bob.png")))

        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 1)

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), 1)
        }
    }

    @Composable
    override fun CreateUI() {
        AppUI(model)
    }

}

