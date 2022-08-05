package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.todayfilm.data.ChangePwRequest
import com.example.todayfilm.data.ChangePwResponse
import com.example.todayfilm.data.DeleteAccountResponse
import com.example.todayfilm.databinding.ActivityChangePasswordBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    val binding by lazy { ActivityChangePasswordBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val userpassword = MyPreference.read(this, "userpassword")
        val userpid = MyPreference.read(this, "userid")

        binding.changePasswordNewPw.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.changePasswordNewPw.length() < 6) {
                    Toast.makeText(this, "비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.changePasswordBtn.setOnClickListener {
            val oldPw = binding.changePasswordOldPw.text.toString()
            val newPw = binding.changePasswordNewPw.text.toString()
            val newPwCheck = binding.changePasswordNewPwCheck.text.toString()

            if ((oldPw.isEmpty()) || (newPw.isEmpty()) || (newPwCheck.isEmpty())) {
                binding.changePasswordErr.text = "기입하지 않은 란이 있습니다."
            } else if (oldPw != userpassword) {
                binding.changePasswordErr.text = "기존 비밀번호가 일치하지 않습니다."
            } else if (oldPw == newPw) {
                binding.changePasswordErr.text = "기존 비밀번호와 다른 비밀번호를 입력하세요."
            } else if (newPw != newPwCheck) {
                binding.changePasswordErr.text = "새로운 비밀번호가 일치하지 않습니다."
            }else{
                // 서버로 요청 보내기
                var changepw = ChangePwRequest()
                changepw.userpid = userpid
                changepw.userpassword = newPw
                val call = NetWorkClient.GetNetwork.changepw(changepw)
                call.enqueue(object : Callback<ChangePwResponse> {
                    override fun onResponse(
                        call: Call<ChangePwResponse>,
                        response: Response<ChangePwResponse>
                    ) {
                        MyPreference.write(this@ChangePasswordActivity, "userpassword", newPw)
                        Toast.makeText(this@ChangePasswordActivity, "성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }

                    override fun onFailure(call: Call<ChangePwResponse>, t: Throwable) {
                        Log.d("", "실패" + t.message.toString())
                    }
                })
            }
        }
    }
}