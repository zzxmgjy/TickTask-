package com.taskflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.taskflow.data.local.entity.ListEntity
import kotlinx.coroutines.flow.Flow

/**
 * 清单数据访问对象
 */
@Dao
interface ListDao {

    @Query("SELECT * FROM task_lists ORDER BY sortOrder ASC")
    fun getAllLists(): Flow<List<ListEntity>>

    @Query("SELECT * FROM task_lists WHERE isSystem = 0 ORDER BY sortOrder ASC")
    fun getUserLists(): Flow<List<ListEntity>>

    @Query("SELECT * FROM task_lists WHERE isSystem = 1 ORDER BY sortOrder ASC")
    fun getSystemLists(): Flow<List<ListEntity>>

    @Query("SELECT * FROM task_lists WHERE id = :listId")
    fun getListById(listId: String): Flow<ListEntity?>

    @Query("SELECT * FROM task_lists WHERE id = :listId")
    suspend fun getListByIdSync(listId: String): ListEntity?

    @Query("SELECT COUNT(*) FROM task_lists WHERE parentId = :parentId")
    suspend fun getChildCount(parentId: String): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE listId = :listId AND parentId IS NULL")
    suspend fun getTotalTaskCountByList(listId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLists(lists: List<ListEntity>)

    @Update
    suspend fun updateList(list: ListEntity)

    @Delete
    suspend fun deleteList(list: ListEntity)

    @Query("DELETE FROM task_lists WHERE id = :listId")
    suspend fun deleteListById(listId: String)

    @Query("UPDATE task_lists SET sortOrder = :sortOrder WHERE id = :listId")
    suspend fun updateSortOrder(listId: String, sortOrder: Int)

    @Transaction
    suspend fun reorderLists(lists: List<String>) {
        lists.forEachIndexed { index, listId ->
            updateSortOrder(listId, index)
        }
    }
}
