package com.ticktask.app.data.repository

import androidx.lifecycle.LiveData
import com.ticktask.app.data.dao.TaskDao
import com.ticktask.app.data.entity.Task

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

    fun getActiveTasks(): LiveData<List<Task>> = taskDao.getActiveTasks()

    fun getCompletedTasks(): LiveData<List<Task>> = taskDao.getCompletedTasks()

    fun getTasksForDay(startOfDay: Long, endOfDay: Long): LiveData<List<Task>> =
        taskDao.getTasksForDay(startOfDay, endOfDay)

    suspend fun insert(task: Task) = taskDao.insert(task)

    suspend fun update(task: Task) {
        task.updatedAt = System.currentTimeMillis()
        taskDao.update(task)
    }

    suspend fun delete(task: Task) = taskDao.delete(task)

    suspend fun getActiveTaskCount(): Int = taskDao.getActiveTaskCount()

    suspend fun getActiveTaskCountForDay(startOfDay: Long, endOfDay: Long): Int =
        taskDao.getActiveTaskCountForDay(startOfDay, endOfDay)

    suspend fun getById(id: Long): Task? = taskDao.getById(id)
}
