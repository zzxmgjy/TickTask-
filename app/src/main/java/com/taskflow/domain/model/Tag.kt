package com.taskflow.domain.model

import java.time.LocalDateTime

/**
 * 标签
 */
data class Tag(
    val id: String,
    val name: String,
    val color: Long = 0xFF9E9E9E,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
