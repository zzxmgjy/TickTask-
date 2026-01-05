package com.taskflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.domain.model.Priority
import com.taskflow.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val tasks: StateFlow<List<com.taskflow.domain.model.Task>> = taskRepository.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun toggleCompleted(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            if (isCompleted) {
                taskRepository.completeTask(taskId)
            } else {
                taskRepository.uncompleteTask(taskId)
            }
        }
    }

    fun updatePriority(taskId: String, priority: Priority) {
        viewModelScope.launch {
            val task = taskRepository.getTaskByIdOnce(taskId) ?: return@launch
            taskRepository.updateTask(task.copy(priority = priority))
        }
    }
}
