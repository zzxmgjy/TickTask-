package com.taskflow.data.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.taskflow.data.local.dao.TaskDao
import com.taskflow.data.local.dao.ListDao
import com.taskflow.data.local.entity.TaskEntity
import com.taskflow.data.local.entity.ListEntity
import com.taskflow.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 同步管理器 - 处理离线优先的数据同步
 */
@Singleton
class SyncManager @Inject constructor(
    private val taskDao: TaskDao,
    private val listDao: ListDao,
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")
    
    /**
     * 同步所有本地修改到云端
     */
    suspend fun syncAll(): SyncResult {
        return try {
            // 同步任务
            syncTasks()
            // 同步清单
            syncLists()
            
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "同步失败")
        }
    }
    
    /**
     * 同步任务数据
     */
    private suspend fun syncTasks() {
        val unsyncedTasks = taskDao.getUnsyncedTasks(TaskEntity.SyncStatus.SYNCED).first()
        
        unsyncedTasks.forEach { taskEntity ->
            try {
                when (taskEntity.syncStatus) {
                    com.taskflow.data.local.entity.TaskEntity.SyncStatus.MODIFIED -> {
                        upsertTaskToCloud(taskEntity)
                    }
                    com.taskflow.data.local.entity.TaskEntity.SyncStatus.DELETED -> {
                        deleteTaskFromCloud(taskEntity.id)
                    }
                }
                taskDao.markAsSynced(listOf(taskEntity.id))
            } catch (e: Exception) {
                // 记录失败，下次重试
            }
        }
    }
    
    /**
     * 同步清单数据
     */
    private suspend fun syncLists() {
        // TODO: 实现清单同步
    }
    
    /**
     * 从云端拉取最新数据
     */
    suspend fun pullFromCloud(userId: String): SyncResult {
        return try {
            val tasksSnapshot = usersCollection
                .document(userId)
                .collection("tasks")
                .get()
                .await()
            
            val listsSnapshot = usersCollection
                .document(userId)
                .collection("lists")
                .get()
                .await()
            
            // 处理任务数据
            for (document in tasksSnapshot.documents) {
                val taskData = document.data ?: continue
                val localTask = taskDao.getTaskByIdSync(document.id)
                
                if (localTask == null) {
                    // 本地不存在，创建
                    val taskEntity = document.toTaskEntity().copy(
                        isSynced = true,
                        syncStatus = TaskEntity.SyncStatus.SYNCED
                    )
                    taskDao.insertTask(taskEntity)
                } else {
                    val remoteUpdatedAt = document.getString("updatedAt")
                        ?.let { runCatching { java.time.LocalDateTime.parse(it) }.getOrNull() }

                    if (remoteUpdatedAt != null && remoteUpdatedAt.isAfter(localTask.updatedAt)) {
                        // 云端更新，更新本地
                        val taskEntity = document.toTaskEntity().copy(
                            updatedAt = remoteUpdatedAt,
                            isSynced = true,
                            syncStatus = TaskEntity.SyncStatus.SYNCED
                        )
                        taskDao.updateTask(taskEntity)
                    }
                }
            }
            
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "拉取失败")
        }
    }
    
    /**
     * 将任务上传到云端
     */
    private suspend fun upsertTaskToCloud(task: TaskEntity) {
        val taskData = hashMapOf(
            "id" to task.id,
            "title" to task.title,
            "description" to task.description,
            "listId" to task.listId,
            "parentId" to task.parentId,
            "isCompleted" to task.isCompleted,
            "completedAt" to task.completedAt.toString(),
            "priority" to task.priority,
            "dueDate" to task.dueDate.toString(),
            "startDate" to task.startDate.toString(),
            "reminder" to task.reminder.toString(),
            "repeatRuleJson" to task.repeatRuleJson,
            "sortOrder" to task.sortOrder,
            "createdAt" to task.createdAt.toString(),
            "updatedAt" to task.updatedAt.toString()
        )
        
        usersCollection
            .document("currentUserId") // 实际使用时应替换为真实用户ID
            .collection("tasks")
            .document(task.id)
            .set(taskData)
            .await()
    }
    
    /**
     * 从云端删除任务
     */
    private suspend fun deleteTaskFromCloud(taskId: String) {
        usersCollection
            .document("currentUserId")
            .collection("tasks")
            .document(taskId)
            .delete()
            .await()
    }
    
    /**
     * 监听云端实时变化
     */
    fun observeCloudChanges(userId: String) {
        usersCollection
            .document(userId)
            .collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                
                // 处理变化
                snapshot.documentChanges.forEach { change ->
                    when (change.type) {
                        com.google.firebase.firestore.DocumentChange.Type.ADDED -> {
                            // 新增
                        }
                        com.google.firebase.firestore.DocumentChange.Type.MODIFIED -> {
                            // 修改
                        }
                        com.google.firebase.firestore.DocumentChange.Type.REMOVED -> {
                            // 删除
                        }
                    }
                }
            }
    }
}

sealed class SyncResult {
    data object Success : SyncResult()
    data class Error(val message: String) : SyncResult()
    data object Conflict : SyncResult()
}

// Extension function to convert Firestore document to TaskEntity
private fun com.google.firebase.firestore.DocumentSnapshot.toTaskEntity(): TaskEntity {
    val data = data ?: throw IllegalArgumentException("Document data is null")
    return TaskEntity(
        id = getString("id") ?: id,
        title = getString("title") ?: "",
        description = getString("description") ?: "",
        listId = getString("listId") ?: "",
        parentId = getString("parentId"),
        isCompleted = getBoolean("isCompleted") ?: false,
        completedAt = null, // Parse from string if needed
        priority = getLong("priority")?.toInt() ?: 0,
        dueDate = null, // Parse from string if needed
        startDate = null,
        reminder = null,
        repeatRuleJson = getString("repeatRuleJson"),
        sortOrder = getLong("sortOrder")?.toInt() ?: 0,
        createdAt = java.time.LocalDateTime.now(),
        updatedAt = java.time.LocalDateTime.now(),
        isSynced = true,
        syncStatus = com.taskflow.data.local.entity.TaskEntity.SyncStatus.SYNCED
    )
}
