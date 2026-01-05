package com.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 任务实体
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val listId: String,
    val parentId: String?,
    val isCompleted: Boolean,
    val completedAt: LocalDateTime?,
    /**
     * 优先级（0 NONE, 1 LOW, 2 MEDIUM, 3 HIGH）
     */
    val priority: Int,
    val dueDate: LocalDateTime?,
    val startDate: LocalDateTime?,
    val reminder: LocalDateTime?,
    val repeatRuleJson: String?,
    val sortOrder: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isSynced: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.MODIFIED
) {
    enum class SyncStatus {
        SYNCED,
        MODIFIED,
        DELETED
    }
}
