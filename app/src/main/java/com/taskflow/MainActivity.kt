package com.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taskflow.presentation.ui.screens.calendar.CalendarScreen
import com.taskflow.presentation.ui.screens.home.HomeScreen
import com.taskflow.presentation.ui.screens.settings.SettingsScreen
import com.taskflow.presentation.ui.screens.task.TaskDetailScreen
import com.taskflow.presentation.ui.theme.TaskFlowTheme
import com.taskflow.receiver.ReminderReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaskFlowTheme {
                val navController = rememberNavController()
                val taskIdFromNotification = intent.getStringExtra(ReminderReceiver.EXTRA_TASK_ID)

                LaunchedEffect(taskIdFromNotification) {
                    if (!taskIdFromNotification.isNullOrBlank()) {
                        navController.navigate("taskDetail/$taskIdFromNotification")
                    }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val bottomItems = listOf(
                    BottomNavItem("home", "任务", Icons.Outlined.CheckCircle),
                    BottomNavItem("calendar", "日历", Icons.Outlined.CalendarMonth),
                    BottomNavItem("settings", "设置", Icons.Outlined.Settings)
                )

                Scaffold(
                    bottomBar = {
                        val showBottomBar = currentDestination?.route in bottomItems.map { it.route }
                        if (showBottomBar) {
                            NavigationBar {
                                bottomItems.forEach { item ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                onTaskClick = { navController.navigate("taskDetail/${it}") }
                            )
                        }
                        composable("calendar") {
                            CalendarScreen(
                                onNavigateToTaskDetail = { navController.navigate("taskDetail/${it}") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen()
                        }
                        composable("taskDetail/{taskId}") { entry ->
                            val taskId = entry.arguments?.getString("taskId") ?: return@composable
                            TaskDetailScreen(
                                taskId = taskId,
                                onNavigateUp = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
