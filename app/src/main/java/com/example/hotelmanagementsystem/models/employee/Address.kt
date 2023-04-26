package com.example.hotelmanagementsystem.models.employee

import java.io.Serializable

data class Address(
    val id: Int?,
    val city: String,
    val pinCode: String,
    val societyName: String,
    val state: String,
    val customer: Any?,
    val employee: Any?,
): Serializable