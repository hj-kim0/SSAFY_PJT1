package com.example.todayfilm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.work.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeUnit

class InitApplication: Application() {
    private lateinit var mContext: Context
    var isNotificationChannelCreated = false
    val EMPTY_NOTIFICATION_WORK_NAME = "Empty Notification"
    val RESET_NOTIFICATION_WORK_NAME = "Reset Notification"

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

        // 푸시 알림 채널 생성 체크 후 WorkManager에 백그라운드 작업 등록
        val workManager = WorkManager.getInstance(applicationContext)

        if (isNotificationChannelCreated) {
            // 작업 생성 --- 필름 기록 알림
            val workEmptyPeriodicWorkRequest = PeriodicWorkRequest.Builder(EmptyNotificationWorker::class.java, 6, TimeUnit.HOURS)
                .setInitialDelay(EverySixHour(), TimeUnit.MILLISECONDS)
                .build()

            // 작업 등록
            workManager.enqueueUniquePeriodicWork(EMPTY_NOTIFICATION_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workEmptyPeriodicWorkRequest)

            // 작업 생성 --- 필름 리셋 알림
            val workResetPeriodicWorkRequest = PeriodicWorkRequest.Builder(ResetNotificationWorker::class.java, 24, TimeUnit.HOURS)
                .setInitialDelay(EveryMidnight(), TimeUnit.MILLISECONDS)
                .build()

            // 작업 등록
            workManager.enqueueUniquePeriodicWork(RESET_NOTIFICATION_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workResetPeriodicWorkRequest)

//            // 테스트용
//            // 작업 생성 --- 필름 리셋 알림
//            val workResetPeriodicWorkRequest = PeriodicWorkRequest.Builder(ResetNotificationWorker::class.java, 24, TimeUnit.HOURS)
//                .setInitialDelay(Test(), TimeUnit.MILLISECONDS)
//                .build()
//            workManager.enqueue(workResetPeriodicWorkRequest)
        }

        // 앱 최초 실행일 경우에 실행되는 작업
        // 내부 저장소에 imgcount, isComplete, imgvids 변수 초기화 및 preference 기본값 지정
        val sp = getSharedPreferences("isFirst", MODE_PRIVATE)
        val settingsSP = PreferenceManager.getDefaultSharedPreferences(mContext)
        val first = sp.getBoolean("isFirst", false)
        if (!first) {
            val editor = sp.edit()
            editor.putBoolean("isFirst", true)
            editor.apply()

            val date = SimpleDateFormat("yyyy/MM/dd (E)", Locale.KOREA)
                .format(System.currentTimeMillis())

            MyPreference.writeInt(this, "imgcount", 0)
            MyPreference.writeInt(this, "isComplete", 0)
            MyPreference.write(this, "imgvids", "")
            MyPreference.write(this, "date", date)

            val settingsEditor = settingsSP.edit()
            settingsEditor.putBoolean("empty", true)
            settingsEditor.putBoolean("follow", true)
            settingsEditor.putBoolean("like", true)
            settingsEditor.putBoolean("new", true)
            settingsEditor.putBoolean("repeat", true)
            settingsEditor.putBoolean("shake", true)
            settingsEditor.apply()
        }
    }
}