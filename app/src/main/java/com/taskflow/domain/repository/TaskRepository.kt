package com.taskflow.domain.repository

import com.taskflow.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 任务仓储接口
 */
interface TaskRepository {

    fun getTasks(): Flow<List<Task>>

    fun getTaskById(taskId: String): Flow<Task?>

    suspend fun getTaskByIdOnce(taskId: String): Task?

    fun getTasksByList(listId: String): Flow<List<Task>>

    fun searchTasks(keyword: String): Flow<List<Task>>

    fun getTasksForDate(date: LocalDate): Flow<List<Task>>

    suspend fun createTask(task: Task): Result<Task>

    suspend fun updateTask(task: Task): Result<Task>

    suspend fun deleteTask(taskId: String): Result<Unit>

    suspend fun completeTask(taskId: String): Result<Task>

    suspend fun uncompleteTask(taskId: String): Result<Task>
}
