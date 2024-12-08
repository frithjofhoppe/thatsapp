package fhnw.emoba.thatsapp.data.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.thatsapp.data.connectors.MqttConnector
import fhnw.emoba.thatsapp.data.models.User
import java.util.UUID

class UserService(
    private val mqttConnector: MqttConnector
) {
    fun connectWithUser(
        currentUser: User,
        onResolve: (userServiceConnection: UserServiceConnection) -> Unit = {},
        onReject: () -> Unit = {}
    ) {
        mqttConnector.connect(
            onResolve = {
                onResolve(UserServiceConnection(currentUser, mqttConnector))
            }, onReject = onReject
        )
    }
}

class UserServiceConnection(val user: User, private val mqttConnector: MqttConnector) {
    private val topic = "users"
    private val userAddedSubscriptions = mutableStateListOf<(User) -> Unit>()

    fun onUserAdded(onUserAdded: (User) -> Unit) {
        userAddedSubscriptions += onUserAdded
    }

    private fun notifyUserAdded(user: User) {
        userAddedSubscriptions.forEach { it(user) }
    }

    fun subscribe() {
        mqttConnector.subscribe("$topic/#",
        ) { m ->
            run {
                val greetedUser = User(m)
                if (greetedUser.id == user.id) {
                    return@run
                }

                greet(greetedUser.id)
                notifyUserAdded(greetedUser)
            }
        }

        mqttConnector.subscribe(
            "$topic/${user.id}",
        ) { m ->
            run {
                notifyUserAdded(User(m))
            }
        }

        announce()
    }

    private fun announce(
        onPublished: () -> Unit = {}, onError: () -> Unit = {}
    ) {
        mqttConnector.publish("$topic/${user.id}", user, onPublished, onError)
    }

    private fun greet(
        targetUserId: UUID, onPublished: () -> Unit = {}, onError: () -> Unit = {}
    ) {
        mqttConnector.publish("$topic/${targetUserId}", user, onPublished, onError)
    }


    fun disconnect(): UserServiceConnection {
        mqttConnector.disconnect()
        return this;
    }
}