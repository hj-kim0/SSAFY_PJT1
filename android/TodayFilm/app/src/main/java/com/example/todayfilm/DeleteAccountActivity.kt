package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.todayfilm.databinding.ActivityDeleteAccountBinding

class DeleteAccountActivity : AppCompatActivity() {
    val binding by lazy { ActivityDeleteAccountBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.deleteAccountBtn.setOnClickListener {
            val pw = binding.deleteAccountPw.text.toString()
            val userpassword = MyPreference.read(this, "userpassword")

            if (pw.isEmpty()) {
                binding.deleteAccountErr.text = "기입하지 않은 란이 있습니다."
            } else if (pw != userpassword) {
                binding.deleteAccountErr.text = "비밀번호가 일치하지 않습니다."
            } else {
                // 서버로 요청 보내기

                // 응답 받으면 토스트 띄우고 intro 액티비티로 이동
                Toast.makeText(this, "성공적으로 탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }
}