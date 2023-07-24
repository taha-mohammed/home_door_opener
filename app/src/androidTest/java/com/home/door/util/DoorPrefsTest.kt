package com.home.door.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.home.door.data.DoorEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
@SmallTest
class DoorPrefsTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val widgetId = Random.nextInt()

    @Test
    fun saveDoorPref() {
        val door = DoorEntity(0, "door", "192.168.1.1", "taha", "taha")
        DoorPrefs.saveDoorPref(
            context,
            widgetId,
            door
        )

        val result = DoorPrefs.loadDoorPref(context, widgetId)
        assertThat(result).isEqualTo(door)
    }

    @Test
    fun deleteDoorPref() {
        val door = DoorEntity(0, "door", "192.168.1.1", "taha", "taha")
        DoorPrefs.saveDoorPref(
            context,
            widgetId,
            door
        )

        DoorPrefs.deleteDoorPref(context, widgetId)

        val result = DoorPrefs.loadDoorPref(context, widgetId)
        assertThat(result).isNotEqualTo(door)
    }
}