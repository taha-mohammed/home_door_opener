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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
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

    @Before
    fun setup() {
        testScope.launch {
            context.dataStore.updateData {
                AppWidgets()
            }
        }
        testScope.advanceUntilIdle()
    }

    @Test
    fun addWidget() = testScope.runTest {
        widgetRepo.addWidget(sampleWidget)

        val result = widgetRepo.getWidget(0)
        assertThat(result).isEqualTo(sampleWidget)
    }

    @Test
    fun deleteWidget() = testScope.runTest {
        widgetRepo.addWidget(sampleWidget)

        widgetRepo.deleteWidget(0)

        val result = widgetRepo.getWidget(0)
        assertThat(result).isNull()
    }

    @Test
    fun deleteWidgetsByDoorId() = testScope.runTest {
        val sampleWidget2 = sampleWidget.copy(widgetId = 2, doorId = 3, doorName = "Test Door 2")

        widgetRepo.addWidget(sampleWidget)
        widgetRepo.addWidget(sampleWidget2)
        widgetRepo.addWidget(sampleWidget.copy(widgetId = 5))

        widgetRepo.deleteWidgetsByDoorId(0)

        val result = widgetRepo.getWidget(0)
        assertThat(result).isNull()

        val result2 = widgetRepo.getWidget(2)
        assertThat(result2).isEqualTo(sampleWidget2)
    }

    @Test
    fun addDuplicatedWidget_updated() = testScope.runTest {
        widgetRepo.addWidget(sampleWidget)
        widgetRepo.addWidget(sampleWidget.copy(doorName = "Test Door 2"))

        val result = widgetRepo.getWidget(0)
        assertThat(result).isEqualTo(sampleWidget.copy(doorName = "Test Door 2"))
    }

    @Test
    fun deleteNotExistedWidget_nothing_happen() = testScope.runTest {
        widgetRepo.deleteWidget(2)

        val result = widgetRepo.getWidget(0)
        assertThat(result).isNull()
    }

}