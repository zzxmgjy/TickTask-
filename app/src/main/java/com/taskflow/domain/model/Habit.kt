package com.taskflow.domain.model

import java.time.LocalDateTime

/**
 * 习惯领域模型
 */
data class Habit(
    val id: String,
    val name: String,
    val icon: String,
    val color: Long,
    val targetDaysPerWeek: Int = 7,
    val reminderTime: LocalDateTime? = null,
    val streak: Int = 0,
    val totalDays: Int = 0,
    val isArchived: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    val completionRate: Float
        get() = if (totalDays > 0) (streak.toFloat() / totalDays).coerceIn(0f, 1f) else 0f
}

/**
 * 习惯打卡记录
 */
data class HabitCheckin(
    val id: String,
    val habitId: String,
    val date: LocalDateTime,
    val note: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * 习惯统计
 */
data class HabitStats(
    val habitId: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCheckins: Int,
    val completionRate: Float,
    val weeklyProgress: List<Boolean>, // 最近7天的打卡情况
    val monthlyProgress: Map<Int, Boolean> // 日期 -> 是否打卡
)
