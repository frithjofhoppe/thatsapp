package fhnw.emoba.thatsapp.data.models

import fhnw.emoba.thatsapp.data.JSONSerializable
import org.json.JSONObject
import java.util.UUID

class User(
    val id: UUID,
    val greeting: String,
    val avatar: String,
    val name: String
) : JSONSerializable {
    constructor(
        jsonObject: JSONObject
    ) : this(
        UUID.fromString(jsonObject.getString("id")),
        jsonObject.getString("greeting"),
        jsonObject.optString("avatar"),
        jsonObject.optString("name")
    )



    constructor(
        greeting: String,
        avatar: String,
        name: String,
    ) : this(UUID.randomUUID(), greeting, avatar, name)


    override fun asJSONString(): String {
        return """
            {
                "id": "$id",
                "greeting": "$greeting",
                "avatar": "$avatar",
                "name": "$name"
            }
        """.trimIndent()
    }

}