package com.example.hotelmanagementsystem.models.customers

import java.io.Serializable

data class Customer(
    val address: Address,
    val age: String?,
    val checkInDate: String?,
    val checkOutDate: String?,
    val gender: String?,
    val guests: Guest,
    val id: Int?,
    val roomId:String?,
    val name: String?,
    val contactNumber: String?,
    val totalAmount:String?,
    val roomNo: Int?,
    val documentImage:String?,
    val orderId:String?
): Serializable