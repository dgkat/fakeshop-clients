package org.example.fakeshop_clients.features.bdui

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

val BduiJson: Json = Json {
    classDiscriminator = "type"
    ignoreUnknownKeys = true
    isLenient = false
    encodeDefaults = true
}
