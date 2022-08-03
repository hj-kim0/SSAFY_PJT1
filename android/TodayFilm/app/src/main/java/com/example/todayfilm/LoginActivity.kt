package com.example.todayfilm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.todayfilm.data.LoginData
import com.example.todayfilm.data.User
import com.example.todayfilm.databinding.ActivityLoginBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            var LoginId = binding.loginId.text.toString()
            var LoginPw = binding.loginPw.text.toString()
            var user = User()
            user.userid = LoginId
            user.userpassword = LoginPw

            val call = NetWorkClient.GetNetwork.login(user)
            call.enqueue(object : Callback<LoginData> {
                override fun onResponse(call: Call<LoginData>, response: Response<LoginData>) {
                    val result: LoginData? = response.body()


                    if (result?.message == "로그인 완료"){
                        Log.d("test:", result.token)
                        MyPreference.write(this@LoginActivity, "userid", result.userpid)
                        MyPreference.write(this@LoginActivity, "usertoken", result.token)
                        MyPreference.write(this@LoginActivity, "userpassword", user.userpassword)
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        }, 500)
                    }else{
                        var dialog = AlertDialog.Builder(this@LoginActivity)
                        dialog.setTitle("로그인 실패")
                        dialog.setMessage("입력하신 정보가 잘못되었습니다. 다시 입력해 주세요")
                        dialog.show()
                    }

                }

                override fun onFailure(call: Call<LoginData>, t: Throwable) {
                    Log.d("", "실패"+t.message.toString())
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