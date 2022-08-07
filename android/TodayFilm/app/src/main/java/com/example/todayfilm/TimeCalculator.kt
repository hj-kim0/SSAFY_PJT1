package com.example.todayfilm

import java.util.*

fun EverySixHour(): Long {
    val currentDate = Calendar.getInstance( )

    // 초기값 06시
    val dueDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 6)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 30)
    }

    // 현재 시간이 06시 이후면 12시로 세팅
    if (dueDate.before(currentDate)) {
        dueDate.add(Calendar.HOUR_OF_DAY, 6)
    }

    // 현재 시간이 12시 이후면 18시로 세팅
    if (dueDate.before(currentDate)) {
        dueDate.add(Calendar.HOUR_OF_DAY, 6)
    }

    // 현재 시간이 18시 이후면 다음날 06시로 세팅
    if (dueDate.before(currentDate)) {
        dueDate.add(Calendar.HOUR_OF_DAY, 12)
    }

    return dueDate.timeInMillis - currentDate.timeInMillis
}

fun EveryMidnight(): Long {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 30)
    }

    // 현재 시간이 0시 이후면 다음날로 세팅
    if (dueDate.before(currentDate)) {
        dueDate.add(Calendar.HOUR_OF_DAY, 24)
    }

    return dueDate.timeInMillis - currentDate.timeInMillis
}

fun Test(): Long {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance().apply {
        add(Calendar.SECOND, 30)
    }

    return dueDate.timeInMillis - currentDate.timeInMillis
}