package fhnw.emoba.thatsapp.data.services

import android.util.Log
import fhnw.emoba.thatsapp.data.connectors.MqttConnector
import fhnw.emoba.thatsapp.data.models.Message
import java.util.UUID

class MessageService(
    private val mqttConnector: MqttConnector
) {
    private val topic = "messages"

    fun connect(
        onResolve: () -> Unit = {},
        onReject: () -> Unit = {}
    ) {
        mqttConnector.connect(onResolve, onReject)
    }

    fun subscribe(
        userIdForSubscription: UUID,
        onReceiveMessage: (Message) -> Unit,
    ) {
        mqttConnector.subscribe("$topic/$userIdForSubscription",
            { message ->
                onReceiveMessage(Message(message))
            }
        )
    }

    fun publish(
        userIdForRecipient: UUID,
        message: Message,
        onPublished: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        mqttConnector.publish(
            "$topic/$userIdForRecipient",
            message,
            onPublished,
            onError
        )
    }

    fun disconnect() {
        mqttConnector.disconnect()
    }
}