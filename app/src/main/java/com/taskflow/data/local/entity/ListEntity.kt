package com.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 清单实体
 */
@Entity(tableName = "task_lists")
data class ListEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val color: Long,
    val parentId: String?,
    val isSystem: Boolean,
    val sortOrder: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
