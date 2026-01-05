package com.ticktask.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ticktask.app.data.database.AppDatabase
import com.ticktask.app.data.entity.Task
import com.ticktask.app.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>
    val activeTasks: LiveData<List<Task>>
    val completedTasks: LiveData<List<Task>>
    val todayTasks: LiveData<List<Task>>

    init {
        val taskDao = AppDatabase.getInstance(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.getAllTasks()
        activeTasks = repository.getActiveTasks()
        completedTasks = repository.getCompletedTasks()

        // Calculate today's start and end time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        todayTasks = repository.getTasksForDay(startOfDay, endOfDay)
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun toggleTaskComplete(task: Task) = viewModelScope.launch {
        task.isCompleted = !task.isCompleted
        repository.update(task)
    }

    fun getActiveTaskCount(): Int {
        return runBlocking {
            repository.getActiveTaskCount()
        }
    }

    fun getTodayTaskCount(): Int {
        return runBlocking {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = calendar.timeInMillis

            repository.getActiveTaskCountForDay(startOfDay, endOfDay)
        }
    }
}
