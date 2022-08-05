package com.example.todayfilm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.todayfilm.databinding.ActivityCameraBinding
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class CameraActivity : AppCompatActivity() {
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"
    // 파일명
    private var path = ""

    // 권한 관련
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS =
        mutableListOf (
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    // Camera2 관련
    private lateinit var cameraId: String
    private var faceCamera = false
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null

    // 방향 관련
    private val ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 90)
        append(Surface.ROTATION_90, 0)
        append(Surface.ROTATION_180, 270)
        append(Surface.ROTATION_270, 180)
    }

    // 저장 관련
    private var width: Int = 0
    private var height: Int = 0
    private var isRecording = false
    private lateinit var mediaRecorder: MediaRecorder






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            binding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                    // TextureListener 에서 SurfaceTexture 가 사용가능한 경우, openCamera() 메서드를 호출한다
                    openCamera()
                }

                override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                    // 지정된 SurfaceTexture 를 파괴하고자 할 때 호출된다
                    // false 를 반환하면 SurfaceTexture#release() 를 호출해야 한다
                    return false
                }

                override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {}

                override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {}
            }
        } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.cameraBtn.setOnClickListener{ stopRecordingVideo(false) }
        binding.cameraSwitch.setOnClickListener{
            if (cameraDevice != null) {
                faceCamera = !faceCamera
                cameraDevice!!.close()
                stopRecordingVideo(true)
                File(path).delete()
                openCamera()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                openCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun openCamera() {
        // 카메라 장치를 탐지하고, characteristics 값 받아오고, 연결하기 위해 CameraManager 객체를 가져온다
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            // CameraManager 에서 cameraIdList 의 값을 가져온다
            // FaceCamera 값이 true면 전면, 아니면 후면 카메라
            cameraId = if (faceCamera) {
                cameraManager.cameraIdList[1]
            } else {
                cameraManager.cameraIdList[0]
            }

            // cameraId로 받아온 값
            val characteristics: CameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            // SurfaceTexture에 사용할 Size 값을 map에서 가져와 imageDimension 에 할당해준다
            val videoDimensions = map!!.getOutputSizes(MediaRecorder::class.java)

            for (dimension in videoDimensions) {
                val ratio = abs(dimension.width.toDouble() / dimension.height)
                if (1.77 < ratio && ratio < 1.78 ) {
                    width = dimension.width
                    height = dimension.height
                    break
                }
            }

            Log.d("너비", width.toString())
            Log.d("높이", height.toString())

            // 카메라를 열기전에 카메라 권한이 있는지 확인한다
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                return
            }

            // cameraId에 해당하는 카메라 실행
            // 이때, stateCallback 은 카메라를 실행할때 호출되는 콜백메서드이며, cameraDevice 에 값을 할달해주고, 카메라 미리보기를 생성한다
            cameraManager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onOpened(camera: CameraDevice) {
            // 카메라가 제대로 열린 경우
            // cameraDevice 리소스를 해지할때 해당 cameraDevice 객체의 참조가 필요하므로, 인자로 들어온 camera 값을 전역변수 cameraDevice에 넣어 준다
            cameraDevice = camera

            // 동영상 촬영 시작
            startRecordingVideo()
        }

        override fun onDisconnected(camera: CameraDevice) {
            // 카메라 연결 해제된 경우
            cameraDevice!!.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            // 에러가 발생한 경우
            // cameraDevice를 닫고 전역변수 cameraDevice에 null 할당
            cameraDevice!!.close()
            cameraDevice = null
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupMediaRecorder() {
        // mediaRecorder 객체 생성
        mediaRecorder = MediaRecorder()

        // 파일 생성 준비
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREA)
            .format(System.currentTimeMillis())
        val file = File(externalCacheDir, "${name}.mp4")
        path = file.path

        // 영상의 rotation
        val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display!!.rotation
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.rotation
        }

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFile(file)
        mediaRecorder.setOrientationHint(ORIENTATIONS.get(rotation))
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P))

        mediaRecorder.prepare()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startRecordingVideo() {
        if (cameraDevice == null || !textureView.isAvailable ) {
            return
        }

        try {
            // 동영상을 어디에, 어떤 형식으로 인코딩해서 저장할지 등을 설정
            setupMediaRecorder()

            // 미리보기를 출력할 Surface 준비
            val textureView = findViewById<TextureView>(R.id.textureView)
            val surfaceTexture = textureView.surfaceTexture
            surfaceTexture!!.setDefaultBufferSize(width, height)
            val previewSurface = Surface(surfaceTexture)

            // 출력 Surface 지정: 미리보기, 영상 녹화
            val outputSurfaces = ArrayList<Surface>(2)
            outputSurfaces.add(previewSurface)
            outputSurfaces.add(mediaRecorder.surface)

            // 미리보기와 영상 녹화를 요청하는 RequestBuilder 생성
            val recordRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            recordRequestBuilder.addTarget(previewSurface)
            recordRequestBuilder.addTarget(mediaRecorder.surface)
            recordRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

            cameraDevice!!.createCaptureSession(outputSurfaces, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    if (cameraDevice == null) {
                        return
                    }

                    cameraCaptureSession = session

                    try {
                        // session이 준비되면 미리보기 출력과 영상 녹화 시작
                        cameraCaptureSession!!.setRepeatingRequest(recordRequestBuilder.build(), null, null)
                        isRecording = true
                        mediaRecorder.start()
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Toast.makeText(this@CameraActivity, "Configuration Changed", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun stopRecordingVideo(isSwitch: Boolean) {
        isRecording = false

        mediaRecorder.stop()
        mediaRecorder.reset()

        if (!isSwitch) {
            moveToCheck()
        }
    }

    private fun moveToCheck() {
        val intent = Intent(this, CheckActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("path", path)
        startActivity(intent)
        finish()
    }
}