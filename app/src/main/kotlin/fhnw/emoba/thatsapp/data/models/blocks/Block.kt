package fhnw.emoba.thatsapp.data.models.blocks

import fhnw.emoba.thatsapp.data.JSONSerializable
import fhnw.emoba.thatsapp.data.models.MessageType
import org.json.JSONObject

abstract class Block(val type: MessageType) : JSONSerializable {
    constructor(jsonObject: JSONObject) : this(MessageType.valueOf(jsonObject.getString("type")))

    abstract override fun asJSONString(): String
}