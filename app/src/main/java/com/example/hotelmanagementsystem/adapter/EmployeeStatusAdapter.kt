package com.example.hotelmanagementsystem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.models.employee.Employee
import com.google.android.material.switchmaterial.SwitchMaterial
import com.squareup.picasso.Picasso

class EmployeeStatusAdapter:RecyclerView.Adapter<EmployeeStatusAdapter.StatusViewHolder>() {


    inner class StatusViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),
        CompoundButton.OnCheckedChangeListener {
        private val empName:TextView = itemView.findViewById(R.id.tvEmpName)
        private val designation:TextView = itemView.findViewById(R.id.tvDesignation)
        val status:TextView = itemView.findViewById(R.id.tvEmpStatus)
        val switch:SwitchMaterial = itemView.findViewById(R.id.switch1)
        private val empImage:ImageView = itemView.findViewById(R.id.ivEmployeeImage)
        val sharedPref = itemView.context.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);

        fun bindViews(employee: Employee){
            empName.text = employee.name.toString()
            designation.text = employee.position.toString()
            switch.setOnCheckedChangeListener(this)
            // Load the switch state from SharedPreferences
            val isPresent = sharedPref.getBoolean("isPresent_$position", false)
            switch.isChecked = isPresent
            Picasso.get().load(employee.profileImage).error(R.drawable.person).into(empImage)
        }

        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
         //   status.text = if (p1) "Present" else "Absent"
            sharedPref.edit().putBoolean("isPresent_$position", p1).apply()
            val check = sharedPref.getBoolean("isPresent_$position",false)
            status.text = if (check) "Present" else "Absent"
        }

    }

    private val customerDiffUtilCallback = object : DiffUtil.ItemCallback<Employee>(){
        override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,customerDiffUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
       return StatusViewHolder(LayoutInflater.from(parent.context).inflate(
           R.layout.single_view_employee_status, parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val employeePosition = differ.currentList[position]
        holder.bindViews(employeePosition)

    }
}