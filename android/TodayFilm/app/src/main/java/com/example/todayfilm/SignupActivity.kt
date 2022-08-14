package com.example.todayfilm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todayfilm.data.SignupData
import com.example.todayfilm.data.User
import com.example.todayfilm.databinding.ActivitySignupBinding
import com.example.todayfilm.retrofit.NetWorkClient.GetNetwork
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private var doubleBackToExit = false
    val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

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
        setContentView(binding.root)

        val spinner = binding.signupQuestions

        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.questions,
            R.layout.spinner_item
        )

        spinner.adapter = arrayAdapter

        binding.signupId.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (binding.signupId.length() < 6) {
                    Toast.makeText(this, "아이디는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signupPw.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (binding.signupPw.length() < 6) {
                    Toast.makeText(this, "비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signupToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            clickableFalse()
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        binding.signupBtn.setOnClickListener {
            val id = binding.signupId.text.toString().trim()
            val pw = binding.signupPw.text.toString().trim()
            val pw2 = binding.signupPwCheck.text.toString().trim()
            val nickname = binding.signupUsername.text.toString().trim()
            val question = binding.signupQuestions.selectedItemPosition
            val answer = binding.signupAnswer.text.toString().trim()

            if ((id.isEmpty()) || (pw.isEmpty()) || (pw2.isEmpty()) || (nickname.isEmpty()) || (answer.isEmpty())) {
                binding.signupErr.text = "기입하지 않은 란이 있습니다."
            } else if (question == 0) {
                binding.signupErr.text = "비밀번호 질문을 선택해주세요."
            } else if (pw != pw2) {
                binding.signupErr.text = "비밀번호가 일치하지 않습니다."
            } else {
                val user = User()
                user.userid = id
                user.userpassword = pw
                user.username = nickname
                user.userpwq = question
                user.userpwa = answer

                val call = GetNetwork.signUp(user)
                call.enqueue(object : Callback<SignupData> {
                    override fun onResponse(
                        call: Call<SignupData>,
                        response: Response<SignupData>
                    ) {
                        val result: SignupData? = response.body()

                        if (result?.message == "계정 생성이 완료되었습니다.") {
                            Toast.makeText(this@SignupActivity, "성공적으로 가입되었습니다.", Toast.LENGTH_SHORT).show()
                            moveToLogin()
                        } else if (result?.message == "중복된 아이디가 있습니다.") {
                            binding.signupErr.text = "이미 존재하는 아이디 입니다."
                        }
                    }

                    override fun onFailure(call: Call<SignupData>, t: Throwable) {
                        Toast.makeText(this@SignupActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun clickableFalse() {
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        binding.signupBtn.isClickable = false
        binding.signupToLogin.isClickable = false
        binding.signupId.isClickable = false
        binding.signupPw.isClickable = false
        binding.signupPwCheck.isClickable = false
    }
}