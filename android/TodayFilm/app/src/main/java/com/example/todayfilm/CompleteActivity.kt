package com.example.todayfilm

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.todayfilm.databinding.ActivityCompleteBinding

class CompleteActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = FrameFragment()
        transaction.add(R.id.fragment_content_complete, fragment)
        transaction.commit()

        // 대표 사진 지정
        var mainImage = 0

        binding.completeBtn.setOnClickListener {
            if (mainImage == 0) {
                Toast.makeText(this, "대표 사진을 선택해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                // 서버로 데이터 전송

                // 응답 받은 후 토스트 띄우고 main 액티비티로 이동
                Toast.makeText(this, "성공적으로 기록되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }
}