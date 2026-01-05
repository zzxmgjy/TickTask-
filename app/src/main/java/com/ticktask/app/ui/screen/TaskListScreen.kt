package com.ticktask.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ticktask.app.R
import com.ticktask.app.data.entity.Task
import com.ticktask.app.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TabIndex { ALL, TODAY, COMPLETED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    var selectedTab by remember { mutableIntStateOf(TabIndex.ALL.ordinal) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == TabIndex.ALL.ordinal,
                    onClick = { selectedTab = TabIndex.ALL.ordinal },
                    text = { Text(stringResource(R.string.all_tasks)) }
                )
                Tab(
                    selected = selectedTab == TabIndex.TODAY.ordinal,
                    onClick = { selectedTab = TabIndex.TODAY.ordinal },
                    text = { Text(stringResource(R.string.today)) }
                )
                Tab(
                    selected = selectedTab == TabIndex.COMPLETED.ordinal,
                    onClick = { selectedTab = TabIndex.COMPLETED.ordinal },
                    text = { Text(stringResource(R.string.completed)) }
                )
            }

            val tasks = when (TabIndex.entries[selectedTab]) {
                TabIndex.ALL -> viewModel.allTasks.collectAsState().value
                TabIndex.TODAY -> viewModel.todayTasks.collectAsState().value
                TabIndex.COMPLETED -> viewModel.completedTasks.collectAsState().value
            }

            if (tasks.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_tasks),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onComplete = { viewModel.toggleTaskComplete(task) },
                            onClick = { taskToEdit = task },
                            onDelete = {
                                taskToDelete = task
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        TaskEditDialog(
            task = null,
            onDismiss = { showAddTaskDialog = false },
            onSave = { newTask ->
                viewModel.insert(newTask)
                showAddTaskDialog = false
            }
        )
    }

    taskToEdit?.let { task ->
        TaskEditDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onSave = { updatedTask ->
                viewModel.update(updatedTask)
                taskToEdit = null
            },
            onDelete = {
                taskToDelete = task
                taskToEdit = null
                showDeleteDialog = true
            }
        )
    }

    if (showDeleteDialog && taskToDelete != null) {
        DeleteConfirmDialog(
            task = taskToDelete!!,
            onDismiss = {
                showDeleteDialog = false
                taskToDelete = null
            },
            onConfirm = {
                viewModel.delete(taskToDelete!!)
                showDeleteDialog = false
                taskToDelete = null
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onComplete: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        3 -> Color.Red
        2 -> Color(0xFFFFA500)
        1 -> Color(0xFF90EE90)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onComplete) {
                    Icon(
                        if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )

                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (task.dueDate != null) {
                        val dateStr = formatDate(task.dueDate!!)
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(priorityColor, CircleShape)
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val taskDate = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return when {
        calendar.timeInMillis == taskDate.timeInMillis -> "今天"
        calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.timeInMillis == taskDate.timeInMillis -> "明天"
        else -> SimpleDateFormat("MM月dd日", Locale.getDefault()).format(Date(timestamp))
    }
}
