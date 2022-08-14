package com.example.todayfilm

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.todayfilm.data.LogoutRequest
import com.example.todayfilm.data.LogoutResponse
import com.example.todayfilm.databinding.ActivitySettingsBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        private lateinit var prefs: SharedPreferences

        // Preference 객체
        private var repeatPreference: Preference? = null
        private var shakePreference: Preference? = null

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

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                "logout" -> {
                    // 다이얼로그 띄우기
                    val normaldialog = NormalDialogFragment()
                    val duration = Toast.LENGTH_SHORT
                    val btn= arrayOf("네", "아니오")
                    normaldialog.arguments = bundleOf(
                        "bodyContext" to "완성되지 않은 필름은 삭제됩니다.",
                        "bodyTitle" to "로그아웃 하시겠습니까?",
                        "btnData" to btn
                    )

                    normaldialog.show((activity as SettingsActivity).supportFragmentManager, "CustomDialog")

                    normaldialog.setButtonClickListener(object :
                        NormalDialogFragment.OnButtonClickListener {
                        override fun onButton1Clicked() {
                            // 네 버튼을 눌렀을 때 처리할 곳
                            val logoutuser = LogoutRequest()
                            val userpid = MyPreference.read(requireActivity(), "userpid").toInt()
                            logoutuser.userpid = userpid
                            val call = NetWorkClient.GetNetwork.signout(logoutuser)
                            call.enqueue(object : Callback<LogoutResponse> {
                                override fun onResponse(
                                    call: Call<LogoutResponse>,
                                    response: Response<LogoutResponse>
                                ) {
                                    val intent = Intent(activity, IntroActivity::class.java)
                                    intent.putExtra("logout", "1")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)

                                    Toast.makeText(context, "성공적으로 로그아웃되었습니다.", duration).show()
                                }

                                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                                    Toast.makeText(context, "로그아웃에 실패했습니다.", duration).show()
                                    Log.e("로그아웃 실패", t.message.toString())
                                }
                            })
                        }

                        override fun onButton2Clicked() {}
                    })
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
    }
}