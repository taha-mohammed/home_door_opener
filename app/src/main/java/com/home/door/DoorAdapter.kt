package com.home.door

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.door.data.DoorEntity

class DoorAdapter(
    private val onDelete: (door: DoorEntity) -> Unit,
    private val onUnlock: (door: DoorEntity) -> Unit,
    private val onAddShortcut: (door: DoorEntity) -> Unit) :
    ListAdapter<DoorEntity, DoorAdapter.ViewHolder>(DoorDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val door = getItem(position)
        holder.doorName.text = door.name
        holder.itemMenu.setOnClickListener {
            showMoreMenu(holder.itemMenu, door)
        }

        holder.unlockBtn.setOnClickListener {
            onUnlock(door)
        }
    }

    private fun showMoreMenu(view: View, door: DoorEntity) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_list_item, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    onDelete(door)
                    true
                }
                R.id.action_shortcut -> {
                    onAddShortcut(door)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doorName: TextView = itemView.findViewById(R.id.door_name)
        val itemMenu: ImageView = itemView.findViewById(R.id.item_menu)
        val unlockBtn: Button = itemView.findViewById(R.id.unlock_btn)
    }

    class DoorDiffUtil : DiffUtil.ItemCallback<DoorEntity>(){
        override fun areItemsTheSame(oldItem: DoorEntity, newItem: DoorEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DoorEntity, newItem: DoorEntity): Boolean {
            return oldItem.id == newItem.id && oldItem.ip == newItem.ip && oldItem.name == newItem.name && oldItem.user == newItem.user && oldItem.password == newItem.password
        }

    }
}