package com.example.todayfilm

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.FragmentProfileBinding
import com.example.todayfilm.retrofit.NetWorkClient
import com.google.android.gms.common.internal.service.Common.API
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

        Log.d("test1:", username + userdesc)
        binding.profileId.setText(userid)




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

        return binding.root
    }
    override fun onResume() {
        super.onResume()




















        userid = MyPreference.read(requireContext(), "userid")
        username = MyPreference.read(requireContext(), "username")
        userdesc = MyPreference.read(requireContext(), "userdesc")




        if (isMyProfile) {
            val pid = MyPreference.read(requireContext(), "userpid")
            Log.d("ㅇ?",pid)

            val profile = GetProfile()
            profile.userpid = pid

            val call = NetWorkClient.GetNetwork.getprofile(profile)

            //1
            call.enqueue(object : Callback<CompleteProfile>{

                override fun onResponse(
                    call: Call<CompleteProfile>,
                    response: Response<CompleteProfile>
                ) {
                    Log.d("체크3",response.body()?.userimg.toString())
                    Log.d("체크3",response.body()?.username.toString())
                    Log.d("체크3",response.body()?.userdesc.toString())

                    val imgview = binding.profileImageFile
                    Glide.with(requireActivity()).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/profile/"+response.body()?.userimg.toString()).into(imgview)
                    // img
                    binding.profileUsername.setText(response.body()?.username.toString())
                    binding.profileDescription.setText(response.body()?.userdesc.toString())

                    // id , desc
                    //3
                }
                override fun onFailure(call: Call<CompleteProfile>, t: Throwable) {

                } } )



            val article = GetArticle()
            Log.d("ㅇ",pid)
            article.userpid = pid
            article.search_userpid = pid

            val callArticle = NetWorkClient.GetNetwork.showarticle(article)

            callArticle.enqueue( object : Callback<List<ShowProfile>>{

                override fun onResponse(
                    call: Call<List<ShowProfile>>,
                    response: Response<List<ShowProfile>>
                ) {

                    val count = response.body()


                    val articleview = binding.profileFilmImage1
                    Glide.with(requireActivity()).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/article/"+ pid +
                            "/" + response.body()!!.get(0).articlecreatedate + "/" + response.body()!!.get(0).articlethumbnail +".png").into(articleview)

                    binding.calen.setText(response.body()!!.get(0).articlecreatedate.toString())


                }

                override fun onFailure(call: Call<List<ShowProfile>>, t: Throwable) {


                }

            })










        } else {
            val imgview = binding.profileImageFile
            Glide.with(requireActivity()).load("http://i7c207.p.ssafy.io:8080/harufilm/profileimg/baseimg.png").into(imgview)
            // 본인 프로필이 아니라면 팔로우 중인지 확인

            if (username != ""){
                binding.profileUsername.setText(username)
            }
            if (userdesc != ""){
                binding.profileDescription.setText(userdesc)
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
    }}
////////////////////////////////////////////