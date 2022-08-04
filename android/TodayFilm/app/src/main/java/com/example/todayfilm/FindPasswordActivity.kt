package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.todayfilm.databinding.ActivityFindPasswordBinding
import kotlin.reflect.typeOf

class FindPasswordActivity : AppCompatActivity() {
    val binding by lazy { ActivityFindPasswordBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val spinner = binding.findPasswordQuestions
        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.questions,
            R.layout.spinner_item
        )
        spinner.adapter = arrayAdapter

        binding.findPasswordBtn.setOnClickListener {
            if (binding.findPasswordGroup.visibility == View.VISIBLE) {
                val id = binding.findPasswordId.text.toString()
                val question = binding.findPasswordQuestions.selectedItemPosition
                val answer = binding.findPasswordAnswer.text.toString()

                if ((id.isEmpty()) || (question == 0) || (answer.isEmpty())) {
                    binding.findPasswordErr.text = "기입하지 않은 란이 있습니다."
                } else {
                    // 서버로 요청 보내기

                    // 응답 받으면 새로운 비밀번호 작성 폼 띄우기
                    binding.findPasswordGroup.visibility = View.GONE
                    binding.changePasswordGroup.visibility = View.VISIBLE
                    binding.findPasswordErr.text = ""
                }
            } else if (binding.changePasswordGroup.visibility == View.VISIBLE) {
                val newPw = binding.findPasswordNewPw.text.toString()
                val newPwCheck = binding.findPasswordNewPwCheck.text.toString()

                if ((newPw.isEmpty()) || (newPwCheck.isEmpty())) {
                    binding.findPasswordErr.text = "기입하지 않은 란이 있습니다."
                } else if (newPw != newPwCheck) {
                    binding.findPasswordErr.text = "새로운 비밀번호가 일치하지 않습니다."
                } else {
                        // 서버로 요청 보내기

                        // 응답 받으면 login 액티비티로 이동
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                }
            }
        }
    }
}