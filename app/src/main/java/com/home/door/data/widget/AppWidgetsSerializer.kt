package com.home.door.data.widget

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


object AppWidgetsSerializer: Serializer<AppWidgets> {

    override val defaultValue: AppWidgets
        get() = AppWidgets()

    override suspend fun readFrom(input: InputStream): AppWidgets {
        return try {
            Json.decodeFromString(
                AppWidgets.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppWidgets, output: OutputStream) {
        Json.encodeToString(
            AppWidgets.serializer(),
            t
        ).let {
            output.write(it.encodeToByteArray())
        }
    }

}