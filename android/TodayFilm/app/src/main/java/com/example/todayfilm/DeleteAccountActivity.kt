package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.todayfilm.data.DeleteAccountRequest
import com.example.todayfilm.data.DeleteAccountResponse


import com.example.todayfilm.databinding.ActivityDeleteAccountBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteAccountActivity : AppCompatActivity() {
    val binding by lazy {ActivityDeleteAccountBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val userpid = MyPreference.read(this, "userpid")
        val userpw = MyPreference.read(this, "userpassword")

        binding.deleteAccountBtn.setOnClickListener{
            val checkpw = binding.deleteAccountPw.text.toString().trim()

            if (checkpw.isEmpty()) {
                binding.deleteAccountErr.text = "기입하지 않은 란이 있습니다."
            } else if (checkpw != userpw){
                binding.deleteAccountErr.text = "비밀번호가 일치하지 않습니다."
            } else {
                val deleteUser = DeleteAccountRequest()
                deleteUser.userpid = userpid.toInt()
                deleteUser.userpassword = userpw

                val call = NetWorkClient.GetNetwork.signdown(deleteUser)
                call.enqueue(object : Callback<DeleteAccountResponse> {
                    override fun onResponse(
                        call: Call<DeleteAccountResponse>,
                        response: Response<DeleteAccountResponse>
                    ) {
                        val result: DeleteAccountResponse? = response.body()

                        if (result?.success == "true") {
                            Toast.makeText(this@DeleteAccountActivity, "성공적으로 탈퇴되었습니다.", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@DeleteAccountActivity, IntroActivity::class.java)
                            intent.putExtra("logout", "1")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        } else if (result?.success == "false") {
                            Toast.makeText(this@DeleteAccountActivity, "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            Log.e("회원탈퇴 실패", result.message)
                        }
                    }

                    override fun onFailure(call: Call<DeleteAccountResponse>, t: Throwable) {
                        Toast.makeText(this@DeleteAccountActivity, "회원탈퇴에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("회원탈퇴 실패", t.message.toString())
                    }
                })
            }
        }
    }
}