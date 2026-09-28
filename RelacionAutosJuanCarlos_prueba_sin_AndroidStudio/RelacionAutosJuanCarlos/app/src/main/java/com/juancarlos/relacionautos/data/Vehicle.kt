package com.juancarlos.relacionautos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehiculos")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoPath: String,
    val chassis: String,
    val model: String,
    val year: String,
    val mechanic: String,
    val observations: String,
    val createdAt: Long = System.currentTimeMillis()
)
