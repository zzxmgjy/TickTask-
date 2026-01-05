package com.taskflow.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

/**
 * 自然语言日期解析工具
 * 支持解析如 "今天", "明天", "后天", "下周一", "3天后", "每周一" 等表达式
 */
object NaturalLanguageParser {
    
    private val datePatterns = listOf(
        // 绝对日期
        Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日") to { match: MatchResult ->
            LocalDate.of(
                match.group(1).toInt(),
                match.group(2).toInt(),
                match.group(3).toInt()
            )
        },
        Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})") to { match: MatchResult ->
            LocalDate.of(
                match.group(1).toInt(),
                match.group(2).toInt(),
                match.group(3).toInt()
            )
        },
        Pattern.compile("(\\d{1,2})/(\\d{1,2})") to { match: MatchResult ->
            val now = LocalDate.now()
            LocalDate.of(now.year, match.group(1).toInt(), match.group(2).toInt())
        },
        
        // 相对日期
        Pattern.compile("今天") to { _ -> LocalDate.now() },
        Pattern.compile("明天|明日") to { _ -> LocalDate.now().plusDays(1) },
        Pattern.compile("后天") to { _ -> LocalDate.now().plusDays(2) },
        Pattern.compile("昨天|昨日") to { _ -> LocalDate.now().minusDays(1) },
        Pattern.compile("大后天") to { _ -> LocalDate.now().plusDays(3) },
        
        // 星期相关
        Pattern.compile("下*周([一二三四五六日天])") to { match ->
            val dayNames = mapOf(
                "一" to java.time.DayOfWeek.MONDAY,
                "二" to java.time.DayOfWeek.TUESDAY,
                "三" to java.time.DayOfWeek.WEDNESDAY,
                "四" to java.time.DayOfWeek.THURSDAY,
                "五" to java.time.DayOfWeek.FRIDAY,
                "六" to java.time.DayOfWeek.SATURDAY,
                "日" to java.time.DayOfWeek.SUNDAY,
                "天" to java.time.DayOfWeek.SUNDAY
            )
            val targetDay = dayNames[match.group(1)] ?: return@to LocalDate.now()
            val today = LocalDate.now()
            var daysUntil = targetDay.value - today.dayOfWeek.value
            if (daysUntil < 0) daysUntil += 7
            if (match.group(0).startsWith("下")) daysUntil += 7
            today.plusDays(daysUntil.toLong())
        },
        
        // X天后/周后/月后
        Pattern.compile("(\\d+)天后") to { match ->
            LocalDate.now().plusDays(match.group(1).toLong())
        },
        Pattern.compile("(\\d+)周后") to { match ->
            LocalDate.now().plusWeeks(match.group(1).toLong())
        },
        Pattern.compile("(\\d+)月后") to { match ->
            LocalDate.now().plusMonths(match.group(1).toLong())
        },
        
        // 下周/下周X
        Pattern.compile("下周") to { _ ->
            LocalDate.now().plusWeeks(1)
        },
        
        // 本周/本周X
        Pattern.compile("本周") to { _ ->
            LocalDate.now()
        }
    )
    
    private val timePatterns = listOf(
        Pattern.compile("(\\d{1,2}):(\\d{2})") to { match ->
            LocalTime.of(match.group(1).toInt(), match.group(2).toInt())
        },
        Pattern.compile("(\\d{1,2})点(\\d{1,2})分") to { match ->
            LocalTime.of(match.group(1).toInt(), match.group(2).toInt())
        },
        Pattern.compile("(\\d{1,2})点") to { match ->
            LocalTime.of(match.group(1).toInt(), 0)
        },
        Pattern.compile("凌晨(\\d{1,2})点") to { match ->
            LocalTime.of(match.group(1).toInt(), 0)
        },
        Pattern.compile("早上(\\d{1,2})点") to { match ->
            LocalTime.of(match.group(1).toInt(), 0)
        },
        Pattern.compile("下午(\\d{1,2})点") to { match ->
            val hour = match.group(1).toInt()
            LocalTime.of(if (hour < 12) hour + 12 else hour, 0)
        },
        Pattern.compile("晚上(\\d{1,2})点") to { match ->
            val hour = match.group(1).toInt()
            LocalTime.of(if (hour < 12) hour + 12 else hour, 0)
        }
    )
    
    /**
     * 解析自然语言输入，返回日期时间
     */
    fun parseDateTime(input: String): ParseResult {
        val trimmedInput = input.trim()
        
        // 分离日期和时间部分
        val datePart = extractDate(trimmedInput)
        val timePart = extractTime(trimmedInput)
        
        return ParseResult(
            date = datePart,
            time = timePart,
            dateTime = if (datePart != null && timePart != null) {
                LocalDateTime.of(datePart, timePart)
            } else if (datePart != null) {
                LocalDateTime.of(datePart, LocalTime.of(9, 0)) // 默认早上9点
            } else {
                null
            }
        )
    }
    
    /**
     * 提取日期
     */
    fun extractDate(input: String): LocalDate? {
        for ((pattern, parser) in datePatterns) {
            val matcher = pattern.matcher(input)
            if (matcher.find()) {
                return try {
                    parser(matcher.toMatchResult())
                } catch (e: Exception) {
                    null
                }
            }
        }
        return null
    }
    
    /**
     * 提取时间
     */
    fun extractTime(input: String): LocalTime? {
        for ((pattern, parser) in timePatterns) {
            val matcher = pattern.matcher(input)
            if (matcher.find()) {
                return try {
                    parser(matcher.toMatchResult())
                } catch (e: Exception) {
                    null
                }
            }
        }
        return null
    }
    
    /**
     * 检查输入是否包含日期
     */
    fun hasDate(input: String): Boolean {
        return extractDate(input) != null
    }
    
    /**
     * 检查输入是否包含时间
     */
    fun hasTime(input: String): Boolean {
        return extractTime(input) != null
    }
    
    /**
     * 从输入中移除日期时间部分，返回纯文本
     */
    fun removeDateTime(input: String): String {
        var result = input
        // 移除日期部分
        for ((pattern, _) in datePatterns) {
            result = pattern.matcher(result).replaceAll("").trim()
        }
        // 移除时间部分
        for ((pattern, _) in timePatterns) {
            result = pattern.matcher(result).replaceAll("").trim()
        }
        return result.trim()
    }
    
    data class ParseResult(
        val date: LocalDate?,
        val time: LocalTime?,
        val dateTime: LocalDateTime?
    )
}

/**
 * 重复规则解析工具
 */
object RepeatRuleParser {
    
    private val repeatPatterns = listOf(
        Pattern.compile("每天|每日") to RepeatType.DAILY,
        Pattern.compile("每周") to RepeatType.WEEKLY,
        Pattern.compile("每两周|双周") to RepeatType.BIWEEKLY,
        Pattern.compile("每月") to RepeatType.MONTHLY,
        Pattern.compile("每年") to RepeatType.YEARLY,
        Pattern.compile("工作日") to RepeatType.WORKDAYS,
        Pattern.compile("周末") to RepeatType.WEEKENDS
    )
    
    fun parseRepeatRule(input: String): ParseRepeatResult? {
        for ((pattern, repeatType) in repeatPatterns) {
            if (pattern.matcher(input).find()) {
                return ParseRepeatResult(
                    type = repeatType,
                    interval = extractInterval(input)
                )
            }
        }
        return null
    }
    
    private fun extractInterval(input: String): Int {
        val intervalPattern = Pattern.compile("每(\\d+)[天周月年]")
        val matcher = intervalPattern.matcher(input)
        return if (matcher.find()) {
            matcher.group(1).toInt()
        } else {
            1
        }
    }
    
    data class ParseRepeatResult(
        val type: RepeatType,
        val interval: Int
    )
    
    enum class RepeatType {
        DAILY,
        WEEKLY,
        BIWEEKLY,
        MONTHLY,
        YEARLY,
        WORKDAYS,
        WEEKENDS,
        CUSTOM
    }
}

/**
 * 日期时间格式化工具
 */
object DateTimeUtils {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm")
    private val weekFormatter = DateTimeFormatter.ofPattern("EEEE")
    
    fun formatDate(date: LocalDateTime): String {
        return date.format(dateFormatter)
    }
    
    fun formatTime(date: LocalDateTime): String {
        return date.format(timeFormatter)
    }
    
    fun formatDateTime(date: LocalDateTime): String {
        return date.format(dateTimeFormatter)
    }
    
    fun formatRelative(date: LocalDateTime): String {
        val today = LocalDate.now()
        val targetDate = date.toLocalDate()
        
        return when {
            targetDate == today -> "今天"
            targetDate == today.plusDays(1) -> "明天"
            targetDate == today.plusDays(2) -> "后天"
            targetDate == today.minusDays(1) -> "昨天"
            targetDate == today.minusDays(2) -> "前天"
            else -> date.format(dateFormatter)
        }
    }
    
    fun formatWeekday(date: LocalDateTime): String {
        return date.format(weekFormatter)
    }
    
    fun isToday(date: LocalDateTime): Boolean {
        return date.toLocalDate() == LocalDate.now()
    }
    
    fun isTomorrow(date: LocalDateTime): Boolean {
        return date.toLocalDate() == LocalDate.now().plusDays(1)
    }
    
    fun isOverdue(date: LocalDateTime): Boolean {
        return date.toLocalDate().isBefore(LocalDate.now())
    }
}
