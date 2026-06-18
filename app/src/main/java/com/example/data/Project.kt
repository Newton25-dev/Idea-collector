package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val colorHex: String = "#4F46E5", // Modern deep outline color by default
    val createdAt: Long = System.currentTimeMillis()
)
