package com.home.door.widget

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.home.door.R
import com.home.door.data.room.DoorEntity
import com.home.door.util.Constants
import com.home.door.util.DoorOpener
import com.home.door.util.toDoor
import com.home.door.util.toList
import kotlinx.coroutines.launch

class WidgetActionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.action?.startsWith(Constants.WIDGET_CLICK_ACTION) == true) {
            val door = intent.extras?.getStringArray(Constants.EXTRA_DOOR)?.toList()?.toDoor()
            if (door == null) {
                finish()
                return
            }
            val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_dialog_title))
                .setPositiveButton(getString(R.string.unlock_btn)) { _, _ ->
                    lifecycleScope.launch {
                        openDoor(door)
                    }
                }.setNegativeButton(getString(R.string.negative_btn)) { _, _ ->
                    finish()
                }
                .setOnCancelListener {
                    finish()
                }
                .create()

            dialog.show()

        }
    }

    private suspend fun openDoor(door: DoorEntity) {
        DoorOpener.unlockDoor(door.toList().toDoor())
            .onSuccess {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.door_opened),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .onFailure {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}