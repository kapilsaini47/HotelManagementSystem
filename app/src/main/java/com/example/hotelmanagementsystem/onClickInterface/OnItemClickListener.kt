package com.example.hotelmanagementsystem.onClickInterface

import android.view.MenuItem
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.models.room.RoomMasters

interface OnItemClickListener {
    fun onItemClick(position:Int,menuItem:MenuItem, item:RoomMasters)
    fun onItemClickOfCustomer(position: Int,menuItem: MenuItem,item:Customer)
    fun onItemClickOfEmployee(position: Int,menuItem: MenuItem,item:Employee)
}