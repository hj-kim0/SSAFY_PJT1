package com.example.todayfilm

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.todayfilm.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var doubleBackToExit = false

    override fun onBackPressed() {
        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, "종료하시려면 뒤로가기를 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed {
                doubleBackToExit = false
            }
        }
    }

    private fun runDelayed(function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, 1500L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @SuppressLint("ResourceType")
        val animation_1 = AnimationUtils.loadAnimation(this,R.animator.alpha1)
        @SuppressLint("ResourceType")
        val animation_2 = AnimationUtils.loadAnimation(this,R.animator.alpha2)
        @SuppressLint("ResourceType")
        val animation_3 = AnimationUtils.loadAnimation(this,R.animator.alpha3)

        binding.introOpen.startAnimation(animation_1)
        binding.introTitle.startAnimation(animation_2)
        binding.introBtn.startAnimation(animation_3)



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