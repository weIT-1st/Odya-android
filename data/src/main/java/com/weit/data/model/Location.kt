package com.weit.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val time: Long,
    val lat: Float,
    val lng: Float
)