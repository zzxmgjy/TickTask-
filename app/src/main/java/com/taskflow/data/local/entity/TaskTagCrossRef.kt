package com.taskflow.data.local.entity

import androidx.room.Entity

/**
 * 任务-标签关联表
 */
@Entity(
    tableName = "task_tag_cross_ref",
    primaryKeys = ["taskId", "tagId"]
)
data class TaskTagCrossRef(
    val taskId: String,
    val tagId: String
)
