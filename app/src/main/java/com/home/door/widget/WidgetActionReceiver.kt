package com.home.door.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.home.door.util.DoorOpener
import com.home.door.util.toDoor
import com.home.door.util.toList

class WidgetActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val door = intent.extras?.getStringArray("door")?.toList()?.toDoor() ?: return
        if (intent.action?.startsWith("ClickAction") == true) {
            // call your function here
            DoorOpener.unlockDoor(door.toList().toDoor())
        }
    }

}