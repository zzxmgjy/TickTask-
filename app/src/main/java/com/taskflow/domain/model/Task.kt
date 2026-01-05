package com.taskflow.domain.model

import java.time.LocalDateTime

/**
 * 任务领域模型
 */
data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val listId: String = SystemLists.INBOX,
    val parentId: String? = null,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null,
    val priority: Priority = Priority.NONE,
    val dueDate: LocalDateTime? = null,
    val startDate: LocalDateTime? = null,
    val reminder: LocalDateTime? = null,
    val repeatRuleJson: String? = null,
    val tags: List<Tag> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val sortOrder: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val hasReminder: Boolean
        get() = reminder != null
}

/**
 * 子任务（仅用于展示/编辑）
 */
data class Subtask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)
