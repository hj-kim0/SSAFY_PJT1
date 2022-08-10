package com.example.todayfilm

import android.app.*
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class InitApplication: Application() {
    private lateinit var mContext: Context
    var isNotificationChannelCreated = false

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext

        // 기본 TimeZone과 Locale 지정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
        Locale.setDefault(Locale.KOREA)

        // 푸시 알림 채널 생성
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationChannel = NotificationChannel("10001", mContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)

                notificationChannel.description = "푸시 알림"
                notificationChannel.enableLights(true) // 불빛 알림 허용
                notificationChannel.lightColor = Color.BLUE // 불빛 색 지정
                notificationChannel.vibrationPattern = longArrayOf(0, 300, 100, 300) // 진동 패턴 지정
                notificationChannel.enableVibration(true) // 진동 허용
                notificationManager.createNotificationChannel(notificationChannel) // 채널 생성

                isNotificationChannelCreated = true
            }
        } catch (e: NullPointerException) {
            Toast.makeText(mContext, "푸시 알림 채널 생성이 실패했습니다. 앱을 재실행하거나 재설치해주세요.", Toast.LENGTH_SHORT).show()
        }

        // 푸시 알림 채널 생성 체크 후 AlarmManager 사용
        if (isNotificationChannelCreated) {
            setEmptyNotification(applicationContext)
            setResetNotification(applicationContext)
        }
    }
}