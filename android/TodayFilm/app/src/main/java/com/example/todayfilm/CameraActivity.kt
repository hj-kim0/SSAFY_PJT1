package com.example.todayfilm

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.todayfilm.databinding.ActivityCameraBinding
import com.homesoft.encoder.Muxer
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class CameraActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 카메라 권한 요청
        if ( allPermissionsGranted() ) {
            startCamera()

            // 동영상용 이미지 촬영 스레드 실행
            videoCaptureThread.start()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // 촬영 버튼
        binding.cameraBtn.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ( requestCode == REQUEST_CODE_PERMISSIONS ) {
            if ( allPermissionsGranted() ) {
                startCamera()
            } else {
                Toast.makeText(this, "카메라 사용 권한이 허가되지 않았습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // 카메라 라이프 사이클을 사용자 라이프 사이클에 bind
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // 미리보기
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // 후면 카메라를 기본값으로 지정
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 재바인드하기 전에 전부 언바인드
                cameraProvider.unbindAll()

                // 카메라 바인드
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "카메라 바인드 실패", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // 이미지 캡쳐 준비
        val imageCapture = imageCapture ?: return

        // 시간으로 파일명 및 경로 지정
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREA)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.P ) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TodayFilm")
            }
        }

        // 메타 데이터와 파일명을 포함한 출력 옵션 지정
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        // 이미지 캡쳐 실행
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "촬영 실패: ${exc}", exc)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "성공적으로 촬영되었습니다. ${outputFileResults.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
            }
        )

        // 저장된 사진 보여주고 다시시도/확인 보여주기
        videoCaptureFlag = false
        val bitmap = binding.viewFinder.bitmap
        binding.viewCapture.setImageBitmap(bitmap)
        Log.d(TAG, videoCaptureQueue.toString())

        binding.viewFinder.visibility = View.GONE
        binding.viewCapture.visibility = View.VISIBLE

        binding.cameraNav.visibility = View.VISIBLE
        binding.cameraBtn.visibility = View.GONE

        binding.cameraOk.setOnClickListener{
            // 사진 프레임에 채우는 로직
            // bitmap을 앱 공용 데이터 영역에 저장

            // 동영상 인코딩 및 저장 로직
//            val muxer = Muxer(this@CameraActivity, )

            // Main activity로 이동
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        binding.cameraRetry.setOnClickListener{
            // 이전과 같은 화면 보여주기
            binding.viewFinder.visibility = View.VISIBLE
            binding.viewCapture.visibility = View.GONE

            binding.cameraNav.visibility = View.GONE
            binding.cameraBtn.visibility = View.VISIBLE

            // videoCaptureQueue 초기화
            videoCaptureQueue.clear()

            // videoCaptureThread 재시작
            videoCaptureFlag = true
            videoCaptureThread.start()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "TodayFilm"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
        private val videoCaptureThread = thread(start = false) {
            while (videoCaptureFlag) {
                if (videoCaptureQueue.size > 30) {
                    videoCaptureQueue.poll()
                }
                videoCaptureQueue.add(binding.viewFinder.bitmap!!)
                Thread.sleep(100)
            }
        }
        private var videoCaptureFlag = true
        private var videoCaptureQueue: Queue<Bitmap> = LinkedList()
        private lateinit var binding: ActivityCameraBinding
    }
}