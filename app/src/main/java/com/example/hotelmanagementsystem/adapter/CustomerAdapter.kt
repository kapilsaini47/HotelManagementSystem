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
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.onClickInterface.OnItemClickListener
import com.squareup.picasso.Picasso

class CustomerAdapter(
    private val onItemClickListenerInterface: OnItemClickListener
):RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    inner class CustomerViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        val name: TextView = itemView.findViewById(R.id.tvName)
        val age: TextView = itemView.findViewById(R.id.tvAge)
        val gender: TextView = itemView.findViewById(R.id.tvGender)
        private val roomNo: TextView = itemView.findViewById(R.id.tvRoomNo)
        val roomCategory: TextView = itemView.findViewById(R.id.tvRoomCategory)
        private val state: TextView = itemView.findViewById(R.id.tvState)
        private val area: TextView = itemView.findViewById(R.id.tvArea)
        private val pin: TextView = itemView.findViewById(R.id.tvPin)
        private val image: ImageView = itemView.findViewById(R.id.imageView)
        private val ivMenu: ImageView = itemView.findViewById(R.id.ivMenu)

        fun bind(customer: Customer){
            name.text = customer.name
            age.text = customer.age.toString()
            gender.text = customer.gender
            state.text = customer.address.state
            area.text = customer.address.societyName
            pin.text = customer.address.pinCode.toString()
            roomNo.text = customer.roomNo.toString()
            //roomCategory.text = customer.roomStatus.toString()
            ivMenu.setOnClickListener(this)
            Picasso.get().load(customer.documentImage).error(R.drawable.document_image).into(image)
        }

        override fun onClick(v: View?) {
            val popupMenu = PopupMenu(v?.context,v)
            popupMenu.menuInflater.inflate(R.menu.item_view_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        override fun onMenuItemClick(menu: MenuItem?): Boolean {
            val position = adapterPosition
            if (menu != null) {
                onItemClickListenerInterface.onItemClickOfCustomer(position,menu,differ.currentList[position])
            }
            return false
        }

    }

    private val customerDiffUtilCallback = object : DiffUtil.ItemCallback<Customer>(){
        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,customerDiffUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        return CustomerViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.single_view_customer_layout,parent,false
        ))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customerList = differ.currentList[position]
        holder.bind(customerList)
        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let { it(customerList) }
            }
        }
    }

    //Click Listener for views
    private var onItemClickListener:((Customer)-> Unit)? = null

    fun setOnItemClickListener(listener: (Customer)-> Unit){
        onItemClickListener= listener
    }
}