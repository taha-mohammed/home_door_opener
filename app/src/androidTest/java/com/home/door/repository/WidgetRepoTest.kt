package com.home.door.repository

import android.content.Context
import androidx.datastore.dataStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.home.door.data.widget.AppWidgets
import com.home.door.data.widget.AppWidgetsSerializer
import com.home.door.data.widget.Widget
import com.home.door.repository.widget.DefaultWidgetRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class WidgetRepoTest {

    private val testScope = TestScope()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val Context.dataStore by dataStore(
        fileName= "test-widgets.pb",
        serializer = AppWidgetsSerializer,
        scope = testScope,
    )
    private val widgetRepo: WidgetRepo = DefaultWidgetRepo(context.dataStore)
    private val sampleWidget = Widget(
        widgetId = 0,
        doorId = 0,
        doorName = "Test Door",
        doorIp = "192.168.3.5",
        doorUser = "test-user",
        doorPassword = "test-password"
    )

    @After
    fun teardown() {
        testScope.launch {
            context.dataStore.updateData {
                AppWidgets()
            }
        }
        testScope.cancel()
    }

    @Test
    fun addWidget() = testScope.runTest {
        widgetRepo.addWidget(sampleWidget)
        this.advanceUntilIdle()

        val result = widgetRepo.getWidget(0)
        assertThat(result).isEqualTo(sampleWidget)
    }

    @Test
    fun deleteWidget() = testScope.runTest {
        widgetRepo.addWidget(sampleWidget)
        this.advanceUntilIdle()

        widgetRepo.deleteWidget(0)
        this.advanceUntilIdle()

        val result = widgetRepo.getWidget(0)
        assertThat(result).isEqualTo(null)
    }

    @Test
    fun deleteWidgetsByDoorId() = testScope.runTest {
        val sampleWidget2 = sampleWidget.copy(widgetId = 2, doorId = 3, doorName = "Test Door 2")

        widgetRepo.addWidget(sampleWidget)
        widgetRepo.addWidget(sampleWidget2)
        widgetRepo.addWidget(sampleWidget.copy(widgetId = 5))
        this.advanceUntilIdle()

        widgetRepo.deleteWidgetsByDoorId(0)
        this.advanceUntilIdle()

        val result = widgetRepo.getWidget(0)
        assertThat(result).isEqualTo(null)

        val result2 = widgetRepo.getWidget(2)
        assertThat(result2).isEqualTo(sampleWidget2)
    }
}