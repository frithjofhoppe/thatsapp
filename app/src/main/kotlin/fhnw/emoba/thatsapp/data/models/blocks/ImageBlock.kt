package fhnw.emoba.thatsapp.data.models.blocks

import fhnw.emoba.thatsapp.data.models.MessageType
import org.json.JSONObject

class ImageBlock(val url: String): Block(MessageType.IMAGE) {
    constructor(jsonObject: JSONObject) : this(jsonObject.getString("url"))

    override fun asJSONString(): String {
        return """
            {
                "type": "${super.type.name}",
                "url": "$url"
            }
        """.trimIndent()
    }
}