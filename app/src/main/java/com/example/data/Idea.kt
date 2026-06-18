package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ideas")
data class Idea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val projectId: Int? = null, // null means unorganized (Inbox)
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val priority: String = "Medium", // Low, Medium, High
    val difficulty: String = "Medium" // Easy, Medium, Hard
)
