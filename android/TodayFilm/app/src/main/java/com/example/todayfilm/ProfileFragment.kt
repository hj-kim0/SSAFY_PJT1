package com.example.todayfilm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.todayfilm.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentProfileBinding
    var userid = ""
    var username = ""
    var userdesc = ""
    var isMyProfile = true
    var isFollow = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        binding.profileId.text = userid

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        userid = MyPreference.read(requireContext(), "userid")
        username = MyPreference.read(requireContext(), "username")
        userdesc = MyPreference.read(requireContext(), "userdesc")

        if (username != ""){
            binding.profileUsername.text = username
        }
        if (userdesc != ""){
            binding.profileDescription.text = userdesc
        }

        val imgview = binding.profileImage
        Glide.with(requireActivity()).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/profile/baseimg.png").into(imgview)

        // 본인 프로필인지 확인 후 isMyProfile과 profile_btn 텍스트 변경, profile_to_settings visiblity 변경
        if (isMyProfile) {
            binding.profileBtn.text = "회원정보 수정"
            binding.profileToSettings.visibility = View.VISIBLE
        } else {
            // 본인 프로필이 아니라면 팔로우 중인지 확인
            binding.profileToSettings.visibility = View.INVISIBLE
            if (isFollow) {
                binding.profileBtn.text = "언팔로우"
            } else {
                binding.profileBtn.text = "팔로우"
            }
        }

        setOnClickListener()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.profileToSettings.setOnClickListener(this)
        binding.profileBtn.setOnClickListener(this)
        binding.profileFilmImage1.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.profile_film_image_1 -> {
                (activity as MainActivity).changeFragment(3)
            }

            R.id.profile_to_settings -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            R.id.profile_btn -> {
                // 본인 프로필인 경우 change profile 액티비티로 이동
                if (isMyProfile) {
                    val intent = Intent(activity, ChangeProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    // 팔로우 중인지 확인 후 서버로 팔로우 / 언팔로우 요청 보내기
                    if (isFollow) {
                        // 언팔로우 요청
                        Log.d("확인용", "언팔로우")
                    } else {
                        // 팔로우 요청
                        Log.d("확인용", "팔로우")
                    }
                }
            }
        }
    }
}

