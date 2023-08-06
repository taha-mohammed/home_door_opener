package com.home.door.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.home.door.R
import com.home.door.util.DoorOpener
import com.home.door.util.toDoor
import com.home.door.util.toList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WidgetActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val door = intent.extras?.getStringArray("door")?.toList()?.toDoor() ?: return
        if (intent.action?.startsWith("ClickAction") == true) {
            val pendingResult = goAsync()
            CoroutineScope(SupervisorJob()).launch {
                DoorOpener.unlockDoor(door.toList().toDoor())
                    .onSuccess {
                        Toast.makeText(
                            context,
                            context.getString(R.string.door_opened),
                            Toast.LENGTH_SHORT
                        ).show()
                        pendingResult.finish()
                    }
                    .onFailure {
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        pendingResult.finish()
                    }
            }
        }
    }

}