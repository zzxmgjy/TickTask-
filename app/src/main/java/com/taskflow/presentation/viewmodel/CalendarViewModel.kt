package com.taskflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.domain.model.Task
import com.taskflow.domain.usecase.task.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import javax.inject.Inject

/**
 * 日历视图ViewModel
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()
    
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()
    
    private val _viewMode = MutableStateFlow(CalendarViewMode.MONTH)
    val viewMode: StateFlow<CalendarViewMode> = _viewMode.asStateFlow()
    
    // 所有任务（用于日历显示）
    val allTasks: StateFlow<List<Task>> = getTasksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // 选中日期的任务
    val tasksForSelectedDate: StateFlow<List<Task>> = combine(
        allTasks,
        _selectedDate
    ) { tasks, date ->
        tasks.filter { task ->
            task.dueDate?.toLocalDate() == date && !task.isCompleted
        }.sortedBy { it.dueDate }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    // 选中月份的任务（用于日历上的小点标记）
    val tasksForCurrentMonth: StateFlow<Map<LocalDate, Int>> = combine(
        allTasks,
        _currentMonth
    ) { tasks, month ->
        val startOfMonth = month.atDay(1).atStartOfDay()
        val endOfMonth = month.atEndOfMonth().atTime(23, 59, 59)
        
        tasks.filter { task ->
            task.dueDate?.let { it >= startOfMonth && it <= endOfMonth } == true
        }.groupBy { task ->
            task.dueDate!!.toLocalDate()
        }.mapValues { (_, tasks) -> tasks.size }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )
    
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1) }
    }
    
    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1) }
    }
    
    fun goToToday() {
        val today = LocalDate.now()
        _selectedDate.value = today
        _currentMonth.value = YearMonth.from(today)
    }
    
    fun setViewMode(mode: CalendarViewMode) {
        _viewMode.value = mode
    }
    
    fun getTasksForDate(date: LocalDate): List<Task> {
        return allTasks.value.filter { task ->
            task.dueDate?.toLocalDate() == date && !task.isCompleted
        }
    }
    
    fun getTasksCountForDate(date: LocalDate): Int {
        return tasksForCurrentMonth.value[date] ?: 0
    }
}

enum class CalendarViewMode {
    DAY,
    WEEK,
    MONTH,
    YEAR
}
