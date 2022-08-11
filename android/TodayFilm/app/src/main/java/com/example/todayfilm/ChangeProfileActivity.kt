package com.example.todayfilm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
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

class ChangeProfileActivity : AppCompatActivity() {
    val binding by lazy { ActivityChangeProfileBinding.inflate(layoutInflater) }
    lateinit var profileImage: ImageView
    var selectedImageUri: Uri? = null
    var pid = ""
    var changeProfilePath = ""


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
                Log.d("프로필 이미지", result?.userimg.toString())
                binding.changeProfileUsername.setText(result?.username)
                binding.changeProfileDescription.setText(result?.userdesc)
            }

            override fun onFailure(call: Call<CompleteProfile>, t: Throwable) {
                Log.d("사용자 정보 조회 실패", t.message.toString())
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
            val name = binding.changeProfileUsername.text.toString()
            val description = binding.changeProfileDescription.text.toString()

            Log.d("testtt", pid+name+description)
            Log.d("teststt", selectedImageUri.toString())

            val userpid = RequestBody.create(MediaType.parse("text/plain"), pid)
            val username = RequestBody.create(MediaType.parse("text/plain"), name)
            val userdesc = RequestBody.create(MediaType.parse("text/plain"), description)

            // 수정 필요 //////////////////////////////////////////////////////////////////////////
            // 사진을 변경했다면 이미지 전달

            /////////////////////////////////////////////////////////////////////////////////////

            if (name.isEmpty()) {
                binding.changeProfileErr.text = "닉네임은 비워둘 수 없습니다."
            } else {
                // 서버로 요청 보내기
                // 선택된 이미지가 없는경우
                if ( selectedImageUri==null ){

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
                        }

                        override fun onFailure(call: Call<ChangeUserDetailResponse>, t: Throwable) {
                            Log.d("회원 정보 수정 실패", t.message.toString())
                        }
                    })
                }else{
                    // 선택한 이미지가 있는경우
                    changeProfilePath = absolutelyPath(selectedImageUri!!)

                    val toimage = File(changeProfilePath)
                    val body = RequestBody.create(MediaType.parse("image/*"), toimage)
                    val image = MultipartBody.Part.createFormData("userimg", toimage.name, body)
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
                        }

                        override fun onFailure(call: Call<ChangeUserDetailResponse>, t: Throwable) {
                            Log.d("회원 정보 수정 실패", t.message.toString())
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
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
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
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
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

    fun absolutelyPath(path: Uri): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = contentResolver.query(path, proj, null, null, null)
        val index = c!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c.moveToFirst()
        val result = c.getString(index)
        return result
    }
}
