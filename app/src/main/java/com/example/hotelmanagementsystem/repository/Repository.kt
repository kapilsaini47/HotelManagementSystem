package com.example.hotelmanagementsystem.repository

import com.example.hotelmanagementsystem.api.RetrofitInstance
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.models.paymentInfo.Payment
import com.example.hotelmanagementsystem.models.room.RoomMasters

class Repository() {

    suspend fun getAllRooms() =
        RetrofitInstance.api.getAllRooms()

    suspend fun getAllEmployee() =
        RetrofitInstance.api.getAllEmployee()

    suspend fun getAllCustomer() =
        RetrofitInstance.api.getAllCustomer()

    suspend fun getAllPayments() =
        RetrofitInstance.api.getAllPayments()

    suspend fun addPayment(payment: Payment) = RetrofitInstance.api.addPayment(payment)

    suspend fun addRoom(roomMasters: RoomMasters) =
        RetrofitInstance.api.addRoom(roomMasters)

    suspend fun addEmployee(employee: Employee) =
        RetrofitInstance.api.addEmployee(employee)

    suspend fun addCustomer(customer: Customer) =
        RetrofitInstance.api.addCustomer(customer)

    suspend fun updateRoom(id:Int,roomMasters: RoomMasters) =
        RetrofitInstance.api.updateRoom(id,roomMasters)

    suspend fun updateBookingDetails(id: Int,customer: Customer) =
        RetrofitInstance.api.updateBookingDetails(id,customer)

    suspend fun updateEmployee(id:Int,employee: Employee) =
        RetrofitInstance.api.updateEmployee(id,employee)

    suspend fun deleteRoom(id: Int) =
        RetrofitInstance.api.deleteRoom(id)

    suspend fun deleteCustomer(id: Int) =
        RetrofitInstance.api.deleteCustomer(id)

    suspend fun deleteEmployee(id: Int) =
        RetrofitInstance.api.deleteEmployee(id)

    suspend fun findRoomById(id: Int) =
        RetrofitInstance.api.findRoomById(id)

    suspend fun findCustomerByName(name:String) =
        RetrofitInstance.api.findCustomerByName(name)

}
