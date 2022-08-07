package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todayfilm.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // settings에서 logout 버튼 눌러서 넘어온 경우, 내부 데이터 전부 삭제
        val logout = intent.getStringExtra("logout")
        println(logout)
        if (logout == "1"){
            resetData(this)
            MyPreference.clear(this)
        }

        val usertoken = MyPreference.read(this, "usertoken")

        if (usertoken != ""){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()

        }
        binding.introSignup.setOnClickListener {
            clickableFalse()
            val intent = Intent(this, SignupActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        binding.introLogin.setOnClickListener {
            clickableFalse()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun clickableFalse() {
        val binding = ActivityIntroBinding.inflate(layoutInflater)
        binding.introSignup.isClickable = false
        binding.introLogin.isClickable = false
    }
}