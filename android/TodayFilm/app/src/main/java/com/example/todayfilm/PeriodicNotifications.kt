package com.example.todayfilm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

fun everySixHour(): Long {
    val currentDate = Calendar.getInstance( )

    // 초기값 06시
    val dueDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 6)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
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

    return dueDate.timeInMillis
}

fun everyMidnight(): Long {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    // 현재 시간이 0시 이후면 다음날로 세팅
    if (dueDate.before(currentDate)) {
        dueDate.add(Calendar.HOUR_OF_DAY, 24)
    }

    return dueDate.timeInMillis
}

const val EMPTY_BROADCAST_CODE = 0

@RequiresApi(Build.VERSION_CODES.M)
fun setEmptyNotification(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent = Intent(context, EmptyNotificationBroadcastReceiver::class.java).let {
        PendingIntent.getBroadcast(context, EMPTY_BROADCAST_CODE, it, FLAG_IMMUTABLE)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            everySixHour(),
            alarmIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            everySixHour(),
            alarmIntent
        )
    }
}

const val RESET_BROADCAST_CODE = 2

@RequiresApi(Build.VERSION_CODES.M)
fun setResetNotification(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent = Intent(context, ResetNotificationBroadcastReceiver::class.java).let {
        PendingIntent.getBroadcast(context, RESET_BROADCAST_CODE, it, FLAG_IMMUTABLE)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            everyMidnight(),
            alarmIntent
        )
    } else {
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            everyMidnight(),
            alarmIntent
        )
    }
}