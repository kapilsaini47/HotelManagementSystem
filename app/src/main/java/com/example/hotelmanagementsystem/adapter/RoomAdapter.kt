package com.example.hotelmanagementsystem.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.onClickInterface.OnItemClickListener
import com.squareup.picasso.Picasso

class RoomAdapter(
    private val onItemClickListenerInterface: OnItemClickListener
):RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {



    inner class RoomViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        var image = itemView.findViewById<ImageView>(R.id.view_roomImgae)
        var price = itemView.findViewById<TextView>(R.id.tvPrice)
        var category = itemView.findViewById<TextView>(R.id.tvCategory)
        var roomNo = itemView.findViewById<TextView>(R.id.tvRoomNo)
        var status = itemView.findViewById<TextView>(R.id.tvStatus)
        var menuImage = itemView.findViewById<ImageView>(R.id.ivMenu)

        //bind views with data class
        fun bindView(roomMasters: RoomMasters){
            price.text = roomMasters.price.toString()
            category.text = roomMasters.roomCategory
            roomNo.text = roomMasters.roomNo
            status.text = roomMasters.status
            menuImage.setOnClickListener(this)
            Picasso.get().load(roomMasters.roomPicture).error(R.drawable.hotel_room).into(image)
        }

        override fun onClick(v: View?) {
            val popupMenu = PopupMenu(v?.context,v)
            popupMenu.menuInflater.inflate(R.menu.item_view_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
            val position = adapterPosition
            if (menuItem != null) {
                onItemClickListenerInterface.onItemClick( position, menuItem, differ.currentList[position])
            }
            return  false
        }
    }

   // diff util to find and update difference view in recyclerview
    private  val diffUtilCallback = object : DiffUtil.ItemCallback<RoomMasters>(){
       override fun areItemsTheSame(oldItem: RoomMasters, newItem: RoomMasters): Boolean {
           return oldItem.id == newItem.id
       }

       override fun areContentsTheSame(oldItem: RoomMasters, newItem: RoomMasters): Boolean {
           return oldItem == newItem
       }
   }

    val differ = AsyncListDiffer(this,diffUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(
            R.layout.singe_view_room_layout, parent,false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = differ.currentList[position]
        holder.bindView(room)
        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let { it(room) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //Click Listener for views
    private var onItemClickListener:((RoomMasters)-> Unit)? = null

    fun setOnItemClickListener(listener: (RoomMasters)-> Unit){
        onItemClickListener= listener
    }
}