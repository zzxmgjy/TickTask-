package com.taskflow.domain.model

import java.time.LocalDateTime

/**
 * 清单/任务分类
 */
data class TaskList(
    val id: String,
    val name: String,
    val icon: String = "",
    val color: Long = 0xFF6200EE,
    val parentId: String? = null,
    val isSystem: Boolean = false,
    val sortOrder: Int = 0,
    val taskCount: Int = 0,
    val completedCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
