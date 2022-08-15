package com.example.todayfilm

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ForecdTerminationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) { //핸들링 하는 부분
        MyPreference.write(this, "keyword", "")
        stopSelf() //서비스 종료
    }
}
