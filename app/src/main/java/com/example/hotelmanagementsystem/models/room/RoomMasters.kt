package com.example.hotelmanagementsystem.models.room

import java.io.Serializable

data class RoomMasters(
    val id: Int?,
    val price: String?,
    val roomCategory: String?,
    val roomNo: String?,
    val roomPicture: String?,
    val status: String?,
    val forGuest: String?,
    val roomDescription:String?
): Serializable