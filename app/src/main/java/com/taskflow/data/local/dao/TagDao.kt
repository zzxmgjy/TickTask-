package com.taskflow.data.local.dao

import androidx.room.*
import com.taskflow.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * 标签数据访问对象
 */
@Dao
interface TagDao {
    
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>
    
    @Query("SELECT * FROM tags WHERE id = :tagId")
    fun getTagById(tagId: String): Flow<TagEntity?>
    
    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagByIdSync(tagId: String): TagEntity?
    
    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN task_tag_cross_ref ttcr ON t.id = ttcr.tagId
        WHERE ttcr.taskId = :taskId
    """)
    fun getTagsForTask(taskId: String): Flow<List<TagEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)
    
    @Update
    suspend fun updateTag(tag: TagEntity)
    
    @Delete
    suspend fun deleteTag(tag: TagEntity)
    
    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTagById(tagId: String)
}
