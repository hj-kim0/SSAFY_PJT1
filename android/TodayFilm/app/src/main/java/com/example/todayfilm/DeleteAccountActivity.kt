package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
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
        var userpid = MyPreference.read(this, "userid")
        var userpw = MyPreference.read(this, "userpassword")

        binding.deleteAccountBtn.setOnClickListener{
            var checkpw = binding.deleteAccountPw.text.toString()
            if (checkpw != userpw){

                binding.deleteAccountErr.setText("비밀번호가 일치하지 않습니다.")
            }else{
                var deleteUser = DeleteAccountRequest()
                deleteUser.userpid = userpid
                deleteUser.userpassword = userpw

                val call = NetWorkClient.GetNetwork.singout(deleteUser)
                call.enqueue(object : Callback<DeleteAccountResponse> {
                    override fun onResponse(
                        call: Call<DeleteAccountResponse>,
                        response: Response<DeleteAccountResponse>
                    ) {





                        var dialog = AlertDialog.Builder(this@DeleteAccountActivity)
                        dialog.setTitle("회원탈퇴")
                        dialog.setMessage("회원탈퇴가 정상적으로 완료되었습니다.")
                        dialog.show()

                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@DeleteAccountActivity, IntroActivity::class.java)
                            intent.putExtra("logout", "1")
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        }, 2500)
                    }

                    override fun onFailure(call: Call<DeleteAccountResponse>, t: Throwable) {
                        Log.d("", "실패" + t.message.toString())
                    }
                })
            }
        }

    }
}