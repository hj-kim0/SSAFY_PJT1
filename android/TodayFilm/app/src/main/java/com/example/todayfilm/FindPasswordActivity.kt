package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.ActivityFindPasswordBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Response

class FindPasswordActivity : AppCompatActivity() {
    val binding by lazy { ActivityFindPasswordBinding.inflate(layoutInflater) }
    var id = ""

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
                id = binding.findPasswordId.text.toString().trim()
                MyPreference.write(this@FindPasswordActivity, "userid", id)
                val question = binding.findPasswordQuestions.selectedItemPosition
                val answer = binding.findPasswordAnswer.text.toString().trim()

                if ((id.isEmpty()) || (answer.isEmpty())) {
                    binding.findPasswordErr.text = "기입하지 않은 란이 있습니다."
                } else if (question == 0) {
                    binding.findPasswordErr.text = "비밀번호 질문을 선택해주세요."
                } else {
                    // 서버로 요청 보내기
                    val findPw = FindPwRequest()
                    findPw.userid = id
                    findPw.userpwq = question
                    findPw.userpwa = answer

                    val call = NetWorkClient.GetNetwork.findpw(findPw)
                    call.enqueue(object : retrofit2.Callback<FindPwResponse> {
                        override fun onResponse(call: Call<FindPwResponse>, response: Response<FindPwResponse>) {
                            val result: FindPwResponse? = response.body()

                            if (result?.message == "비밀 번호 질문 일치") {
                                // 응답 받으면 새로운 비밀번호 작성 폼 띄우기
                                binding.findPasswordGroup.visibility = View.GONE
                                binding.changePasswordGroup.visibility = View.VISIBLE
                                binding.findPasswordErr.text = ""
                            } else {
                                Toast.makeText(this@FindPasswordActivity, "비밀번호 질문과 답변이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<FindPwResponse>, t: Throwable) {
                            Toast.makeText(this@FindPasswordActivity, "비밀번호 찾기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            Log.e("비밀번호 찾기 실패", t.message.toString())
                        }
                    })
                }
            } else if (binding.changePasswordGroup.visibility == View.VISIBLE) {
                val newPw = binding.findPasswordNewPw.text.toString().trim()
                val newPwCheck = binding.findPasswordNewPwCheck.text.toString().trim()

                if ((newPw.isEmpty()) || (newPwCheck.isEmpty())) {
                    binding.findPasswordErr.text = "기입하지 않은 란이 있습니다."
                } else if (newPw != newPwCheck) {
                    binding.findPasswordErr.text = "새로운 비밀번호가 일치하지 않습니다."
                } else {
                    // 서버로 요청 보내기
                    val userid = MyPreference.read(this@FindPasswordActivity, "userid")
                    val changepw = ChangePwRequest()
                    changepw.userid = userid
                    changepw.usernewpw = newPw

                    val call = NetWorkClient.GetNetwork.changepw(changepw)
                    call.enqueue(object : retrofit2.Callback<ChangePwResponse> {
                        override fun onResponse(
                            call: Call<ChangePwResponse>,
                            response: Response<ChangePwResponse>
                        ) {
                            val result: ChangePwResponse? = response.body()

                            if (result?.success == "true") {
                                Toast.makeText(this@FindPasswordActivity, "성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@FindPasswordActivity, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            } else if (result?.success == "false") {
                                Toast.makeText(this@FindPasswordActivity, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                Log.e("비밀번호 변경 실패", result.message)
                            }
                        }

                        override fun onFailure(call: Call<ChangePwResponse>, t: Throwable) {
                            Toast.makeText(this@FindPasswordActivity, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            Log.e("비밀번호 변경 실패", t.message.toString())
                        }
                    })
                }
            }
        }
    }
}