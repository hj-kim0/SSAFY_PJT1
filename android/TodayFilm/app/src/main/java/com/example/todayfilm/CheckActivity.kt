package com.example.todayfilm

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.Rotation
import com.daasuu.mp4compose.composer.Mp4Composer
import com.example.todayfilm.data.Imgvid
import com.example.todayfilm.databinding.ActivityCheckBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Type
import kotlin.collections.ArrayList

class CheckActivity : AppCompatActivity() {
    private val TAG = "테스트용 로그"
    private var srcPath = ""
    private lateinit var resultFile: File
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        // 촬영한 영상 경로 및 확장자 제외한 파일명
        srcPath = intent.getStringExtra("path").toString()
        var name = srcPath.substring(srcPath.lastIndexOf(File.separator)+1)
        name = name.substring(0 until (name.length - 4))

        // 결과가 저장될 경로
        val destPath = this.externalCacheDir.toString() + "/${name}_trimmed.mp4"

        // 영상 길이 구해서 시작 시간, 종료 시간 구하기
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(srcPath)
        val endTime =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong()
        val startTime = if (endTime!! >= 3000) {
            endTime - 3000
        }  else {
            0
        }

        // 영상 길이 자르기
        Mp4Composer(srcPath, destPath)
            .rotation(Rotation.ROTATION_270)
            .size(852, 480)
            .fillMode(FillMode.PRESERVE_ASPECT_FIT)
            .trim(startTime, endTime)
            .listener(object : Mp4Composer.Listener {
                override fun onProgress(progress: Double) {
                    Log.d(TAG, "onProgress = $progress")
                }

                override fun onCurrentWrittenVideoTime(timeUs: Long) {
                }

                override fun onCompleted() {
                    runOnUiThread {
                        // 영상 미리보기
                        binding.videoView.setVideoPath(destPath)
                        binding.videoView.setMediaController(MediaController(this@CheckActivity))

                        // 로딩 화면 끝
                        loadingDialog.dismiss()

                        binding.videoView.start()
                    }
                }

                override fun onCanceled() {
                    File(srcPath).delete()
                    loadingDialog.dismiss()
                    Toast.makeText(this@CheckActivity, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailed(exception: Exception) {
                    File(srcPath).delete()
                    loadingDialog.dismiss()
                    Toast.makeText(this@CheckActivity, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            })
            .start()

        // 3초로 잘린 영상
        resultFile = File(destPath)

        // 다시시도 버튼
        binding.cameraRetry.setOnClickListener {
            // 소스 파일 및 결과 영상 제거
            File(srcPath).delete()
            resultFile.delete()

            // camera activity로 이동
            val intent = Intent(this, CameraActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        // 확인 버튼
        binding.cameraOk.setOnClickListener {
            binding.cameraOk.isClickable = false
            loadingDialog.show()

            // 내부 저장소에 저장된 사진 정보 확인
            val prev = MyPreference.read(this, "imgvids") // 내부 저장소에 저장된 string(json)
            val gson = GsonBuilder().create()
            val groupListType: Type = object: TypeToken<ArrayList<Imgvid?>?>() {}.type // json을 객체로 바꿀 때 타입 추론을 위함
            val tempArray = ArrayList<Imgvid>()

            // 데이터가 비어있지 않다면 객체 수 확인해서 imgnumber값 지정하고 tempArray에 기존 데이터 추가
            var imgcount = MyPreference.readInt(this, "imgcount")
            if (imgcount != 0 && prev != "" && prev != "[]") {
                tempArray.addAll(gson.fromJson(prev, groupListType))
            }
            imgcount += 1
            MyPreference.writeInt(this, "imgcount", imgcount)

            // 결과 영상을 cache에서 file 디렉토리로 이름 바꿔 옮기기
            val movedFile = File(this.getExternalFilesDir(null), "${imgcount}.mp4")

            resultFile.copyTo(movedFile)
            resultFile.delete()

            // 영상의 마지막 프레임 추출해서 같은 위치에 같은 이름으로 저장
            var bitmap = mediaMetadataRetriever.getFrameAtTime(endTime * 1000)
            val rotateMatrix = Matrix()
            rotateMatrix.postRotate(270F)
            bitmap = Bitmap.createBitmap(bitmap!!, 0,0, bitmap.width, bitmap.height, rotateMatrix, false)
            bitmap = Bitmap.createScaledBitmap(bitmap, 852, 480, true)
            val imageFile = File(this.getExternalFilesDir(null), "${imgcount}.jpg")
            val fileOutputStream = FileOutputStream(imageFile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            bitmap.recycle()
            fileOutputStream.flush()

            // 저장할 Imgvid 객체 생성
            val data = Imgvid()
            data.imgpath = imageFile.absolutePath
            data.vidpath = movedFile.absolutePath

            // tempArray에 객체 추가하고 내부 저장소에 ArrayList를 String으로 변경해서 저장
            tempArray.add(data)
            val strList = gson.toJson(tempArray, groupListType)
            MyPreference.write(this, "imgvids", strList)

            // 소스 파일 지우기
            File(srcPath).delete()

            // 홈으로 이동
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // 소스 파일 및 결과 영상 제거
        File(srcPath).delete()
        resultFile.delete()

        // camera activity로 이동
        val intent = Intent(this, CameraActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog.dismiss()

        this.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}
