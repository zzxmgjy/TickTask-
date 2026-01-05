package com.taskflow.domain.repository

import com.taskflow.domain.model.Tag
import kotlinx.coroutines.flow.Flow

/**
 * 标签仓储接口
 */
interface TagRepository {

    fun getAllTags(): Flow<List<Tag>>

    fun getTagsForTask(taskId: String): Flow<List<Tag>>

    suspend fun createTag(tag: Tag): Result<Tag>

    suspend fun deleteTag(tagId: String): Result<Unit>

    suspend fun setTaskTags(taskId: String, tagIds: List<String>): Result<Unit>
}
