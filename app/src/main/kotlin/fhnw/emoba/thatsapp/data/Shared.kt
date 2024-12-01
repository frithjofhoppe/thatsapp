package fhnw.emoba.thatsapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

fun content(url: String, headers: HashMap<String, String>?): String = content(streamFrom(url, headers))

fun content(fileName: String, context: Context): String = content(context.assets.open(fileName))

fun content(inputStream: InputStream): String {
    val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
    val jsonString = reader.readText()
    reader.close()

    return jsonString
}

fun JSONArrayFrom(fileName: String, context: Context): JSONArray =
    JSONArray(content(fileName, context))

fun <T> dataListFrom(fileName: String, context: Context, transform: (JSONObject) -> T): List<T> =
    JSONArray(content(fileName, context)).map(transform)

fun bitmap(url: String) = bitmap(streamFrom(url, null))

fun bitmap(inputStream: InputStream): Bitmap {
    val bitmapAsBytes = inputStream.readBytes()
    inputStream.close()

    return BitmapFactory.decodeByteArray(bitmapAsBytes, 0, bitmapAsBytes.size)
}

fun <T> JSONArray.map(transform: (JSONObject) -> T): List<T> {
    val list = mutableListOf<T>()
    for (i in 0 until length()) {
        list.add(transform(getJSONObject(i)))
    }
    return list
}

fun streamFrom(url: String, headers: HashMap<String, String>?): InputStream {
    val conn = URL(url).openConnection() as HttpsURLConnection

    if (headers != null) {
        for ((key, value) in headers) {
            conn.setRequestProperty(key, value)
        }
    }
    conn.connect()


    return conn.inputStream
}