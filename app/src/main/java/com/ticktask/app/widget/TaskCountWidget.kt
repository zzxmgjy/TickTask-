package com.ticktask.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.ticktask.app.MainActivity
import com.ticktask.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class TaskCountWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val taskCount = withContext(Dispatchers.IO) {
            // Calculate today's task count
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = calendar.timeInMillis

            // Get task count from database
            val database = com.ticktask.app.data.database.AppDatabase.getInstance(context)
            val count = database.taskDao().getActiveTaskCountForDay(startOfDay, endOfDay)
            count
        }

        provideContent {
            GlanceTheme {
                Box(
                    modifier = androidx.glance.layout.fillMaxSize()
                        .background(GlanceTheme.colors.primaryContainer)
                        .padding(16.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = context.getString(R.string.widget_title),
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        androidx.glance.layout.Box(
                            modifier = androidx.glance.layout.size(width = androidx.compose.ui.unit.dp.Zero, height = 12.dp)
                        )

                        Text(
                            text = if (taskCount == 0) {
                                context.getString(R.string.no_tasks_today)
                            } else {
                                context.getString(R.string.remaining_tasks, taskCount)
                            },
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

class TaskCountWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskCountWidget()
}
