package com.ticktask.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ticktask.app.R
import com.ticktask.app.data.entity.Task
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by rememberSaveable { mutableStateOf(task?.title ?: "") }
    var description by rememberSaveable { mutableStateOf(task?.description ?: "") }
    var selectedPriority by rememberSaveable { mutableIntStateOf(task?.priority ?: 0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableLongStateOf(task?.dueDate ?: 0L) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (selectedDate > 0) selectedDate else System.currentTimeMillis()
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (task != null) stringResource(R.string.edit_task) else stringResource(R.string.new_task)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.task_description_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(stringResource(R.string.priority))

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    PriorityButton(
                        text = stringResource(R.string.high),
                        selected = selectedPriority == 3,
                        onClick = { selectedPriority = 3 },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    PriorityButton(
                        text = stringResource(R.string.medium),
                        selected = selectedPriority == 2,
                        onClick = { selectedPriority = 2 },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    PriorityButton(
                        text = stringResource(R.string.low),
                        selected = selectedPriority == 1,
                        onClick = { selectedPriority = 1 },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    PriorityButton(
                        text = stringResource(R.string.none),
                        selected = selectedPriority == 0,
                        onClick = { selectedPriority = 0 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = if (selectedDate > 0) formatDate(selectedDate) else "",
                    onValueChange = { },
                    label = { Text(stringResource(R.string.due_date)) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.DateRange,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newTask = task?.copy(
                        title = title,
                        description = description,
                        priority = selectedPriority,
                        dueDate = if (selectedDate > 0) selectedDate else null
                    ) ?: Task(
                        title = title,
                        description = description,
                        priority = selectedPriority,
                        dueDate = if (selectedDate > 0) selectedDate else null
                    )
                    onSave(newTask)
                },
                enabled = title.trim().isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = if (onDelete != null) Modifier.weight(1f) else Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun PriorityButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = if (selected) {
            androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        } else {
            androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
        },
        modifier = modifier.height(36.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall)
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
        else -> java.text.SimpleDateFormat("yyyy年MM月dd日", java.util.Locale.getDefault())
            .format(java.util.Date(timestamp))
    }
}
