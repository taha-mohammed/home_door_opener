package com.home.door.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.home.door.R
import com.home.door.util.Constants
import com.home.door.util.DoorOpener
import com.home.door.util.toDoor
import com.home.door.util.toList
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class WidgetActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val door = intent.extras?.getStringArray("door")?.toList()?.toDoor() ?: return
        if (intent.action?.startsWith(Constants.WIDGET_CLICK_ACTION) == true) {
            val pendingResult = goAsync()
            runBlocking {
                async { DoorOpener.unlockDoor(door.toList().toDoor()) }.await()
                    .onSuccess {
                        Toast.makeText(
                            context.applicationContext,
                            context.getString(R.string.door_opened),
                            Toast.LENGTH_SHORT
                        ).show()
                        pendingResult.finish()
                    }
                    .onFailure {
                        Toast.makeText(context.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        pendingResult.finish()
                    }

            }
        }
    }

}