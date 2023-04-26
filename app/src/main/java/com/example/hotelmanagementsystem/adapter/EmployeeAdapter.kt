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
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.onClickInterface.OnItemClickListener
import com.squareup.picasso.Picasso

class EmployeeAdapter(
    private val onItemClickListenerInterface: OnItemClickListener
):RecyclerView.Adapter<EmployeeAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),
        View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
        val age = itemView.findViewById<TextView>(R.id.tvAge)
        val gender = itemView.findViewById<TextView>(R.id.tvGender)
        val empId = itemView.findViewById<TextView>(R.id.tvRoomNo)
        val designation = itemView.findViewById<TextView>(R.id.tvRoomCategory)
        val state = itemView.findViewById<TextView>(R.id.tvState)
        val area = itemView.findViewById<TextView>(R.id.tvArea)
        val pin = itemView.findViewById<TextView>(R.id.tvPin)
        val image = itemView.findViewById<ImageView>(R.id.imageView)
        val ivMenu = itemView.findViewById<ImageView>(R.id.ivMenu)

        fun bind(employee: Employee){
            tvName.text = employee.name.toString()
            age.text = employee.age.toString()
            gender.text = employee.gender.toString()
            empId.text = employee.eId.toString()
            state.text = employee.address.state
            area.text = employee.address.societyName
            pin.text = employee.address.pinCode
            designation.text = employee.position.toString()
            ivMenu.setOnClickListener(this)
            Picasso.get().load(employee.profileImage).error(R.drawable.person).into(image)
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
                onItemClickListenerInterface.onItemClickOfEmployee(position,menu,differ.currentList[position])
            }
            return false
        }
    }

    private val diffUtilCallBack = object :DiffUtil.ItemCallback<Employee>(){
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtilCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.single_view_customer_layout, parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val personList = differ.currentList[position]
        holder.bind(personList)
        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let { it(personList) }
            }
        }
    }

    //Click listener for views
    private var onItemClickListener:((Employee)-> Unit)? = null

    fun setOnItemClickListener(listener: (Employee)-> Unit){
        onItemClickListener= listener
    }
}