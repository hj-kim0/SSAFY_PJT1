package com.example.todayfilm

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.ActivityChangeProfileBinding
import com.example.todayfilm.retrofit.NetWorkClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ChangeProfileActivity : AppCompatActivity() {
    val binding by lazy { ActivityChangeProfileBinding.inflate(layoutInflater) }
    private lateinit var profileImage: ImageView
    private var selectedImageUri: Uri? = null
    private var pid = ""
    private var changeProfilePath = ""

    // 권한 관련
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        pid = MyPreference.read(this, "userpid")

        // 사용자 정보 조회
        val profile = GetProfile()
        profile.userpid = pid

        val callUser = NetWorkClient.GetNetwork.getprofile(profile)
        callUser.enqueue(object : Callback<CompleteProfile>{
            override fun onResponse(
                call: Call<CompleteProfile>,
                response: Response<CompleteProfile>
            ) {
                val result = response.body()
                val imgview = binding.changeProfileImage
                Glide.with(this@ChangeProfileActivity).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/profile/" + result?.userimg).into(imgview)
                binding.changeProfileUsername.setText(result?.username)
                binding.changeProfileDescription.setText(result?.userdesc)
            }

            override fun onFailure(call: Call<CompleteProfile>, t: Throwable) {
                Log.e("사용자 정보 조회 실패", t.message.toString())
            }
        })

        // 프로필 이미지 변경
        profileImage = binding.changeProfileImage

        profileImage.setOnClickListener {
            if (allPermissionsGranted()) {
                navigateGallery()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        binding.changeProfileBtn.setOnClickListener {
            val name = binding.changeProfileUsername.text.toString().trim()
            val description = binding.changeProfileDescription.text.toString().trim()

            val userpid = RequestBody.create(MediaType.parse("text/plain"), pid)
            val username = RequestBody.create(MediaType.parse("text/plain"), name)
            val userdesc = RequestBody.create(MediaType.parse("text/plain"), description)

            if (name.isEmpty()) {
                binding.changeProfileErr.text = "닉네임은 비워둘 수 없습니다."
            } else {
                // 서버로 요청 보내기
                // 선택된 이미지가 없는경우
                if ( selectedImageUri == null ) {
                    val call = NetWorkClient.GetNetwork.changeuserdetail2(userpid, username, userdesc)
                    call.enqueue(object : Callback<ChangeUserDetailResponse> {
                        override fun onResponse(
                            call: Call<ChangeUserDetailResponse>,
                            response: Response<ChangeUserDetailResponse>
                        ) {
                            Toast.makeText(this@ChangeProfileActivity, "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@ChangeProfileActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.putExtra("parent", "changeprofile")
                            startActivity(intent)
                            finish()
                        }

                        override fun onFailure(call: Call<ChangeUserDetailResponse>, t: Throwable) {
                            Toast.makeText(this@ChangeProfileActivity, "회원정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            Log.e("회원정보 수정 실패", t.message.toString())
                        }
                    })
                } else {
                    // 선택한 이미지가 있는 경우
                    changeProfilePath = absolutelyPath(selectedImageUri!!)

                    // 단축 400으로 리사이징 및 필요시 회전
                    val bitmap = BitmapFactory.decodeFile(changeProfilePath)
                    val rotatedBitmap = rotateImageIfRequired(this, bitmap, selectedImageUri!!)

                    var dstHeight = 400
                    var dstWidth = 400

                    if (rotatedBitmap!!.width > 400 && rotatedBitmap.height > 400) {
                        if (rotatedBitmap.width > rotatedBitmap.height) {
                            dstWidth = (rotatedBitmap.width * 400 / rotatedBitmap.height)
                        } else {
                            dstHeight = (rotatedBitmap.height * 400 / rotatedBitmap.width)
                        }
                    }

                    val scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap!!, dstWidth, dstHeight, true)
                    val imagetemp = File.createTempFile("profileimagetemp", ".jpg")
                    imagetemp.deleteOnExit()
                    val fileOutputStream = FileOutputStream(imagetemp)
                    scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)

                    val body = RequestBody.create(MediaType.parse("image/*"), imagetemp)
                    val image = MultipartBody.Part.createFormData("userimg", imagetemp.name, body)
                    val call = NetWorkClient.GetNetwork.changeuserdetail(image, userpid, username, userdesc)
                    call.enqueue(object : Callback<ChangeUserDetailResponse> {
                        override fun onResponse(
                            call: Call<ChangeUserDetailResponse>,
                            response: Response<ChangeUserDetailResponse>
                        ) {
                            Toast.makeText(this@ChangeProfileActivity, "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@ChangeProfileActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.putExtra("parent", "changeprofile")
                            startActivity(intent)
                            finish()
                        }

                        override fun onFailure(call: Call<ChangeUserDetailResponse>, t: Throwable) {
                            Toast.makeText(this@ChangeProfileActivity, "회원정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            Log.e("회원정보 수정 실패", t.message.toString())
                        }
                    })
                }
            }
        }

        binding.changeProfileToChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                navigateGallery()
            } else {
                Toast.makeText(this, "사용자가 권한을 허가하지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        // Image 컨텐츠만을 가져옴
        intent.type = "image/*"

        // 갤러리에서 이미지를 선택한 후, 갤러리에서 수행한 값을 받아오기
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 예외처리
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        // navigateGallery에서 지정한 requestCode일 경우
        when (requestCode) {
            2000 -> {
                selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    profileImage.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun absolutelyPath(path: Uri): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = contentResolver.query(path, proj, null, null, null)
        val index = c!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c.moveToFirst()
        val result = c.getString(index)
        c.close()
        return result
    }

    private fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(uri) ?: return null

        val exif = if (Build.VERSION.SDK_INT > 23) {
            ExifInterface(input)
        } else {
            ExifInterface(uri.path!!)
        }

        return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
