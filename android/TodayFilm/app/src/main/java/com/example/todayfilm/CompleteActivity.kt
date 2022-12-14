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
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.example.todayfilm.data.FindPwResponse
import com.example.todayfilm.databinding.ActivityCompleteBinding
import com.example.todayfilm.retrofit.NetWorkClient
import kotlinx.android.synthetic.main.activity_complete.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class CompleteActivity : AppCompatActivity() {
    private var thumbnail = 0

    // 로딩 화면
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val date = MyPreference.read(this, "date")
        val filename = date.substring(0, 8)

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = FrameFragment()
        val bundle = Bundle()
        bundle.putString("parent", "complete")
        bundle.putString("articlecreatedate", date)
        fragment.arguments = bundle
        transaction.add(R.id.fragment_content_complete, fragment)
        transaction.commit()

        MyPreference.writeInt(this, "articlethumbnail", 0)

        binding.completeBtn.setOnClickListener {
            val hashtags = binding.completeHashtag.insertTag
            var isHashtagsOK = true
            var isComma = false
            var hashstring = ""

            // 해시태그 유효성 검사
            if (hashtags.size < 0 || hashtags.size > 4) {
                isHashtagsOK = false
            }
            for (hashtag in hashtags) {
                if (hashtag.length < 0 || hashtag.length > 32) {
                    isHashtagsOK = false
                    break
                } else if (hashtag.contains(',')) {
                    isComma = true
                    break
                } else {
                    hashstring += ",$hashtag"
                }
            }

            thumbnail = MyPreference.readInt(this, "articlethumbnail")

            if (thumbnail == 0) {
                Toast.makeText(this, "대표 사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else if (!isHashtagsOK) {
                Toast.makeText(this, "해시태그가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            } else if (isComma) {
                Toast.makeText(this, "해시태그에 쉼표는 입력할 수 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog = LoadingDialog(this)
                loadingDialog.show()

                // 해시태그 스트링의 첫 번째 쉼표 제거
                if (hashtags.isNotEmpty()) {
                    hashstring = hashstring.substring(1)
                }

                // 공개 여부 확인
                val shareBtn = binding.completeShareGroup.checkedRadioButtonId
                var share = 0

                when (shareBtn) {
                    R.id.complete_share_all -> {
                        share = 0
                    }
                    R.id.complete_share_followers -> {
                        share = 1
                    }
                    R.id.complete_share_nobody -> {
                        share = 2
                    }
                }

                // 서버로 데이터 전송
                val imgcount = MyPreference.readInt(this, "imgcount")

                // 이미지 데이터
                val images = ArrayList<MultipartBody.Part>()
                for (index in 1..imgcount) {
                    val image = File(this.getExternalFilesDir(null), "${index}.jpg")
                    val body = RequestBody.create(MediaType.parse("image/*"), image)
                    images.add(MultipartBody.Part.createFormData("imgdata", image.name, body))
                }

                // 비디오 데이터
                val videos = ArrayList<MultipartBody.Part>()
                for (index in 1..imgcount) {
                    val video = File(this.getExternalFilesDir(null), "${index}.mp4")
                    val body = RequestBody.create(MediaType.parse("video/*"), video)
                    videos.add(MultipartBody.Part.createFormData("videodata", video.name, body))
                }

                val isRepeat = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("repeat", true)

                // 게시글 반복 저장
                if (imgcount in 1..3) {
                    if (isRepeat) {
                        for (index in 1..4-imgcount) {
                            if (imgcount == 2 && index == 2) {
                                val image = File(this.getExternalFilesDir(null), "2.jpg")
                                val body = RequestBody.create(MediaType.parse("image/*"), image)
                                images.add(MultipartBody.Part.createFormData("imgdata", "4.jpg", body))

                                val video = File(this.getExternalFilesDir(null), "2.mp4")
                                val body1 = RequestBody.create(MediaType.parse("video/*"), video)
                                videos.add(MultipartBody.Part.createFormData("videodata", "4.mp4", body1))
                            } else {
                                val image = File(this.getExternalFilesDir(null), "1.jpg")
                                val body = RequestBody.create(MediaType.parse("image/*"), image)
                                images.add(MultipartBody.Part.createFormData("imgdata", "${imgcount + index}.jpg", body))

                                val video = File(this.getExternalFilesDir(null), "1.mp4")
                                val body1 = RequestBody.create(MediaType.parse("video/*"), video)
                                videos.add(MultipartBody.Part.createFormData("videodata", "${imgcount + index}.mp4", body1))
                            }
                        }
                    } else {
                        val imageinputstream = resources.openRawResource(R.raw.blankimage)
                        val imagetemp = File.createTempFile("imagetemp", ".jpg")
                        imagetemp.deleteOnExit()
                        imagetemp.outputStream().use { fileOutputStream ->
                            imageinputstream.copyTo(fileOutputStream)
                        }
                        val body = RequestBody.create(MediaType.parse("image/*"), imagetemp)

                        val videoinputstream = resources.openRawResource(R.raw.blankvideo)
                        val videotemp = File.createTempFile("videotemp", ".mp4")
                        videotemp.deleteOnExit()
                        videotemp.outputStream().use { fileOutputStream ->
                            videoinputstream.copyTo(fileOutputStream)
                        }
                        val body1 = RequestBody.create(MediaType.parse("video/*"), videotemp)

                        for (index in 1..4-imgcount) {
                            images.add(MultipartBody.Part.createFormData("imgdata", "${imgcount + index}.jpg", body))
                            videos.add(MultipartBody.Part.createFormData("videodata", "${imgcount + index}.mp4", body1))
                        }
                    }
                }

                // 게시글 작성자
                val pid = MyPreference.read(this, "userpid")
                val userpid = RequestBody.create(MediaType.parse("text/plain"), pid)

                // 미리보기에 사용될 썸네일 이미지 번호
                val articlethumbnail = RequestBody.create(MediaType.parse("text/plain"), thumbnail.toString())

                // 공개 여부
                val articleshare = RequestBody.create(MediaType.parse("text/plain"), share.toString())

                // 해시태그
                val hashlist = RequestBody.create(MediaType.parse("text/plain"), hashstring)

                // 전송
                val call = NetWorkClient.GetNetwork.createarticle(images, videos, userpid, articlethumbnail, articleshare, hashlist)
                call.enqueue(object : Callback<FindPwResponse> {

                    override fun onResponse(call: Call<FindPwResponse>, response: Response<FindPwResponse>) {
                        when (thumbnail) {
                            1 -> {
                                findViewById<FrameLayout>(R.id.image1_section).foreground = null
                            }

                            2 -> {
                                findViewById<FrameLayout>(R.id.image2_section).foreground = null
                            }

                            3 -> {
                                findViewById<FrameLayout>(R.id.image3_section).foreground = null
                            }

                            4 -> {
                                findViewById<FrameLayout>(R.id.image4_section).foreground = null
                            }
                        }

                        // 기기에 저장
                        if (complete_save.isChecked) {
                            val bitmap = Bitmap.createBitmap(fragment_content_complete.width, fragment_content_complete.height, (Bitmap.Config.ARGB_8888))
                            val canvas = Canvas(bitmap)
                            val bgDrawable = fragment_content_complete.background
                            if (bgDrawable != null) {
                                bgDrawable.draw(canvas)
                            } else {
                                canvas.drawColor(Color.WHITE)
                            }
                            fragment_content_complete.draw(canvas)

                            var fos: OutputStream? = null
                            // 3
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                // 4
                                baseContext?.contentResolver?.also { resolver ->
                                    // 5
                                    val contentValues = ContentValues().apply {
                                        put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.jpg")
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
                                val image = File(imagesDir, "$filename.jpg")
                                fos = FileOutputStream(image)
                            }

                            fos?.use {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                                Toast.makeText(baseContext, "성공적으로 저장되었습니다..", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // 내부 저장소에 필름 완성했다는 정보 남기기
                        MyPreference.writeInt(applicationContext, "isComplete", 1)

                        // 로딩 화면 끝
                        loadingDialog.dismiss()

                        // 응답 받은 후 토스트 띄우고 profile로 이동
                        Toast.makeText(applicationContext, "성공적으로 기록되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.putExtra("parent", "complete")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }

                    override fun onFailure(call: Call<FindPwResponse>, t: Throwable) {
                        // 로딩 화면 끝
                        loadingDialog.dismiss()

                        Toast.makeText(applicationContext, "게시글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("게시글 작성 실패", t.message.toString())
                    }
                })
            }
        }
    }
}