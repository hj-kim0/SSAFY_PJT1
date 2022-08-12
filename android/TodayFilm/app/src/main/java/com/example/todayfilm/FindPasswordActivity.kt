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
import com.example.todayfilm.MyPreference

class FindPasswordActivity : AppCompatActivity() {
    val binding by lazy { ActivityFindPasswordBinding.inflate(layoutInflater) }
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val pid = MyPreference.read(this, "userid")

        val spinner = binding.findPasswordQuestions
        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.questions,
            R.layout.spinner_item
        )
        spinner.adapter = arrayAdapter

        binding.findPasswordBtn.setOnClickListener {
            if (binding.findPasswordGroup.visibility == View.VISIBLE) {
                id = binding.findPasswordId.text.toString()
                MyPreference.write(this@FindPasswordActivity, "userid", id)
                val question = binding.findPasswordQuestions.selectedItemPosition
                val answer = binding.findPasswordAnswer.text.toString()
                if ((id.isEmpty()) || (question == 0) || (answer.isEmpty())) {
                    binding.findPasswordErr.text = "기입하지 않은 란이 있습니다."
                } else {
                    // 서버로 요청 보내기
                    var findPw = FindPwRequest()
                    findPw.userid = id
                    findPw.userpwq = question
                    findPw.userpwa = answer

                    val call = NetWorkClient.GetNetwork.findpw(findPw)
                    call.enqueue(object : retrofit2.Callback<FindPwResponse> {
                        override fun onResponse(call: Call<FindPwResponse>, response: Response<FindPwResponse>) {
                            val result: FindPwResponse? = response.body()
                            if (result?.message == "비밀 번호 질문 일치"){
//                                MyPreference.write(this@FindPasswordActivity, "userpid", pid)
                                binding.findPasswordGroup.visibility = View.GONE
                                binding.changePasswordGroup.visibility = View.VISIBLE
                                binding.findPasswordErr.text = ""
                                // 응답 받으면 새로운 비밀번호 작성 폼 띄우기
                                binding.findPasswordGroup.visibility = View.GONE
                                binding.changePasswordGroup.visibility = View.VISIBLE
                                binding.findPasswordErr.text = ""
                            }else{
                                Toast.makeText(this@FindPasswordActivity, "비밀번호 질문과 답변이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

                            }
                        }

                        override fun onFailure(call: Call<FindPwResponse>, t: Throwable) {
                            Log.d("", "실패" + t.message.toString())
                        }
                    })

//                    // 응답 받으면 새로운 비밀번호 작성 폼 띄우기
//                    binding.findPasswordGroup.visibility = View.GONE
//                    binding.changePasswordGroup.visibility = View.VISIBLE
//                    binding.findPasswordErr.text = ""
                }
            } else if (binding.changePasswordGroup.visibility == View.VISIBLE) {
                val newPw = binding.findPasswordNewPw.text.toString()
                val newPwCheck = binding.findPasswordNewPwCheck.text.toString()
                Log.d("test1", newPw+newPwCheck)
                if ((newPw.isEmpty()) || (newPwCheck.isEmpty())) {
                    binding.findPasswordErr.text = "기입하지 않은 란이 있습니다."
                } else if (newPw != newPwCheck) {
                    binding.findPasswordErr.text = "새로운 비밀번호가 일치하지 않습니다."
                } else {
                        Log.d("test2", "여기")
                        // 서버로 요청 보내기11
                    val userid = MyPreference.read(this@FindPasswordActivity, "userid")
                    var changepw = ChangePwRequest()
                    changepw.userid = userid
                    changepw.usernewpw = newPw
                    Log.d("tests",changepw.userid + " " + changepw.usernewpw)
                    val call = NetWorkClient.GetNetwork.changepw(changepw)
                    call.enqueue(object : retrofit2.Callback<ChangePwResponse> {
                        override fun onResponse(
                            call: Call<ChangePwResponse>,
                            response: Response<ChangePwResponse>
                        ) {
                            Toast.makeText(this@FindPasswordActivity, "성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            onBackPressed()



                        }

                        override fun onFailure(call: Call<ChangePwResponse>, t: Throwable) {
                            Log.d("", "실패" + t.message.toString())
                        }
                    })
//                         응답 받으면 login 액티비티로 이동
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                }
            }
        }
    }
}