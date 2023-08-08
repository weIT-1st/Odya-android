package com.weit.data.model.coordinate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coordinate")
data class Coordinate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val time: Long,
    val lat: Float,
    val lng: Float,
)
