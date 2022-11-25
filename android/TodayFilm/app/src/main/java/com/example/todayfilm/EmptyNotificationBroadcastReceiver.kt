package com.example.todayfilm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager

class EmptyNotificationBroadcastReceiver: BroadcastReceiver() {
    companion object {
        const val EMPTY_NOTIFICATION_CODE = 1
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 != null) {
            // 작업 수행
            val isEmptyNotificationActivated = PreferenceManager.getDefaultSharedPreferences(p0).getBoolean("empty", true)
            val imgcount = MyPreference.readInt(p0, "imgcount")
            val isComplete = MyPreference.readInt(p0, "isComplete")

            if (isEmptyNotificationActivated && imgcount < 4 && isComplete != 1 ) {
                // 푸시 알림 생성
                val notificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationBuilder = NotificationCompat.Builder(p0, "10001")
                notificationBuilder.setSmallIcon(R.drawable.logo)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true) // 클릭 시 푸시 알림 제거

                // 푸시 알림 동작 --- 클릭 시 MainActivity 호출
                val intent = Intent(p0, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) // 앱이 대기열에 있는 경우 재실행이 아닌 활성화
                intent.addCategory(Intent.CATEGORY_LAUNCHER)

                // pendingIntent 생성 --- 앱이 구동되고 있지 않는 등 특정 시점에서 수행하는 intent
                // 코드(1)이 같으면 같은 알림으로 인식 --- 개별 생성 X, 기존 알림 갱신
                val pendingIntent = PendingIntent.getActivity(p0,
                    EMPTY_NOTIFICATION_CODE, intent, PendingIntent.FLAG_IMMUTABLE)

                // 푸시 알림 설정
                notificationBuilder.setContentTitle("오늘의 남은 사진: ${4 - imgcount}장")
                    .setContentText("사진을 찍어 필름을 완성해보세요!")
                    .setContentIntent(pendingIntent)

                notificationManager.notify(EMPTY_NOTIFICATION_CODE, notificationBuilder.build())
            }

            // AlarmManager 재등록
            setEmptyNotification(p0)
        }
    }
}