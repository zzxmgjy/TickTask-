package com.taskflow.di

import android.content.Context
import androidx.room.Room
import com.taskflow.data.local.TaskFlowDatabase
import com.taskflow.data.local.dao.ListDao
import com.taskflow.data.local.dao.ReminderDao
import com.taskflow.data.local.dao.TagDao
import com.taskflow.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskFlowDatabase {
        return Room.databaseBuilder(
            context,
            TaskFlowDatabase::class.java,
            "taskflow.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTaskDao(db: TaskFlowDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideListDao(db: TaskFlowDatabase): ListDao = db.listDao()

    @Provides
    fun provideTagDao(db: TaskFlowDatabase): TagDao = db.tagDao()

    @Provides
    fun provideReminderDao(db: TaskFlowDatabase): ReminderDao = db.reminderDao()
}
