package com.home.door

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.home.door.data.DoorEntity
import com.home.door.databinding.ActivityMainBinding
import com.home.door.util.DoorPrefs
import com.home.door.util.DoorValidator
import com.home.door.util.Graph
import com.home.door.widget.UnlockWidget
import com.home.door.widget.updateAppWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val viewModel by viewModels<DoorViewModel>()

        val doorAdapter = DoorAdapter(
            onDelete = viewModel::delete,
            onAddShortcut = { pinWidget(this, it)},
            onUnlock = viewModel::unlockDoor
        )
        binding.recycler.adapter = doorAdapter
        binding.recycler.layoutManager = GridLayoutManager( this, 2)
        doorAdapter.submitList(viewModel.doors)
        lifecycleScope.launch {
            viewModel.uiEvent.collect {
                if (it == UiEvent.REFRESH) {
                    doorAdapter.submitList(viewModel.doors)
                    Log.d("TAG", "onCreate: refresh list")
                }
            }
        }

        binding.fab.setOnClickListener { _ ->
            showNewDoorDialog(viewModel::insert)
        }
    }

    private fun showNewDoorDialog(onAddNew: (DoorEntity) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_door, null)
        val nameEditText: TextInputEditText = dialogView.findViewById(R.id.name_edit)
        val ipEditText: TextInputEditText = dialogView.findViewById(R.id.ip_edit)
        val userEditText: TextInputEditText = dialogView.findViewById(R.id.username_edit)
        val passwordEditText: TextInputEditText = dialogView.findViewById(R.id.password_edit)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(getString(R.string.dialog_title))
            .setPositiveButton(getString(R.string.positive_btn)) { _, _ ->

            }
            .setNegativeButton(getString(R.string.negative_btn), null)

        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                if (checkFields(nameEditText, ipEditText, userEditText, passwordEditText)) {
                    val door = DoorEntity(
                        name = nameEditText.text.toString().trim(),
                        ip = ipEditText.text.toString().trim(),
                        user = userEditText.text.toString().trim(),
                        password = passwordEditText.text.toString().trim(),
                    )
                    onAddNew(door)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun checkFields(
        nameEditText: TextInputEditText,
        ipEditText: TextInputEditText,
        userEditText: TextInputEditText,
        passwordEditText: TextInputEditText
    ): Boolean {
        // validate name
        var validated = true
        try { DoorValidator.validateName(nameEditText.text.toString(), this) }
        catch (e: Exception) {
            nameEditText.error = e.message;validated = false }

        // validate ip
        try { DoorValidator.validateIp(ipEditText.text.toString(), this) }
        catch (e: Exception) {
            ipEditText.error = e.message;validated = false }

        // validate username
        try { DoorValidator.validateUser(userEditText.text.toString(), this) }
        catch (e: Exception) {
            userEditText.error = e.message;validated = false }

        // validate password
        try { DoorValidator.validatePassword(passwordEditText.text.toString(), this) }
        catch (e: Exception) {
            passwordEditText.error = e.message;validated = false }

        return validated
    }

    private fun pinWidget(context: Context, door: DoorEntity) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val provider = ComponentName(context, UnlockWidget::class.java)
        val widgetsCount = appWidgetManager.getAppWidgetIds(provider).size

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appWidgetManager.isRequestPinAppWidgetSupported) {

                val result = appWidgetManager.requestPinAppWidget(provider, null, null)
                if (result) {
                    Toast.makeText(
                        context,
                        getString(R.string.pin_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                    lifecycleScope.launch(Dispatchers.IO) {
                        var currWidgets = appWidgetManager.getAppWidgetIds(provider)
                        while (currWidgets.size <= widgetsCount){
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
        }
    }

}