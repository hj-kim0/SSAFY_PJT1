package com.example.todayfilm

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class NotificationPreferenceFragment: PreferenceFragmentCompat() {
    lateinit var prefs: SharedPreferences

    var emptyPreference: Preference? = null
    var followPreference: Preference? = null
    var likePreference: Preference? = null
    var newPreference: Preference? = null

    // onCreate() 중에 호출되어 Fragment에 preference를 제공하는 메서드
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_notification, rootKey)

        if (rootKey == null) {
            // Preference 객체들 초기화
            emptyPreference = findPreference("empty")
            followPreference = findPreference("follow")
            likePreference = findPreference("like")
            newPreference = findPreference("new")
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
    }

    // 설정 변경 이벤트 처리
    val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
        when (key) {
            "empty" -> {
                val value = prefs.getBoolean("empty", false)
            }
            "follow" -> {
                val value = prefs.getBoolean("follow", false)
            }
            "like" -> {
                val value = prefs.getBoolean("like", false)
            }
            "new" -> {
                val value = prefs.getBoolean("new", false)
            }
        }
    }

    // 리스너 등록
    override fun onResume() {
        super.onResume()
        prefs.registerOnSharedPreferenceChangeListener(prefListener)
    }

    // 리스너 해제
    override fun onPause() {
        super.onPause()
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}