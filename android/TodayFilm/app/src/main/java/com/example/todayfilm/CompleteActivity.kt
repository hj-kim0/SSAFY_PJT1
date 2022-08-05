package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.todayfilm.databinding.ActivityCompleteBinding

class CompleteActivity : AppCompatActivity() {
    var mainImage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = FrameFragment()
        val bundle = Bundle()
        bundle.putString("parent", "complete")
        fragment.arguments = bundle
        transaction.add(R.id.fragment_content_complete, fragment)
        transaction.commit()

        MyPreference.writeInt(this, "mainImage", 0)

        binding.completeBtn.setOnClickListener {
            mainImage = MyPreference.readInt(this, "mainImage")

            if (mainImage == 0) {
                Toast.makeText(this, "대표 사진을 선택해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                // 서버로 데이터 전송

                // 내부 저장소에 오늘 필름 완성했다는 정보 남기기
                MyPreference.writeInt(this, "isComplete", 1)

                // 응답 받은 후 토스트 띄우고 main 액티비티로 이동
                Toast.makeText(this, "성공적으로 기록되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }
}