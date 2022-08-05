package com.example.todayfilm

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.todayfilm.databinding.ActivityCompleteBinding
import kotlinx.android.synthetic.main.activity_complete.*
import kotlinx.android.synthetic.main.fragment_film.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

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
            val hashtags = binding.completeHashtag.insertTag

            mainImage = MyPreference.readInt(this, "mainImage")

            mainImage =  1


            if (mainImage == 0) {
                Toast.makeText(this, "대표 사진을 선택해주세요!", Toast.LENGTH_SHORT).show()
            } else {
                // 서버로 데이터 전송

                // 내부 저장소에 오늘 필름 완성했다는 정보 남기기
                MyPreference.writeInt(this, "isComplete", 1)



                if(complete_save.isChecked){

                    val duration = Toast.LENGTH_SHORT
                    val bitmap = Bitmap.createBitmap(fragment_content_complete.getWidth(), fragment_content_complete.getHeight(), (Bitmap.Config.ARGB_8888));
                    val canvas = Canvas(bitmap);
                    val bgDrawable = fragment_content_complete.getBackground();
                    if (bgDrawable != null) {
                        bgDrawable.draw(canvas);
                    } else {
                        canvas.drawColor(Color.WHITE);
                    }
                    fragment_content_complete.draw(canvas);



                    var fos: OutputStream? = null
                    var title = "이것은 당시 날짜이다."
                    // 3
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // 4
                        baseContext?.contentResolver?.also { resolver ->

                            // 5
                            val contentValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, "$title.png")
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                            }

                            // 6
                            val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                            // 7
                            fos = imageUri?.let { resolver.openOutputStream(it) }
                        }
                    } else {
                        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        val image = File(imagesDir, "$title.png")
                        fos = FileOutputStream(image)
                    }

                    fos?.use {

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        Toast.makeText(baseContext, "성공적으로 저장되었습니다..", duration).show()
                    }




                }



                // 응답 받은 후 토스트 띄우고 main 액티비티로 이동
                Toast.makeText(this, "성공적으로 기록되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }
}