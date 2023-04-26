package com.example.hotelmanagementsystem.models.customers

import java.io.Serializable

data class Guest(
    val id:Int?,
    val name:String?,
    val gender:String?,
    val age:String?
): Serializable
