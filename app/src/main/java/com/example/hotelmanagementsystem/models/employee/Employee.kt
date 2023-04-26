package com.example.hotelmanagementsystem.models.employee

import java.io.Serializable

data class Employee(
    val id: Int?,
    val address: Address,
    val age: String?,
    val eId: String?,
    val gender: String?,
    val profileImage: String?,
    val name: String?,
    val position: String?,
): Serializable