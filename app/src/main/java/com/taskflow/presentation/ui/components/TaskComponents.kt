package com.taskflow.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.taskflow.domain.model.Priority
import com.taskflow.domain.model.Task
import com.taskflow.presentation.ui.theme.TaskColors
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 通用任务卡片组件
 */
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 复选框
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = TaskColors.Completed
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 任务内容
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted) {
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    } else null,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // 任务元信息
                TaskMetaInfo(task = task)
            }
            
            // 优先级和提醒图标
            TaskActionIcons(task = task, onPriorityChange = onPriorityChange)
        }
    }
}

/**
 * 任务元信息行
 */
@Composable
fun TaskMetaInfo(task: Task) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 截止日期
        task.dueDate?.let { dueDate ->
            TaskChip(
                text = formatDueDate(dueDate),
                color = getDueDateColor(dueDate, task.isCompleted)
            )
        }
        
        // 子任务数量
        if (task.subtasks.isNotEmpty()) {
            TaskChip(
                text = "${task.subtasks.count { it.isCompleted }}/${task.subtasks.size}",
                icon = Icons.Outlined.CheckCircle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 任务操作图标（优先级、提醒等）
 */
@Composable
fun TaskActionIcons(
    task: Task,
    onPriorityChange: (Priority) -> Unit
) {
    var showPriorityMenu by remember { mutableStateOf(false) }
    
    Column(
        horizontalAlignment = Alignment.End
    ) {
        // 优先级选择器
        Box {
            IconButton(
                onClick = { showPriorityMenu = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = when (task.priority) {
                        Priority.HIGH -> Icons.Filled.Flag
                        Priority.MEDIUM -> Icons.Outlined.Flag
                        Priority.LOW -> Icons.Outlined.IosShare
                        else -> Icons.Outlined.Flag
                    },
                    contentDescription = "优先级",
                    tint = when (task.priority) {
                        Priority.HIGH -> TaskColors.HighPriority
                        Priority.MEDIUM -> TaskColors.MediumPriority
                        Priority.LOW -> TaskColors.LowPriority
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
            
            DropdownMenu(
                expanded = showPriorityMenu,
                onDismissRequest = { showPriorityMenu = false }
            ) {
                Priority.entries.forEach { priority ->
                    DropdownMenuItem(
                        text = { Text(priority.name) },
                        onClick = {
                            onPriorityChange(priority)
                            showPriorityMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Flag,
                                contentDescription = null,
                                tint = when (priority) {
                                    Priority.HIGH -> TaskColors.HighPriority
                                    Priority.MEDIUM -> TaskColors.MediumPriority
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    )
                }
            }
        }
        
        // 提醒图标
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

/**
 * 任务标签Chip
 */
@Composable
fun TaskChip(
    text: String,
    icon: ImageVector? = null,
    color: Color
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

/**
 * 优先级选择器组件
 */
@Composable
fun PrioritySelector(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Priority.entries.forEach { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onPrioritySelected(priority) },
                label = { Text(priority.name) },
                leadingIcon = {
                    Icon(
                        imageVector = when (priority) {
                            Priority.HIGH -> Icons.Filled.Flag
                            Priority.MEDIUM -> Icons.Outlined.Flag
                            Priority.LOW -> Icons.Outlined.IosShare
                            else -> Icons.Outlined.Flag
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = when (priority) {
                            Priority.HIGH -> TaskColors.HighPriority
                            Priority.MEDIUM -> TaskColors.MediumPriority
                            Priority.LOW -> TaskColors.LowPriority
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 加载状态组件
 */
@Composable
fun LoadingContent(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}

/**
 * 空状态组件
 */
@Composable
fun EmptyContent(
    icon: ImageVector = Icons.Outlined.Inbox,
    title: String = "暂无数据",
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        subtitle?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        action?.let {
            Spacer(modifier = Modifier.height(16.dp))
            it()
        }
    }
}

/**
 * 错误状态组件
 */
@Composable
fun ErrorContent(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        onRetry?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = it) {
                Text("重试")
            }
        }
    }
}

// Helper functions
private fun formatDueDate(dueDate: LocalDateTime): String {
    val now = LocalDateTime.now()
    return when {
        dueDate.toLocalDate() == now.toLocalDate() -> {
            "今天 ${dueDate.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
        dueDate.toLocalDate() == now.toLocalDate().plusDays(1) -> {
            "明天 ${dueDate.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
        dueDate.toLocalDate() == now.toLocalDate().plusDays(2) -> {
            "后天 ${dueDate.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        }
        else -> {
            dueDate.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
        }
    }
}

private fun getDueDateColor(dueDate: LocalDateTime, isCompleted: Boolean): Color {
    return when {
        isCompleted -> TaskColors.Completed
        dueDate.isBefore(LocalDateTime.now()) -> TaskColors.Overdue
        dueDate.toLocalDate() == LocalDateTime.now().toLocalDate() -> TaskColors.Today
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
