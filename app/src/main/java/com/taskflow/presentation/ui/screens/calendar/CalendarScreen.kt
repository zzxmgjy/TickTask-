package com.taskflow.presentation.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.gridrememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taskflow.domain.model.Task
import com.taskflow.presentation.ui.theme.TaskColors
import com.taskflow.presentation.viewmodel.CalendarViewModel
import com.taskflow.presentation.viewmodel.CalendarViewMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * 日历页面 - 支持日/周/月视图
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onNavigateToTaskDetail: (String) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val tasksForSelectedDate by viewModel.tasksForSelectedDate.collectAsState()
    val tasksForCurrentMonth by viewModel.tasksForCurrentMonth.collectAsState()
    
    var showDatePicker by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy年M月")),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.goToToday() }) {
                        Icon(Icons.Outlined.Today, contentDescription = "今天")
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Outlined.DateRange, contentDescription = "选择日期")
                    }
                    Box {
                        IconButton(onClick = { /* TODO: 视图切换菜单 */ }) {
                            Icon(Icons.Outlined.ViewModule, contentDescription = "视图")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 视图切换器
            ViewModeSelector(
                currentMode = viewMode,
                onModeSelected = { viewModel.setViewMode(it) }
            )
            
            when (viewMode) {
                CalendarViewMode.MONTH -> {
                    MonthCalendarView(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        tasksForCurrentMonth = tasksForCurrentMonth,
                        onDateSelected = { viewModel.selectDate(it) },
                        onPreviousMonth = { viewModel.goToPreviousMonth() },
                        onNextMonth = { viewModel.goToNextMonth() }
                    )
                }
                CalendarViewMode.WEEK -> {
                    WeekCalendarView(
                        selectedDate = selectedDate,
                        tasksForCurrentMonth = tasksForCurrentMonth,
                        onDateSelected = { viewModel.selectDate(it) }
                    )
                }
                CalendarViewMode.DAY -> {
                    DayDetailView(
                        date = selectedDate,
                        tasks = tasksForSelectedDate,
                        onTaskClick = { onNavigateToTaskDetail(it.id) }
                    )
                }
                CalendarViewMode.YEAR -> {
                    YearCalendarView(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        onMonthSelected = { 
                            viewModel.selectDate(it.atDay(1))
                            viewModel.setViewMode(CalendarViewMode.MONTH)
                        }
                    )
                }
            }
        }
    }
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
            ))
        }
    }
}

@Composable
fun ViewModeSelector(
    currentMode: CalendarViewMode,
    onModeSelected: (CalendarViewMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CalendarViewMode.entries.forEach { mode ->
            FilterChip(
                selected = currentMode == mode,
                onClick = { onModeSelected(mode) },
                label = {
                    Text(
                        text = when (mode) {
                            CalendarViewMode.DAY -> "日"
                            CalendarViewMode.WEEK -> "周"
                            CalendarViewMode.MONTH -> "月"
                            CalendarViewMode.YEAR -> "年"
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = when (mode) {
                            CalendarViewMode.DAY -> Icons.Outlined.ViewDay
                            CalendarViewMode.WEEK -> Icons.Outlined.ViewWeek
                            CalendarViewMode.MONTH -> Icons.Outlined.CalendarMonth
                            CalendarViewMode.YEAR -> Icons.Outlined.CalendarViewDay
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun MonthCalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    tasksForCurrentMonth: Map<LocalDate, Int>,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 月份导航
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "上个月")
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy年M月")),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onNextMonth) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "下个月")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 星期标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.entries.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 日历网格
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday
        val daysInMonth = currentMonth.lengthOfMonth()
        
        var dayCounter = 1
        val rows = (firstDayOfWeek + daysInMonth + 6) / 7
        
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..6) {
                    val dayIndex = row * 7 + col - firstDayOfWeek + 1
                    if (dayIndex in 1..daysInMonth) {
                        val date = currentMonth.atDay(dayIndex)
                        val taskCount = tasksForCurrentMonth[date] ?: 0
                        
                        DayCell(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == LocalDate.now(),
                            taskCount = taskCount,
                            onClick = { onDateSelected(date) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    taskCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
        
        if (taskCount > 0 && !isSelected) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                repeat(minOf(taskCount, 3)) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (isToday) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary
                            )
                    )
                    if (it < minOf(taskCount, 3) - 1) {
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WeekCalendarView(
    selectedDate: LocalDate,
    tasksForCurrentMonth: Map<LocalDate, Int>,
    onDateSelected: (LocalDate) -> Unit
) {
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() % 7)
    val daysOfWeek = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { date ->
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .clickable { onDateSelected(date) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isToday) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                               else MaterialTheme.colorScheme.onSurface
                    )
                    if (tasksForCurrentMonth[date] ?: 0 > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 选中日期的任务列表
        val tasksForDate = tasksForCurrentMonth.filterKeys { it == selectedDate }
        
        if (tasksForDate.isNotEmpty()) {
            Text(
                text = "${selectedDate.dayOfMonth}日 有 ${tasksForDate.values.sum()} 个任务",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DayDetailView(
    date: LocalDate,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE, M月d日")),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (tasks.isEmpty()) "今天没有待办任务" else "共 ${tasks.size} 个任务",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (tasks.isEmpty()) {
            item {
                EmptyDayState()
            }
        } else {
            items(tasks, key = { it.id }) { task ->
                TaskCalendarItem(
                    task = task,
                    onClick = { onTaskClick(task) }
                )
            }
        }
    }
}

@Composable
fun TaskCalendarItem(
    task: Task,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (task.priority) {
                    com.taskflow.domain.model.Priority.HIGH -> Icons.Filled.Flag
                    com.taskflow.domain.model.Priority.MEDIUM -> Icons.Outlined.Flag
                    else -> Icons.Outlined.Flag
                },
                contentDescription = null,
                tint = when (task.priority) {
                    com.taskflow.domain.model.Priority.HIGH -> TaskColors.HighPriority
                    com.taskflow.domain.model.Priority.MEDIUM -> TaskColors.MediumPriority
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                task.dueDate?.let {
                    Text(
                        text = it.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (task.hasReminder) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "有提醒",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyDayState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Celebration,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "轻松的一天！",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun YearCalendarView(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onMonthSelected: (YearMonth) -> Unit
) {
    val months = (0..11).map { currentMonth.plusMonths(it.toLong()) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = currentMonth.year.toString(),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(months.chunked(3)) { rowMonths ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowMonths.forEach { month ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onMonthSelected(month) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (month == currentMonth) 
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = month.lengthOfMonth().toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
