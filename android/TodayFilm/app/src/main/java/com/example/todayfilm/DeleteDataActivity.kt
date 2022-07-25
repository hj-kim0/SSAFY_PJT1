package com.example.todayfilm

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.todayfilm.databinding.ActivityChangePasswordBinding
import com.example.todayfilm.databinding.ActivityDeleteDataBinding

class DeleteDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDeleteDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.deleteDataBtn.setOnClickListener{
            val check = binding.deleteDataCheck.text.toString()

            if (check == "데이터 초기화 확인") {
                binding.deleteDataCheckErr.visibility = View.INVISIBLE
                val builder = AlertDialog.Builder(this)
                builder.setTitle("정말 삭제하시겠습니까?")
                builder.setMessage("기기에 저장된 모든 데이터가 삭제되어 복구할 수 없습니다.")

                val listener = DialogInterface.OnClickListener{ _, p1 ->
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            // 데이터 초기화 로직
                            // DB 초기화, 이미지, 동영상 제거 등

                            // toast 띄우기
                            Toast.makeText(this@DeleteDataActivity, "데이터 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                            // activity 변경
                            val intent = Intent(this, IntroActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                    }
                }

                builder.setPositiveButton("예", listener)
                builder.setNegativeButton("아니오", null)

                builder.show()
            } else {
                binding.deleteDataCheckErr.visibility = View.VISIBLE
            }
        }
    }
}