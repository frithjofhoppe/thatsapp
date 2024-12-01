package fhnw.emoba.thatsapp.data.models.blocks

import fhnw.emoba.thatsapp.data.models.GeoPosition
import fhnw.emoba.thatsapp.data.models.MessageType
import org.json.JSONObject

class LocationBlock(val geoPosition: GeoPosition) : Block(MessageType.LOCATION) {
    constructor(jsonObject: JSONObject) : this(GeoPosition(jsonObject.getJSONObject("geoPosition")))

    override fun asJSONString(): String {
        return """
            {
                "type": "${super.type.name}",
                "geoPosition": ${geoPosition.asJSONString()}
            }
        """.trimIndent()
    }
}