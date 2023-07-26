package com.home.door.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.home.door.data.DoorEntity
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
@SmallTest
class DoorPrefsTest {

    private lateinit var doorPrefs: DoorPrefs
    private val widgetId = Random.nextInt()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        doorPrefs = DoorPrefs(context.getSharedPreferences("TEST-SHARED", 0))
    }

    @After
    fun tearDown() {
        ApplicationProvider.getApplicationContext<Context>().deleteSharedPreferences("TEST-SHARED")
    }

    @Test
    fun saveDoorPref() {
        val door = DoorEntity(0, "door", "192.168.1.1", "taha", "taha")
        doorPrefs.saveDoorPref(
            widgetId,
            door
        )

        val result = doorPrefs.loadDoorPref(widgetId)
        assertThat(result).isEqualTo(door)
    }

    @Test
    fun deleteDoorPref() {
        val door = DoorEntity(0, "door", "192.168.1.1", "taha", "taha")
        doorPrefs.saveDoorPref(
            widgetId,
            door
        )

        doorPrefs.deleteDoorPref(widgetId)

        val result = doorPrefs.loadDoorPref(widgetId)
        assertThat(result).isNotEqualTo(door)
    }
}