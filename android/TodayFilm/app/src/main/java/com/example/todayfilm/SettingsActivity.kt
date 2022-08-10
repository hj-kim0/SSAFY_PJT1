package com.example.todayfilm

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.todayfilm.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_content_settings, SettingsPreference())
                    .commit()
        }
    }

    class SettingsPreference : PreferenceFragmentCompat() {
        // 저장되는 데이터에 접근하기 위한 SharedPreferences
        lateinit var prefs: SharedPreferences

        // Preference 객체
        var repeatPreference: Preference? = null
        var shakePreference: Preference? = null

        // onCreatee() 중에 호출되어 Fragment에 preference를 제공
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preference, rootKey)

            if (rootKey == null) {
                // Preference 객체 초기화
                repeatPreference = findPreference("repeat")
                shakePreference = findPreference("shake")

                // SharedPreferences 객체 초기화
                prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
            }
        }

        val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
            when (key) {
                "repeat" -> {
                    val value = prefs.getBoolean("repeat", false)
                }
                "shake" -> {
                    val value = prefs.getBoolean("shake", false)
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

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            val key = preference.key

            when (key) {
                "logout" -> {
                    // 다이얼로그 띄우기
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("로그아웃 하시겠습니까?")
                        .setMessage("완성되지 않은 필름은 삭제됩니다.")
                        .setPositiveButton("예", DialogInterface.OnClickListener { dialog, id ->
                            // 서버로 로그아웃 요청

                            // 응답 받은 후 토스트 띄우고 intro 액티비티로 이동
                            showToast()
                            val intent = Intent(activity, IntroActivity::class.java)
                            intent.putExtra("logout", "1")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        })
                        .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, id -> })
                    builder.show()
                }
                "deleteAccount" -> {
                    // delete account 액티비티로 이동
                    val intent = Intent(activity, DeleteAccountActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }

            return super.onPreferenceTreeClick(preference)
        }

        private fun showToast() {
            Toast.makeText(activity, "성공적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}