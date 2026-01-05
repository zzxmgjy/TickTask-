# 滴答任务 (TickTask)

一个类似滴答清单的 Android 任务管理应用 MVP。

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose + Material Design 3
- **架构**: MVVM (Model-View-ViewModel)
- **数据库**: Room (SQLite)
- **小组件**: Glance AppWidget
- **最低支持**: Android 8.0 (API 26)
- **目标 SDK**: Android 14 (API 34)

## 功能特性

### 已实现功能 (MVP)

1. **任务管理**
   - 创建新任务
   - 编辑现有任务
   - 删除任务
   - 标记任务完成/未完成

2. **任务属性**
   - 任务标题
   - 任务描述
   - 优先级 (无/低/中/高)
   - 截止日期
   - 创建/更新时间戳

3. **视图筛选**
   - 所有任务
   - 今日任务
   - 已完成任务

4. **小组件**
   - 今日任务计数小组件
   - 显示剩余未完成任务数量
   - 点击打开应用

5. **UI/UX**
   - 中文界面
   - Material Design 3 设计语言
   - 深色/浅色主题自动切换
   - 流畅的动画效果

## 项目结构

```
app/src/main/java/com/ticktask/app/
├── MainActivity.kt                    # 应用入口
├── data/
│   ├── entity/
│   │   └── Task.kt                   # 任务数据实体
│   ├── dao/
│   │   └── TaskDao.kt                # 数据访问对象
│   ├── database/
│   │   └── AppDatabase.kt            # Room 数据库
│   └── repository/
│       └── TaskRepository.kt          # 仓储层
├── ui/
│   ├── viewmodel/
│   │   └── TaskViewModel.kt          # 任务视图模型
│   └── screen/
│       ├── TaskListScreen.kt         # 任务列表主界面
│       ├── TaskEditDialog.kt          # 任务编辑对话框
│       └── DeleteConfirmDialog.kt    # 删除确认对话框
└── widget/
    └── TaskCountWidget.kt            # 任务计数小组件
```

## 架构说明

### MVVM 架构

```
┌─────────────────┐
│   UI Layer      │
│  (Compose)      │
└────────┬────────┘
         │ observes
┌────────▼────────┐
│  ViewModel      │
└────────┬────────┘
         │ uses
┌────────▼────────┐
│  Repository     │
└────────┬────────┘
         │ uses
┌────────▼────────┐
│  DAO (Room)     │
└────────┬────────┘
         │
┌────────▼────────┐
│  SQLite DB      │
└─────────────────┘
```

### 数据流向

1. **读取数据**: Database → DAO → Repository → ViewModel → LiveData → UI
2. **写入数据**: UI → ViewModel → Repository → DAO → Database

## 构建和运行

### 前置要求

- Android Studio Hedgehog 或更高版本
- JDK 17 或更高版本
- Android SDK 34

### 构建步骤

1. 克隆项目:
   ```bash
   git clone <repository-url>
   cd TickTask
   ```

2. 在 Android Studio 中打开项目

3. 等待 Gradle 同步完成

4. 运行应用:
   - 连接 Android 设备或启动模拟器
   - 点击 Run 按钮 (或 Shift+F10)

## 数据库架构

### Task 表结构

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 主键 (自增) |
| title | String | 任务标题 |
| description | String | 任务描述 |
| isCompleted | Boolean | 是否完成 |
| priority | Int | 优先级 (0:无, 1:低, 2:中, 3:高) |
| dueDate | Long? | 截止日期 (时间戳) |
| createdAt | Long | 创建时间 |
| updatedAt | Long | 更新时间 |

## 未来规划

### 第二阶段 (功能扩展)
- [ ] 任务分类/清单管理
- [ ] 标签系统
- [ ] 提醒和通知
- [ ] 更多小组件类型 (列表预览、快速添加、四象限)

### 第三阶段 (高级功能)
- [ ] 重复任务/周期性任务
- [ ] 云端同步
- [ ] 主题自定义
- [ ] 搜索功能
- [ ] 数据导入/导出
- [ ] 统计报表

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License
