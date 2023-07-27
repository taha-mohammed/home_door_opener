package com.home.door

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.home.door.data.DoorEntity
import com.home.door.databinding.ActivityMainBinding
import com.home.door.util.Graph
import com.home.door.util.MainEvent
import com.home.door.widget.UnlockWidget
import com.home.door.widget.updateAppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var dialog: AlertDialog
    private lateinit var nameEditText: TextInputEditText
    private lateinit var ipEditText: TextInputEditText
    private lateinit var userEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val viewModel by viewModels<DoorViewModel>()

        val doorAdapter = DoorAdapter(
            onDelete = { viewModel.onEvent(MainEvent.DeleteDoor(it)) },
            onAddShortcut = { pinWidget(this, it) },
            onUnlock = { viewModel.onEvent(MainEvent.UnlockDoor(it)) }
        )
        binding.recycler.adapter = doorAdapter
        binding.recycler.layoutManager = GridLayoutManager(this, 2)
        doorAdapter.submitList(viewModel.doors)

        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    UiEvent.AddSuccess -> {
                        dialog.dismiss()
                    }
                    UiEvent.Refresh -> {
                        doorAdapter.submitList(viewModel.doors)
                        Log.d("TAG", "onCreate: refresh list")
                    }
                    is UiEvent.OpenResult -> {
                        event.result
                            .onSuccess {
                                Toast.makeText(
                                    this@MainActivity,
                                    getString(R.string.door_opened),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .onFailure {
                                Toast.makeText(
                                    this@MainActivity,
                                    it.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    is UiEvent.ValidateFields -> {
                        with(event.result) {
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

        binding.fab.setOnClickListener { _ ->
            showNewDoorDialog { viewModel.onEvent(MainEvent.AddDoor(it)) }
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
            .setNegativeButton(getString(R.string.negative_btn), null)

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

    private fun pinWidget(context: Context, door: DoorEntity) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val provider = ComponentName(context, UnlockWidget::class.java)
        val widgetsCount = appWidgetManager.getAppWidgetIds(provider).size

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O
            || !appWidgetManager.isRequestPinAppWidgetSupported
        ) {
            Toast.makeText(context, getString(R.string.feature_not_supported), Toast.LENGTH_SHORT)
                .show()
            return
        }
        val result = appWidgetManager.requestPinAppWidget(provider, null, null)
        if (!result) {
            Toast.makeText(context, getString(R.string.shortcut_failed), Toast.LENGTH_SHORT)
                .show()
        }
        Toast.makeText(
            context,
            getString(R.string.pin_toast),
            Toast.LENGTH_SHORT
        ).show()
        lifecycleScope.launch(Dispatchers.IO) {
            var currWidgets = appWidgetManager.getAppWidgetIds(provider)
            while (currWidgets.size <= widgetsCount) {
                currWidgets = appWidgetManager.getAppWidgetIds(provider)
            }
            currWidgets.last().let {
                Graph.doorPrefs.saveDoorPref(it, door)
                Log.d("TAG", "pinWidget: door data is saved")
                updateAppWidget(context, appWidgetManager, it)
                Log.d("TAG", "pinWidget: widget is updated")
            }
        }
    }

}