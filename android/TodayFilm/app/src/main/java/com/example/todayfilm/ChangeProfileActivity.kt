package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.ActivityChangeProfileBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                var changeUser = ChangeUserDetailRequest()
                changeUser.username = username
                changeUser.userdesc = description

                val call = NetWorkClient.GetNetwork.changeuserdetail(changeUser)
                call.enqueue(object : Callback<ChangeUserDetailResponse> {
                    override fun onResponse(
                        call: Call<ChangeUserDetailResponse>,
                        response: Response<ChangeUserDetailResponse>
                    ) {

                        MyPreference.write(this@ChangeProfileActivity, "username", username)
                        MyPreference.write(this@ChangeProfileActivity, "userdesc", description)


                      
                    }

                    override fun onFailure(call: Call<ChangeUserDetailResponse>, t: Throwable) {
                        Log.d("", "실패" + t.message.toString())
                    }
                })
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