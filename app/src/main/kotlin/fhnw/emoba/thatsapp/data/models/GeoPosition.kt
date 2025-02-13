package fhnw.emoba.thatsapp.data.models

import fhnw.emoba.thatsapp.data.JSONSerializable
import org.json.JSONObject
import java.util.Locale;

class GeoPosition(
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val altitude: Double = 0.0
) : JSONSerializable {

    constructor(jsonObject: JSONObject) : this(
        jsonObject.getDouble("longitude"),
        jsonObject.getDouble("latitude"),
        jsonObject.getDouble("altitude")
    )

    fun dms(): String {
        return format(latitude, longitude)
    }

    fun asGoogleMapsURL() =
        "https://www.google.com/maps/place/${dms()}/@${latitude},${longitude},17z"

    fun asOpenStreetMapsURL(zoom: Int = 17) =
        "https://www.openstreetmap.org/?mlat=${latitude}&mlon=${longitude}#map=${zoom}/${latitude}/${longitude}"

    private fun format(
        latitude: Double,
        longitude: Double
    ): String {
        val latCompassDirection = if (latitude > 0.0) "N" else "S"
        val lonCompassDirection = if (longitude > 0.0) "E" else "W"

        return "${getDMS(latitude)} $latCompassDirection, ${getDMS(longitude)} $lonCompassDirection"
    }

    private fun getDMS(value: Double): String {
        val absValue = Math.abs(value)
        val degree = absValue.toInt()
        val minutes = ((absValue - degree) * 60.0).toInt()
        val seconds =
            (absValue - degree - minutes / 60.0) * 3600.0
        return "${degree}° ${minutes}′ ${
            String.format(
                Locale.ENGLISH,
                "%.4f",
                seconds
            )
        }″";
    }

    override fun asJSONString(): String {
        return """
            {
                "latitude": $latitude,
                "longitude": $longitude,
                "altitude": $altitude
            }
        """.trimIndent()
    }
}