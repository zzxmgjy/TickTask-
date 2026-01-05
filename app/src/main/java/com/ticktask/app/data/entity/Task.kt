package com.ticktask.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    var isCompleted: Boolean = false,
    var priority: Int = 0, // 0: None, 1: Low, 2: Medium, 3: High
    var dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)
