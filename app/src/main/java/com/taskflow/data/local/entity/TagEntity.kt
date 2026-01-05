package com.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 标签实体
 */
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
