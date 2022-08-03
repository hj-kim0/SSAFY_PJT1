package com.example.todayfilm

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
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
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.preference)
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            val key = preference.key

            when (key) {
                "changePw" -> {
                    // change password 액티비티로 이동
                    val intent = Intent(activity, ChangePasswordActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
                "logout" -> {
                    // 다이얼로그 띄우기
                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("로그아웃 하시겠습니까?")
                        .setPositiveButton("예", DialogInterface.OnClickListener { dialog, id ->
                            // 서버로 로그아웃 요청

                            // 응답 받은 후 토스트 띄우고 intro 액티비티로 이동
                            showToast()
                            val intent = Intent(activity, IntroActivity::class.java)
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