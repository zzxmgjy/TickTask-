package com.taskflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.taskflow.data.local.dao.ListDao
import com.taskflow.data.local.dao.ReminderDao
import com.taskflow.data.local.dao.TagDao
import com.taskflow.data.local.dao.TaskDao
import com.taskflow.data.local.entity.HabitCheckinEntity
import com.taskflow.data.local.entity.HabitEntity
import com.taskflow.data.local.entity.ListEntity
import com.taskflow.data.local.entity.ReminderEntity
import com.taskflow.data.local.entity.TagEntity
import com.taskflow.data.local.entity.TaskEntity
import com.taskflow.data.local.entity.TaskTagCrossRef

@Database(
    entities = [
        TaskEntity::class,
        ListEntity::class,
        TagEntity::class,
        TaskTagCrossRef::class,
        ReminderEntity::class,
        HabitEntity::class,
        HabitCheckinEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class TaskFlowDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun listDao(): ListDao
    abstract fun tagDao(): TagDao
    abstract fun reminderDao(): ReminderDao
}
