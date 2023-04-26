package com.example.hotelmanagementsystem.api

import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.customers.CustomerModel
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.models.employee.EmployeeModel
import com.example.hotelmanagementsystem.models.paymentInfo.Payment
import com.example.hotelmanagementsystem.models.paymentInfo.PaymentModel
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.models.room.RoomModelX
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import retrofit2.Response
import retrofit2.http.*

interface RoomApi {

    @GET("/room")
    suspend fun getAllRooms(): Response<RoomModelX>

    @GET("/employee")
    suspend fun getAllEmployee():Response<EmployeeModel>

    @GET("/customer")
    suspend fun getAllCustomer():Response<CustomerModel>

    @GET("/payments")
    suspend fun getAllPayments():Response<PaymentModel>

    @POST("/payments/add")
    suspend fun addPayment(
        @Body payment: Payment
    ):Response<Payment>

    @POST("/customer/add")
    suspend fun addCustomer(
        @Body customer: Customer
    ):Response<Customer>

    @POST("/room/add")
    suspend fun addRoom(
        @Body roomMasters: RoomMasters
    ):Response<RoomMasters>

    @POST("/employee/add")
    suspend fun addEmployee(
        @Body employee: Employee
    ):Response<Employee>

    @PUT("/room/update/{id}")
    suspend fun updateRoom(
        @Path("id")
        roomId: Int,
        @Body roomMasters: RoomMasters
    ):Response<RoomModelX>

    @PUT("/customer/update/{id}")
    suspend fun updateBookingDetails(
        @Path("id")
        customerId: Int,
        @Body customer: Customer
    ):Response<Customer>

    @PUT("/employee/update/{id}")
    suspend fun updateEmployee(
        @Path("id")
        employeeId: Int,
        @Body employee: Employee
    ):Response<Employee>

    @DELETE("/room/delete/{id}")
    suspend fun deleteRoom(
        @Path("id")
        roomId:Int
    ):Response<RoomModelX>

    @DELETE("/customer/delete/{id}")
    suspend fun deleteCustomer(
        @Path("id")
        customerId:Int
    ):Response<Customer>

    @DELETE("/employee/delete/{id}")
    suspend fun deleteEmployee(
        @Path("id")
        employeeId:Int
    ):Response<EmployeeModel>

    @GET("/room/{id}")
    suspend fun findRoomById(
        @Path("id")
        roomId: Int
    ):Response<RoomMasters>

    @GET("/customer/search")
    suspend fun findCustomerByName(
        @Query("name")
        name:String
    ):Response<CustomerModel>

}