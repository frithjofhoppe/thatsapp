package fhnw.emoba.thatsapp.data.connectors

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.auth.Mqtt5SimpleAuth
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import fhnw.emoba.thatsapp.data.JSONSerializable
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.text.Charsets.UTF_8

class MqttConnector(
    val qos: MqttQos = MqttQos.AT_LEAST_ONCE
) {
    private val username = "FrithjofHoppe"
    private val password = "cKuDq4*tGg%I45MO"
    private val mqttBroker = "1b590c628fd24b42a4f43f472e1bb7c9.s1.eu.hivemq.cloud"
    private val topic = "fhnw/emoba/dieincompetenten"

    private val client = MqttClient.builder()
        .useMqttVersion5()
        .serverHost(mqttBroker)
        .identifier(UUID.randomUUID().toString())
        .sslWithDefaultConfig()
        .simpleAuth()
        .username(username)
        .password(UTF_8.encode(password))
        .applySimpleAuth()
        .serverPort(8883)
        .buildAsync()

    fun connect(onResolve: () -> Unit = {}, onReject: () -> Unit = {}) {
        client.connectWith()
            .cleanStart(true)
            .keepAlive(30)
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.d("MqttConnector", "Error: ${throwable.message}")
                    onReject()
                } else {
                    onResolve()
                }
            }
    }

    fun subscribe(
        subTopic: String,
        onReceiveNotification: (JSONObject) -> Unit = {},
    ) {
        client.subscribeWith()
            .topicFilter("$topic/$subTopic")
            .qos(qos)
            .noLocal(true)
            .callback { onReceiveNotification(JSONObject(it.payloadAsString())) }
            .send()
    }

    fun publish(
        subTopic: String,
        notification: JSONSerializable,
        onPublished: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        client.publishWith()
            .topic("$topic/$subTopic")
            .payload(notification.asJSONString().asPayload())
            .qos(qos)
            .retain(false)
            .messageExpiryInterval(120)
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.d("MqttConnector", "Error: ${throwable.message}")
                    onError()
                } else {
                    onPublished()
                }
            }
    }

    fun disconnect() {
        client.disconnectWith()
            .sessionExpiryInterval(0)
            .send()
    }
}

private fun String.asPayload(): ByteArray = toByteArray(StandardCharsets.UTF_8)
private fun Mqtt5Publish.payloadAsString(): String = String(payloadAsBytes, StandardCharsets.UTF_8)
