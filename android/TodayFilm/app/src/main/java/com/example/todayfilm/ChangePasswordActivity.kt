package com.example.todayfilm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.todayfilm.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    val binding by lazy { ActivityChangePasswordBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.changePasswordBtn.setOnClickListener {
            val oldPw = binding.changePasswordOldPw.text.toString()
            val newPw = binding.changePasswordNewPw.text.toString()
            val newPwCheck = binding.changePasswordNewPwCheck.text.toString()
            val userpassword = MyPreference.read(this, "userpassword")

            if ((oldPw.isEmpty()) || (newPw.isEmpty()) || (newPwCheck.isEmpty())) {
                binding.changePasswordErr.text = "기입하지 않은 란이 있습니다."
            } else if (oldPw != userpassword) {
                binding.changePasswordErr.text = "기존 비밀번호가 일치하지 않습니다."
            } else if (newPw != newPwCheck) {
                binding.changePasswordErr.text = "새로운 비밀번호가 일치하지 않습니다."
            } else {
                // 서버로 요청 보내기

                // 응답 받으면 토스트 띄우고 뒤로가기
                Toast.makeText(this, "성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
    }
}