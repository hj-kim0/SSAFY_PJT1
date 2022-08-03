package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.todayfilm.databinding.ActivityChangeProfileBinding

class ChangeProfileActivity : AppCompatActivity() {
    val binding by lazy { ActivityChangeProfileBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.changeProfileBtn.setOnClickListener {
            val username = binding.changeProfileUsername.text.toString()
            val description = binding.changeProfileDescription.text.toString()

            if ((username.isEmpty()) || (description.isEmpty())) {
                binding.changeProfileErr.text = "기입하지 않은 란이 있습니다."
            } else {
                // 서버로 요청 보내기

                // 응답 받으면 토스트 띄우고 뒤로가기
                Toast.makeText(this, "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }

        binding.changeProfileToChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}