package com.home.door

import android.app.Application
import com.home.door.util.Graph

class DoorApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}