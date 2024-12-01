package fhnw.emoba.thatsapp.data.models.blocks

import fhnw.emoba.thatsapp.data.models.MessageType
import org.json.JSONObject

class TextBlock(val text: String) : Block(MessageType.TEXT) {
    constructor(jsonObject: JSONObject) : this(jsonObject.getString("text"))

    override fun asJSONString(): String {
        return """
            {
                "type": "${super.type.name}",
                "text": "$text"
            }
        """.trimIndent()
    }
}