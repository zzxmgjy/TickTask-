package com.taskflow.data.repository

import com.taskflow.data.local.dao.TaskDao
import com.taskflow.data.local.entity.TaskEntity
import com.taskflow.data.local.entity.TaskWithRelations
import com.taskflow.domain.model.Priority
import com.taskflow.domain.model.Subtask
import com.taskflow.domain.model.Tag
import com.taskflow.domain.model.Task
import com.taskflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getAllTasksWithRelations(TaskEntity.SyncStatus.DELETED).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getTaskById(taskId: String): Flow<Task?> {
        return taskDao.getTaskByIdWithRelations(taskId).map { it?.toDomain() }
    }

    override suspend fun getTaskByIdOnce(taskId: String): Task? {
        return taskDao.getTaskByIdWithRelations(taskId).first()?.toDomain()
    }

    override fun getTasksByList(listId: String): Flow<List<Task>> {
        return taskDao.getTasksByListWithRelations(listId, TaskEntity.SyncStatus.DELETED).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun searchTasks(keyword: String): Flow<List<Task>> {
        return taskDao.searchTasksWithRelations(keyword, TaskEntity.SyncStatus.DELETED)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getTasksForDate(date: LocalDate): Flow<List<Task>> {
        return getTasks().map { tasks ->
            tasks.filter { it.dueDate?.toLocalDate() == date }
        }
    }

    override suspend fun createTask(task: Task): Result<Task> {
        return try {
            val now = LocalDateTime.now()
            val entity = task.toEntity(updatedAt = now)
            taskDao.insertTask(entity.copy(updatedAt = now, syncStatus = TaskEntity.SyncStatus.MODIFIED, isSynced = false))
            Result.success(task.copy(updatedAt = now))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return try {
            val now = LocalDateTime.now()
            val entity = task.toEntity(updatedAt = now).copy(syncStatus = TaskEntity.SyncStatus.MODIFIED, isSynced = false)
            taskDao.updateTask(entity)
            Result.success(task.copy(updatedAt = now))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            taskDao.markAsDeleted(taskId, TaskEntity.SyncStatus.DELETED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeTask(taskId: String): Result<Task> {
        val current = getTaskByIdOnce(taskId) ?: return Result.failure(NoSuchElementException("Task not found"))
        return updateTask(
            current.copy(
                isCompleted = true,
                completedAt = LocalDateTime.now()
            )
        )
    }

    override suspend fun uncompleteTask(taskId: String): Result<Task> {
        val current = getTaskByIdOnce(taskId) ?: return Result.failure(NoSuchElementException("Task not found"))
        return updateTask(
            current.copy(
                isCompleted = false,
                completedAt = null
            )
        )
    }

    private fun TaskWithRelations.toDomain(): Task {
        return Task(
            id = task.id,
            title = task.title,
            description = task.description,
            listId = task.listId,
            parentId = task.parentId,
            isCompleted = task.isCompleted,
            completedAt = task.completedAt,
            priority = task.priority.toPriority(),
            dueDate = task.dueDate,
            startDate = task.startDate,
            reminder = task.reminder,
            repeatRuleJson = task.repeatRuleJson,
            tags = tags.map { Tag(id = it.id, name = it.name, color = it.color, createdAt = it.createdAt, updatedAt = it.updatedAt) },
            subtasks = subtasks.map { Subtask(id = it.id, title = it.title, isCompleted = it.isCompleted) },
            sortOrder = task.sortOrder,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }

    private fun Task.toEntity(
        createdAt: LocalDateTime = this.createdAt,
        updatedAt: LocalDateTime = this.updatedAt
    ): TaskEntity {
        return TaskEntity(
            id = id,
            title = title,
            description = description,
            listId = listId,
            parentId = parentId,
            isCompleted = isCompleted,
            completedAt = completedAt,
            priority = priority.toInt(),
            dueDate = dueDate,
            startDate = startDate,
            reminder = reminder,
            repeatRuleJson = repeatRuleJson,
            sortOrder = sortOrder,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isSynced = false,
            syncStatus = TaskEntity.SyncStatus.MODIFIED
        )
    }

    private fun Int.toPriority(): Priority {
        return when (this) {
            1 -> Priority.LOW
            2 -> Priority.MEDIUM
            3 -> Priority.HIGH
            else -> Priority.NONE
        }
    }

    private fun Priority.toInt(): Int {
        return when (this) {
            Priority.LOW -> 1
            Priority.MEDIUM -> 2
            Priority.HIGH -> 3
            Priority.NONE -> 0
        }
    }
}
