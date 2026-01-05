package com.taskflow.data.repository

import com.taskflow.data.local.dao.ListDao
import com.taskflow.data.local.entity.ListEntity
import com.taskflow.domain.model.SystemLists
import com.taskflow.domain.model.TaskList
import com.taskflow.domain.repository.ListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 清单仓储实现
 */
@Singleton
class ListRepositoryImpl @Inject constructor(
    private val listDao: ListDao
) : ListRepository {
    
    override fun getAllLists(): Flow<List<TaskList>> {
        return listDao.getAllLists().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getListById(listId: String): Flow<TaskList?> {
        return listDao.getListById(listId).map { it?.toDomain() }
    }
    
    override fun getSystemLists(): Flow<List<TaskList>> {
        return listDao.getSystemLists().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getUserLists(): Flow<List<TaskList>> {
        return listDao.getUserLists().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun createList(list: TaskList): Result<TaskList> {
        return try {
            listDao.insertList(list.toEntity())
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateList(list: TaskList): Result<TaskList> {
        return try {
            listDao.updateList(list.toEntity())
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteList(listId: String): Result<Unit> {
        return try {
            listDao.deleteListById(listId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun reorderLists(lists: List<String>): Result<Unit> {
        return try {
            listDao.reorderLists(lists)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTaskCount(listId: String): Int {
        return listDao.getTotalTaskCountByList(listId)
    }
    
    // Mapping functions
    private fun ListEntity.toDomain(): TaskList {
        return TaskList(
            id = id,
            name = name,
            icon = icon,
            color = color,
            parentId = parentId,
            isSystem = isSystem,
            sortOrder = sortOrder,
            taskCount = 0, // 需要单独查询
            completedCount = 0,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun TaskList.toEntity(): ListEntity {
        return ListEntity(
            id = id,
            name = name,
            icon = icon,
            color = color,
            parentId = parentId,
            isSystem = isSystem,
            sortOrder = sortOrder,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

// 工厂类 - 用于创建默认清单
object ListFactory {
    
    fun createInboxList(): TaskList {
        return TaskList(
            id = SystemLists.INBOX,
            name = "收集箱",
            icon = "inbox",
            color = 0xFF6200EE,
            isSystem = true,
            sortOrder = 0
        )
    }
    
    fun createTodayList(): TaskList {
        return TaskList(
            id = SystemLists.TODAY,
            name = "今天",
            icon = "today",
            color = 0xFF2196F3,
            isSystem = true,
            sortOrder = 1
        )
    }
    
    fun createTomorrowList(): TaskList {
        return TaskList(
            id = SystemLists.TOMORROW,
            name = "明天",
            icon = "tomorrow",
            color = 0xFF4CAF50,
            isSystem = true,
            sortOrder = 2
        )
    }
    
    fun createThisWeekList(): TaskList {
        return TaskList(
            id = SystemLists.THIS_WEEK,
            name = "本周",
            icon = "week",
            color = 0xFFFF9800,
            isSystem = true,
            sortOrder = 3
        )
    }
    
    fun createDefaultLists(): List<TaskList> {
        return listOf(
            createInboxList(),
            createTodayList(),
            createTomorrowList(),
            createThisWeekList()
        )
    }
}
