package com.taskflow.data.repository

import com.taskflow.data.local.dao.TagDao
import com.taskflow.data.local.dao.TaskDao
import com.taskflow.data.local.entity.TagEntity
import com.taskflow.data.local.entity.TaskTagCrossRef
import com.taskflow.domain.model.Tag
import com.taskflow.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val taskDao: TaskDao
) : TagRepository {

    override fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags().map { list -> list.map { it.toDomain() } }
    }

    override fun getTagsForTask(taskId: String): Flow<List<Tag>> {
        return tagDao.getTagsForTask(taskId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun createTag(tag: Tag): Result<Tag> {
        return try {
            val now = LocalDateTime.now()
            val entity = TagEntity(
                id = tag.id,
                name = tag.name,
                color = tag.color,
                createdAt = tag.createdAt,
                updatedAt = now
            )
            tagDao.insertTag(entity)
            Result.success(tag.copy(updatedAt = now))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTag(tagId: String): Result<Unit> {
        return try {
            tagDao.deleteTagById(tagId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setTaskTags(taskId: String, tagIds: List<String>): Result<Unit> {
        return try {
            taskDao.deleteTagsForTask(taskId)
            if (tagIds.isNotEmpty()) {
                val refs = tagIds.distinct().map { tagId -> TaskTagCrossRef(taskId = taskId, tagId = tagId) }
                taskDao.insertTaskTagCrossRefs(refs)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun TagEntity.toDomain(): Tag {
        return Tag(
            id = id,
            name = name,
            color = color,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
