package fhnw.emoba.thatsapp.data.models

enum class MessageType(
    val type: String
) {
    TEXT("TEXT"),
    LOCATION("LOCATION"),
    IMAGE("IMAGE")
}