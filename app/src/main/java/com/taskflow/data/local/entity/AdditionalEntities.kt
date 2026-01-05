package com.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 提醒实体
 */
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey
    val id: String,
    val taskId: String,
    val triggerAt: LocalDateTime,
    val isTriggered: Boolean,
    val isSnoozed: Boolean,
    val snoozeUntil: LocalDateTime?,
    val notificationMethod: String, // APP, EMAIL, WECHAT
    val intervalMinutes: Int,
    val createdAt: LocalDateTime
)

/**
 * 习惯实体
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val color: Long,
    val targetDaysPerWeek: Int = 7,
    val reminderTime: LocalDateTime?,
    val streak: Int = 0,
    val totalDays: Int = 0,
    val isArchived: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * 习惯打卡记录实体
 */
@Entity(
    tableName = "habit_checkins",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["habitId"])]
)
data class HabitCheckinEntity(
    @PrimaryKey
    val id: String,
    val habitId: String,
    val date: LocalDateTime,
    val note: String = "",
    val createdAt: LocalDateTime
)
