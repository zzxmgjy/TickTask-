package com.taskflow.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskflow.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 开机接收器 - 设备启动后重新设置任务提醒
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            rescheduleAllReminders(context)
        }
    }
    
    private fun rescheduleAllReminders(context: Context) {
        // TODO: 从数据库获取所有有提醒的任务，重新设置提醒
        // val tasks = taskRepository.getTasksWithReminders()
        // tasks.forEach { task ->
        //     scheduleReminder(context, task)
        // }
    }
}
