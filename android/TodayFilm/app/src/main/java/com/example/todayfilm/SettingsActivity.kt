package com.example.todayfilm

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.todayfilm.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 여부에 따라 계정 메뉴 변화
        // binding.settingsGuest.visibility = View.VISIBLE
        // binding.settingsUser.visibility = View.GONE

        binding.settingsToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.settingsToChangePassword.setOnClickListener{
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        binding.settingsLogout.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃 하시겠습니까?")
            builder.setMessage("기기의 데이터는 유지됩니다.")

            val listener = DialogInterface.OnClickListener{ _, p1 ->
                when (p1) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        // 로그아웃 로직
                        // 세션 삭제하고 앱 공용 설정에 로그인 상태 false로 변경

                        // settings activity 변화
                        binding.settingsGuest.visibility = View.VISIBLE
                        binding.settingsUser.visibility = View.GONE

                        // toast 띄우기
                        Toast.makeText(this@SettingsActivity, "성공적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            builder.setPositiveButton("예", listener)
            builder.setNegativeButton("아니오", null)

            builder.show()
        }

        binding.settingsToDeleteAccount.setOnClickListener{
            val intent = Intent(this, DeleteAccountActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


        binding.settingsToDeleteData.setOnClickListener{
            val intent = Intent(this, DeleteDataActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }




        val spinner: Spinner = findViewById(R.id.spinner)
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.my_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }



    }


}