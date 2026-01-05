package com.taskflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.domain.model.Task
import com.taskflow.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 任务详情ViewModel
 */
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                taskRepository.getTaskById(taskId)
                    .catch { e ->
                        _error.value = e.message
                    }
                    .collect { task ->
                        _task.value = task
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            taskRepository.updateTask(task)
                .onSuccess { updatedTask ->
                    _task.value = updatedTask
                    _isLoading.value = false
                }
                .onFailure { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
        }
    }
    
    fun toggleComplete() {
        val currentTask = _task.value ?: return
        viewModelScope.launch {
            if (currentTask.isCompleted) {
                taskRepository.uncompleteTask(currentTask.id)
                    .onSuccess { updatedTask ->
                        _task.value = updatedTask
                    }
                    .onFailure { e ->
                        _error.value = e.message
                    }
            } else {
                taskRepository.completeTask(currentTask.id)
                    .onSuccess { updatedTask ->
                        _task.value = updatedTask
                    }
                    .onFailure { e ->
                        _error.value = e.message
                    }
            }
        }
    }
    
    fun deleteTask() {
        val taskId = _task.value?.id ?: return
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }
    
    fun updateTitle(title: String) {
        val currentTask = _task.value ?: return
        updateTask(currentTask.copy(title = title))
    }
    
    fun updateDescription(description: String) {
        val currentTask = _task.value ?: return
        updateTask(currentTask.copy(description = description))
    }
    
    fun updateDueDate(dueDate: java.time.LocalDateTime?) {
        val currentTask = _task.value ?: return
        updateTask(currentTask.copy(dueDate = dueDate))
    }
    
    fun clearError() {
        _error.value = null
    }
}
