package com.taskflow.domain.usecase.task

import com.taskflow.domain.model.Task
import com.taskflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> = taskRepository.getTasks()
}
