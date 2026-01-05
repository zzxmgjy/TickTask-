package com.taskflow.domain.repository

import com.taskflow.domain.model.Habit
import com.taskflow.domain.model.HabitCheckin
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 习惯仓储接口
 */
interface HabitRepository {
    
    fun getAllHabits(): Flow<List<Habit>>
    
    fun getHabitById(habitId: String): Flow<Habit?>
    
    fun getHabitsForDate(date: LocalDate): Flow<List<Habit>>
    
    fun getCheckinsForHabit(habitId: String): Flow<List<HabitCheckin>>
    
    fun getCheckinsForDateRange(habitId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<HabitCheckin>>
    
    suspend fun createHabit(habit: Habit): Result<Habit>
    
    suspend fun updateHabit(habit: Habit): Result<Habit>
    
    suspend fun deleteHabit(habitId: String): Result<Unit>
    
    suspend fun checkIn(habitId: String, note: String = ""): Result<HabitCheckin>
    
    suspend fun undoCheckIn(habitId: String, date: LocalDate): Result<Unit>
    
    suspend fun calculateStreak(habitId: String): Int
    
    suspend fun calculateTotalDays(habitId: String): Int
}
