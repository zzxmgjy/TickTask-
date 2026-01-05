package com.taskflow.presentation.ui.screens.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 习惯追踪页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(
    onNavigateToHabitDetail: (String) -> Unit
) {
    // 示例数据
    val habits = remember {
        listOf(
            Habit(
                id = "1",
                name = "早起",
                icon = "☀️",
                color = 0xFFFFB300,
                streak = 7,
                totalDays = 30,
                completedToday = true,
                history = listOf(true, true, true, true, true, true, true)
            ),
            Habit(
                id = "2",
                name = "阅读",
                icon = "📖",
                color = 0xFF2196F3,
                streak = 3,
                totalDays = 15,
                completedToday = false,
                history = listOf(true, true, true, false, false, true, true)
            ),
            Habit(
                id = "3",
                name = "运动",
                icon = "🏃",
                color = 0xFF4CAF50,
                streak = 0,
                totalDays = 8,
                completedToday = false,
                history = listOf(false, false, true, true, false, false, false)
            )
        )
    }
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("今日", "习惯", "统计")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("习惯养成") },
                actions = {
                    IconButton(onClick = { /* 添加习惯 */ }) {
                        Icon(Icons.Filled.Add, contentDescription = "添加习惯")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* 添加新习惯 */ }) {
                Icon(Icons.Filled.Add, contentDescription = "添加习惯")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab切换
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> TodayHabitsTab(habits = habits)
                1 -> AllHabitsTab(habits = habits, onNavigateToHabitDetail = onNavigateToHabitDetail)
                2 -> HabitStatsTab(habits = habits)
            }
        }
    }
}

@Composable
fun TodayHabitsTab(habits: List<Habit>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "今日习惯",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("M月d日 EEEE")),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(habits) { habit ->
            HabitCheckCard(
                habit = habit,
                onCheckedChange = { /* 切换完成状态 */ }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp)) // FAB space
        }
    }
}

@Composable
fun AllHabitsTab(
    habits: List<Habit>,
    onNavigateToHabitDetail: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "我的习惯",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        items(habits) { habit ->
            HabitCard(
                habit = habit,
                onClick = { onNavigateToHabitDetail(habit.id) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HabitStatsTab(habits: List<Habit>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "习惯统计",
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        // 本周概览
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "本周概览",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            value = habits.count { it.completedToday }.toString(),
                            label = "今日完成"
                        )
                        StatItem(
                            value = habits.sumOf { it.streak }.toString(),
                            label = "连续天数"
                        )
                        StatItem(
                            value = habits.sumOf { it.totalDays }.toString(),
                            label = "总天数"
                        )
                    }
                }
            }
        }
        
        // 习惯完成率
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "完成率",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    habits.forEach { habit ->
                        HabitProgressBar(
                            name = habit.name,
                            progress = habit.totalDays.toFloat() / 30f,
                            percentage = (habit.totalDays * 100 / 30)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HabitCheckCard(
    habit: Habit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 习惯图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(habit.color).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.icon,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "连续 ${habit.streak} 天",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 打卡按钮
            IconButton(
                onClick = { onCheckedChange(!habit.completedToday) },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (habit.completedToday) Color(habit.color)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = if (habit.completedToday) Icons.Filled.Check 
                                 else Icons.Outlined.Add,
                    contentDescription = if (habit.completedToday) "已打卡" else "打卡",
                    tint = if (habit.completedToday) Color.White
                          else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: Habit,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(habit.color).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = habit.icon,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // 连续打卡天数
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${habit.streak} 天",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 最近7天历史
                WeekHistoryIndicator(history = habit.history)
            }
            
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeekHistoryIndicator(history: List<Boolean>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        history.takeLast(7).forEach { completed ->
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (completed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HabitProgressBar(
    name: String,
    progress: Float,
    percentage: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

data class Habit(
    val id: String,
    val name: String,
    val icon: String,
    val color: Long,
    val streak: Int,
    val totalDays: Int,
    val completedToday: Boolean,
    val history: List<Boolean>
)
