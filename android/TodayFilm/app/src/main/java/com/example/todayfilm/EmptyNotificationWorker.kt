package com.example.todayfilm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import androidx.work.*

class EmptyNotificationWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        const val WORK_EMPTY_NOTIFICATION_CODE = 0
    }

    override fun doWork(): Result {
        val isEmptyNotificationActivated = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("empty", true)
        val imgcount = MyPreference.readInt(context, "imgcount")

        if (isEmptyNotificationActivated && imgcount < 4) {
            // 푸시 알림 생성
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder = NotificationCompat.Builder(context, "10001")
            notificationBuilder.setSmallIcon(R.drawable.logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true) // 클릭 시 푸시 알림 제거

            // 푸시 알림 동작 --- 클릭 시 MainActivity 호출
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) // 앱이 대기열에 있는 경우 재실행이 아닌 활성화
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            // pendingIntent 생성 --- 앱이 구동되고 있지 않는 등 특정 시점에서 수행하는 intent
            // 코드(0)이 같으면 같은 알림으로 인식 --- 개별 생성 X, 기존 알림 갱신
            val pendingIntent = PendingIntent.getActivity(context, WORK_EMPTY_NOTIFICATION_CODE, intent, PendingIntent.FLAG_IMMUTABLE)

            // 푸시 알림 설정
            notificationBuilder.setContentTitle("오늘의 남은 사진: ${4 - imgcount}장")
                .setContentText("사진을 찍어 필름을 완성해보세요!")
                .setContentIntent(pendingIntent)

            if (notificationManager != null) {
                notificationManager.notify(WORK_EMPTY_NOTIFICATION_CODE, notificationBuilder.build())
            }
        }

        return Result.success()
    }
}