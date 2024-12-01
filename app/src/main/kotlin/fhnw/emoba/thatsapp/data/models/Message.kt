package fhnw.emoba.thatsapp.data.models

import fhnw.emoba.thatsapp.data.JSONSerializable
import fhnw.emoba.thatsapp.data.models.blocks.Block
import fhnw.emoba.thatsapp.data.models.blocks.TextBlock
import fhnw.emoba.thatsapp.data.map
import fhnw.emoba.thatsapp.data.models.blocks.ImageBlock
import fhnw.emoba.thatsapp.data.models.blocks.LocationBlock
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.UUID

class Message(
    val id: UUID,
    val sender: UUID,
    val timestamp: LocalDateTime,
    val blocks: ArrayList<Block>
) : JSONSerializable {
    constructor(
        jsonObject: JSONObject
    ) : this(
        UUID.fromString(jsonObject.getString("id")),
        UUID.fromString(jsonObject.getString("sender")),
        LocalDateTime.parse(jsonObject.getString("timestamp")),
        ArrayList()
    ) {
        jsonObject.getJSONArray("blocks").map { createBlock(it) }.filterNotNull()
            .forEach { blocks.add(it) }
    }

    constructor(
        sender: UUID,
        blocks: ArrayList<Block>
    ) : this(UUID.randomUUID(), sender, LocalDateTime.now(), blocks) {
    }


    private fun createBlock(jsonObject: JSONObject): Block? {
        val type = jsonObject.getString("type")
        return when (MessageType.valueOf(type)) {
            MessageType.TEXT -> TextBlock(jsonObject)
            MessageType.LOCATION -> LocationBlock(jsonObject)
            MessageType.IMAGE -> ImageBlock(jsonObject)
            else -> null
        }
    }

    override fun asJSONString(): String {
        return """
            {
                "id": "$id",
                "sender": "$sender",
                "timestamp": "$timestamp",
                "blocks": [${blocks.joinToString(",") { (it as JSONSerializable).asJSONString() }}]
            }
        """.trimIndent()
    }
}

