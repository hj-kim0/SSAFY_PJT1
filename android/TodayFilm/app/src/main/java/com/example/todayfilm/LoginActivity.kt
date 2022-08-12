package com.example.todayfilm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.todayfilm.data.LoginData
import com.example.todayfilm.data.User
import com.example.todayfilm.databinding.ActivityLoginBinding
import com.example.todayfilm.retrofit.NetWorkClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {
    private var doubleBackToExit = false
    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onBackPressed() {
        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, "종료하시려면 뒤로가기를 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
            }
        }
    }

    fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginToFindPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, FindPasswordActivity::class.java)
            clickableFalse()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.loginToSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            clickableFalse()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        binding.loginBtn.setOnClickListener{
            val loadingDialog = LoadingDialog(this)
            loadingDialog.show()

            val loginId = binding.loginId.text.toString()
            val loginPw = binding.loginPw.text.toString()

            val user = User()
            user.userid = loginId
            user.userpassword = loginPw

            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(object : OnCompleteListener<String?> {
                    override fun onComplete(@NonNull task: Task<String?>) {
                        // 새로운 토큰 생성 성공 시
                        user.userfcmtoken = task.getResult()
                        val call = NetWorkClient.GetNetwork.login(user)

                        call.enqueue(object : Callback<LoginData> {
                            override fun onResponse(
                                call: Call<LoginData>,
                                response: Response<LoginData>
                            ) {
                                val result: LoginData? = response.body()

                                if (result != null) {
                                    MyPreference.write(this@LoginActivity, "userpid", result.userpid)
                                    MyPreference.write(this@LoginActivity, "usertoken", result.token)
                                    MyPreference.write(this@LoginActivity, "userid", loginId)
                                    MyPreference.write(this@LoginActivity, "userpassword", loginPw)
                                    MyPreference.write(this@LoginActivity, "todayarticleidx", result.todayarticleidx)

                                    // 앱 최초 로그인일 경우에 실행되는 작업
                                    // 내부 저장소에 imgcount, isComplete, imgvids 변수 초기화 및 preference 기본값 지정
                                    val sp = getSharedPreferences("my_sp_storage", MODE_PRIVATE)
                                    val settingsSP =
                                        PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                                    val first = sp.getBoolean("isFirst", false)
                                    if (!first) {
                                        val editor = sp.edit()
                                        editor.putBoolean("isFirst", true)
                                        editor.apply()

                                        val date = SimpleDateFormat("yyyy/MM/dd (E)", Locale.KOREA)
                                            .format(System.currentTimeMillis())

                                        MyPreference.writeInt(this@LoginActivity, "imgcount", 0)
                                        MyPreference.writeInt(this@LoginActivity, "isComplete", 0)
                                        MyPreference.write(this@LoginActivity, "imgvids", "")
                                        MyPreference.write(this@LoginActivity, "date", date)

                                        val settingsEditor = settingsSP.edit()
                                        settingsEditor.putBoolean("empty", true)
                                        settingsEditor.putBoolean("follow", true)
                                        settingsEditor.putBoolean("like", true)
                                        settingsEditor.putBoolean("new", true)
                                        settingsEditor.putBoolean("repeat", true)
                                        settingsEditor.putBoolean("shake", true)
                                        settingsEditor.apply()
                                    }

                                    if (result.todayarticleidx != "-1") {
                                        MyPreference.writeInt(this@LoginActivity, "isComplete", 1)
                                    }

                                    loadingDialog.dismiss()

                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    loadingDialog.dismiss()
                                    Toast.makeText(this@LoginActivity, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<LoginData>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                Log.d("로그인 실패", t.message.toString())
                            }
                        })
                    }
                })
        }
    }

    private fun clickableFalse() {
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.loginBtn.isClickable = false
        binding.loginToSignup.isClickable = false
        binding.loginId.isClickable = false
        binding.loginPw.isClickable = false
        binding.loginToFindPassword.isClickable = false
    }
}