package com.taskflow.presentation.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * 设置页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var offlineModeEnabled by remember { mutableStateOf(true) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("系统默认") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // 外观设置
            item {
                SettingsSectionHeader(title = "外观")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "深色模式",
                    subtitle = "使用深色主题",
                    trailing = {
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it }
                        )
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Palette,
                    title = "主题颜色",
                    subtitle = selectedTheme,
                    onClick = { showThemeDialog = true }
                )
            }
            
            // 通知设置
            item {
                SettingsSectionHeader(title = "通知")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Notifications,
                    title = "应用通知",
                    subtitle = "接收任务提醒",
                    trailing = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                    }
                )
            }
            
            // 同步设置
            item {
                SettingsSectionHeader(title = "数据与同步")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.OfflinePin,
                    title = "离线模式",
                    subtitle = "优先本地存储",
                    trailing = {
                        Switch(
                            checked = offlineModeEnabled,
                            onCheckedChange = { offlineModeEnabled = it }
                        )
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.CloudSync,
                    title = "云同步",
                    subtitle = "上次同步: 刚刚",
                    onClick = { /* 手动同步 */ }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Storage,
                    title = "存储空间",
                    subtitle = "已使用 128MB",
                    onClick = { /* 清理存储 */ }
                )
            }
            
            // 安全设置
            item {
                SettingsSectionHeader(title = "安全")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Lock,
                    title = "应用锁",
                    subtitle = "使用指纹或密码解锁",
                    onClick = { /* 设置应用锁 */ }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "隐私保护",
                    subtitle = "管理隐私设置",
                    onClick = { /* 隐私设置 */ }
                )
            }
            
            // 账户设置
            item {
                SettingsSectionHeader(title = "账户")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Person,
                    title = "账户信息",
                    subtitle = "user@example.com",
                    onClick = { /* 账户管理 */ }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Logout,
                    title = "退出登录",
                    subtitle = null,
                    onClick = { /* 退出登录 */ },
                    iconTint = MaterialTheme.colorScheme.error
                )
            }
            
            // 关于
            item {
                SettingsSectionHeader(title = "关于")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = "版本",
                    subtitle = "TaskFlow v1.0.0",
                    onClick = { /* 版本信息 */ }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = "使用条款",
                    subtitle = null,
                    onClick = { /* 条款 */ }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Policy,
                    title = "隐私政策",
                    subtitle = null,
                    onClick = { /* 政策 */ }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Outlined.Feedback,
                    title = "反馈与建议",
                    subtitle = null,
                    onClick = { /* 反馈 */ }
                )
            }
        }
    }
    
    // 主题选择对话框
    if (showThemeDialog) {
        ThemeSelectionDialog(
            selectedTheme = selectedTheme,
            onThemeSelected = {
                selectedTheme = it
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf("系统默认", "浅色", "深色", "绿色", "蓝色", "紫色")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择主题") },
        text = {
            Column {
                themes.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = theme == selectedTheme,
                            onClick = { onThemeSelected(theme) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(theme)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
