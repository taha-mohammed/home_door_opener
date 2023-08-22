package com.home.door.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.home.door.R
import com.home.door.data.room.DoorEntity
import com.home.door.util.Graph
import com.home.door.util.toWidget
import kotlinx.coroutines.launch

class WidgetConfActivity : AppCompatActivity() {

    private lateinit var doors: List<DoorEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_conf)

        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        val recycler = findViewById<RecyclerView>(R.id.conf_recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        val onClick:(door: DoorEntity) -> Unit = {
            lifecycleScope.launch {
                Graph.widgetRepo.addWidget(it.toWidget(widgetId))
                updateAppWidget(
                    this@WidgetConfActivity,
                    AppWidgetManager.getInstance(this@WidgetConfActivity),
                    widgetId
                )

                val resultIntent = Intent()
                resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        doors = emptyList()
        lifecycleScope.launch {
            Graph.doorRepo.getDoors().collect{
                doors = it
                recycler.adapter = ConfAdapter(doors, onClick)
            }
        }
    }
}

class ConfAdapter(
    private val items: List<DoorEntity>,
    val onClick: (door: DoorEntity)-> Unit
) : RecyclerView.Adapter<ConfAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view)
        val layout: LinearLayout = view.findViewById(R.id.item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conf_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position].name
        holder.layout.setOnClickListener {
            onClick(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}