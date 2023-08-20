package com.home.door.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.home.door.R
import com.home.door.util.Constants

class WidgetActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action?.startsWith(Constants.OLD_WIDGET_CLICK_ACTION) == true) {
            Toast.makeText(
                context.applicationContext,
                context.getString(R.string.invalid_widget_text)
                        + context.getString(R.string.add_widget_again),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}