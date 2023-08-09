package com.home.door.main

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.home.door.R
import com.home.door.data.room.DoorEntity
import com.home.door.databinding.ActivityMainBinding
import com.home.door.util.Graph
import com.home.door.widget.UnlockWidget
import com.home.door.widget.updateAppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var dialog: AlertDialog
    private lateinit var nameEditText: TextInputEditText
    private lateinit var ipEditText: TextInputEditText
    private lateinit var userEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    private var dialogJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val viewModel by viewModels<DoorViewModel>()

        viewModel.initialize()

        val doorAdapter = DoorAdapter(
            onDelete = { viewModel.onEvent(MainEvent.DeleteDoor(it)) },
            onAddShortcut = {
                pinWidget { id ->
                    if (id == null) {
                        Toast.makeText(
                            this,
                            getString(R.string.shortcut_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@pinWidget
                    }
                    Graph.doorPrefs.saveDoorPref(id, it)
                    Log.d("TAG", "pinWidget: door data is saved")
                    updateAppWidget(this@MainActivity, AppWidgetManager.getInstance(this), id)
                    Log.d("TAG", "pinWidget: widget is updated")
                    Toast.makeText(
                        this,
                        getString(R.string.pin_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onUnlock = { viewModel.onEvent(MainEvent.UnlockDoor(it)) }
        )
        binding.recycler.adapter = doorAdapter
        binding.recycler.layoutManager = GridLayoutManager(this, 2)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.distinctUntilChanged { _, new -> new.openResult == null }
                    .collect { state ->
                        state.openResult
                            ?.onSuccess {
                                Toast.makeText(
                                    this@MainActivity,
                                    getString(R.string.door_opened),
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.onEvent(MainEvent.DoorUnlocked)
                            }
                            ?.onFailure {
                                Toast.makeText(
                                    this@MainActivity,
                                    it.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.onEvent(MainEvent.DoorUnlocked)
                            }
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.distinctUntilChanged { old, new -> old.doors == new.doors }
                    .collect { state ->
                        doorAdapter.submitList(state.doors)
                    }
            }
        }

        binding.fab.setOnClickListener { _ ->
            showNewDoorDialog { viewModel.onEvent(MainEvent.AddDoor(it)) }
            dialogJob = lifecycleScope.launch {
                viewModel.uiState.collect { state ->
                    if (state.isAdded) {
                        dialog.cancel()
                        viewModel.onEvent(MainEvent.DoorAdded)
                        cancel()
                    }
                    with(state.validationState) {
                        nameError?.let {
                            nameEditText.error = getString(it)
                        }
                        ipError?.let {
                            ipEditText.error = getString(it)
                        }
                        userError?.let {
                            userEditText.error = getString(it)
                        }
                        passwordError?.let {
                            passwordEditText.error = getString(it)
                        }
                    }
                }
            }
        }
    }

    private fun showNewDoorDialog(onAddNew: (DoorEntity) -> Unit) {
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.dialog_new_door, null)
        nameEditText = dialogView.findViewById(R.id.name_edit)
        ipEditText = dialogView.findViewById(R.id.ip_edit)
        userEditText = dialogView.findViewById(R.id.username_edit)
        passwordEditText = dialogView.findViewById(R.id.password_edit)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(getString(R.string.dialog_title))
            .setPositiveButton(getString(R.string.positive_btn)) { _, _ ->

            }
            .setNegativeButton(getString(R.string.negative_btn)) { _, _ -> dialogJob?.cancel() }

        dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    val door = DoorEntity(
                        name = nameEditText.text.toString().trim(),
                        ip = ipEditText.text.toString().trim(),
                        user = userEditText.text.toString().trim(),
                        password = passwordEditText.text.toString().trim(),
                    )
                    onAddNew(door)
                }
        }
        dialog.show()
    }

    private fun pinWidget(onComplete: (Int?) -> Unit) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val provider = ComponentName(this, UnlockWidget::class.java)
        val widgetsCount = appWidgetManager.getAppWidgetIds(provider).size

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O
            || !appWidgetManager.isRequestPinAppWidgetSupported
        ) {
            Toast.makeText(this, getString(R.string.feature_not_supported), Toast.LENGTH_SHORT)
                .show()
            return
        }
        appWidgetManager.requestPinAppWidget(provider, null, null)
        lifecycleScope.launch(Dispatchers.Default) {
            withTimeoutOrNull(5000L) {
                var currWidgets: IntArray
                do {
                    currWidgets = appWidgetManager.getAppWidgetIds(provider)
                } while (currWidgets.size <= widgetsCount)

                currWidgets.last()
            }.also {
                withContext(Dispatchers.Main) {
                    onComplete(it)
                }
            }
        }
    }

}