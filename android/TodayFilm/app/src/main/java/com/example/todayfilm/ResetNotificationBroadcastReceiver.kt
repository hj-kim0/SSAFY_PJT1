package com.example.todayfilm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class ResetNotificationBroadcastReceiver: BroadcastReceiver() {
    companion object {
        const val RESET_NOTIFICATION_CODE = 3
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 != null) {
            // 작업 수행
            val isComplete = MyPreference.readInt(p0, "isComplete")
            val imgcount = MyPreference.readInt(p0, "imgcount")
            val imgvids = MyPreference.read(p0, "imgvids")
            val date = SimpleDateFormat("yyyy/MM/dd (E)", Locale.KOREA)
                .format(System.currentTimeMillis())

            // 푸시 알림 생성
            val notificationManager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder = NotificationCompat.Builder(p0, "10001")
            notificationBuilder.setSmallIcon(R.drawable.logo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true) // 클릭 시 푸시 알림 제거

            // 완성 O -> 데이터 초기화, date 갱신
            if (isComplete == 1) {
                resetData(p0)
                MyPreference.write(p0, "date", date)
            } else {
                if (imgcount > 0 || imgvids != "") {
                    // 완성 X && 데이터 O -> MainActivity 호출
                    val intent = Intent(p0, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) // 앱이 대기열에 있는 경우 재실행이 아닌 활성화
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intent.putExtra("fromResetNotification", true)

                    // pendingIntent 생성 --- 앱이 구동되고 있지 않는 등 특정 시점에서 수행하는 intent
                    // 코드(3)이 같으면 같은 알림으로 인식 --- 개별 생성 X, 기존 알림 갱신
                    val pendingIntent = PendingIntent.getActivity(p0,
                        RESET_NOTIFICATION_CODE, intent, PendingIntent.FLAG_IMMUTABLE)

                    // 푸시 알림 설정
                    notificationBuilder.setContentTitle("어제의 필름이 완성되지 않았습니다.")
                        .setContentText("알림을 눌러 확인해주세요!")
                        .setContentIntent(pendingIntent)

                    if (notificationManager != null) {
                        notificationManager.notify(RESET_NOTIFICATION_CODE, notificationBuilder.build())
                    }
                } else {
                    // 완성 X && 데이터 X -> date 갱신
                    MyPreference.write(p0, "date", date)
                }
            }

            // AlarmManager 재등록
            setResetNotification(p0)
        }
    }
}