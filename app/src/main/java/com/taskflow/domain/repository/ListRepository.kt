package com.taskflow.domain.repository

import com.taskflow.domain.model.TaskList
import kotlinx.coroutines.flow.Flow

/**
 * 清单仓储接口
 */
interface ListRepository {

    fun getAllLists(): Flow<List<TaskList>>

    fun getSystemLists(): Flow<List<TaskList>>

    fun getUserLists(): Flow<List<TaskList>>

    fun getListById(listId: String): Flow<TaskList?>

    suspend fun createList(list: TaskList): Result<TaskList>

    suspend fun updateList(list: TaskList): Result<TaskList>

    suspend fun deleteList(listId: String): Result<Unit>

    suspend fun reorderLists(lists: List<String>): Result<Unit>

    suspend fun getTaskCount(listId: String): Int
}
