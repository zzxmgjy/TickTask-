package com.taskflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.taskflow.data.local.entity.TaskEntity
import com.taskflow.data.local.entity.TaskTagCrossRef
import com.taskflow.data.local.entity.TaskWithRelations
import kotlinx.coroutines.flow.Flow

/**
 * 任务数据访问对象
 */
@Dao
interface TaskDao {

    @Transaction
    @Query(
        """
        SELECT * FROM tasks
        WHERE parentId IS NULL AND syncStatus != :deletedStatus
        ORDER BY sortOrder ASC, updatedAt DESC
        """
    )
    fun getAllTasksWithRelations(
        deletedStatus: TaskEntity.SyncStatus
    ): Flow<List<TaskWithRelations>>

    @Transaction
    @Query(
        """
        SELECT * FROM tasks
        WHERE listId = :listId AND parentId IS NULL AND syncStatus != :deletedStatus
        ORDER BY sortOrder ASC, updatedAt DESC
        """
    )
    fun getTasksByListWithRelations(
        listId: String,
        deletedStatus: TaskEntity.SyncStatus
    ): Flow<List<TaskWithRelations>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun getTaskByIdWithRelations(taskId: String): Flow<TaskWithRelations?>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskByIdSync(taskId: String): TaskEntity?

    @Transaction
    @Query(
        """
        SELECT * FROM tasks
        WHERE parentId IS NULL
          AND syncStatus != :deletedStatus
          AND (title LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%')
        ORDER BY updatedAt DESC
        """
    )
    fun searchTasksWithRelations(
        keyword: String,
        deletedStatus: TaskEntity.SyncStatus
    ): Flow<List<TaskWithRelations>>

    @Query("SELECT * FROM tasks WHERE syncStatus != :syncedStatus")
    fun getUnsyncedTasks(syncedStatus: TaskEntity.SyncStatus): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET syncStatus = :syncStatus, isSynced = :isSynced WHERE id IN (:taskIds)")
    suspend fun updateSyncStatus(taskIds: List<String>, syncStatus: TaskEntity.SyncStatus, isSynced: Boolean)

    suspend fun markAsSynced(taskIds: List<String>) {
        updateSyncStatus(taskIds, TaskEntity.SyncStatus.SYNCED, true)
    }

    @Query("UPDATE tasks SET syncStatus = :deletedStatus WHERE id = :taskId")
    suspend fun markAsDeleted(taskId: String, deletedStatus: TaskEntity.SyncStatus)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun hardDeleteTask(taskId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskTagCrossRefs(refs: List<TaskTagCrossRef>)

    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId")
    suspend fun deleteTagsForTask(taskId: String)
}
