package com.example.todayfilm

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.todayfilm.data.Imgvid
import com.example.todayfilm.databinding.FragmentFrameBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.collections.ArrayList

class FrameFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentFrameBinding
    var parent: String? = null
    var imgnumber = 0
    lateinit var sharedPreferences: SharedPreferences
    var play_userpid: Int? = null
    var play_articlecreatedate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrameBinding.inflate(inflater, container, false)
        parent = arguments?.getString("parent")

        sharedPreferences = requireActivity().getSharedPreferences(MyPreference.sp_name, Context.MODE_PRIVATE)

        // 부모가 home이나 complete라면 내부 저장소의 데이터 가져와서 보여주기
        if (parent == "home" || parent == "complete") {
            // 내부 저장소에 저장된 사진 정보 확인
            val prev = MyPreference.read(requireActivity(), "imgvids") // 내부 저장소에 저장된 string(json)
            val gson = GsonBuilder().create()
            val groupListType: Type = object: TypeToken<ArrayList<Imgvid?>?>() {}.type // json을 객체로 바꿀 때 타입 추론을 위함
            val tempArray = ArrayList<Imgvid>()

            // 데이터가 비어있지 않다면 객체 수 확인해서 imgnumber값 지정하고 tempArray에 기존 데이터 추가
            if (prev != "none" && prev != "" && prev != "[]") {
                tempArray.addAll(gson.fromJson(prev, groupListType))
                imgnumber += tempArray.size
            }

            // 데이터 바인딩
            if (imgnumber > 0) {
                binding.imgvid1 = tempArray[0]
            }
            if (imgnumber > 1) {
                binding.imgvid2 = tempArray[1]
            }
            if (imgnumber > 2) {
                binding.imgvid3 = tempArray[2]
            }
            if (imgnumber > 3) {
                binding.imgvid4 = tempArray[3]
            }

            // 설정에서 반복하기로 한 경우
            val isRepeat = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getBoolean("repeat", true)
            if (parent == "complete" && isRepeat && imgnumber < 4) {
                when (imgnumber) {
                    1 -> {
                        binding.imgvid1 = tempArray[0]
                        binding.imgvid2 = tempArray[0]
                        binding.imgvid3 = tempArray[0]
                        binding.imgvid4 = tempArray[0]
                    }

                    2 -> {
                        binding.imgvid1 = tempArray[0]
                        binding.imgvid2 = tempArray[1]
                        binding.imgvid3 = tempArray[0]
                        binding.imgvid4 = tempArray[1]
                    }

                     3 -> {
                         binding.imgvid1 = tempArray[0]
                         binding.imgvid2 = tempArray[1]
                         binding.imgvid3 = tempArray[2]
                         binding.imgvid4 = tempArray[0]
                     }
                }
            }

            binding.date = MyPreference.read(requireActivity(), "date")

        } else {
            // 부모가 film이라면 넘겨받은 정보로 서버 데이터 보여주기
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 부모가 complete면 대표 이미지 지정할 수 있게 함
        if (parent == "complete") {
            setOnClickListener()
        }

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.getLiveText().observe(requireActivity(), androidx.lifecycle.Observer {
            play_articlecreatedate = it.substring(0, 8)
            play_userpid = it.substring(8).toInt()

            if (play_userpid!! > 0 && play_articlecreatedate!!.length > 0) {
                binding.image1Photo.visibility = View.GONE
                binding.image2Photo.visibility = View.GONE
                binding.image3Photo.visibility = View.GONE
                binding.image4Photo.visibility = View.GONE

                playVideo(binding.image1Video, 1)
                playVideo(binding.image2Video, 2)
                playVideo(binding.image3Video, 3)
                playVideo(binding.image4Video, 4)
            }
        })
    }

    private fun setOnClickListener() {
        if (imgnumber > 0) {
            binding.image1Section.setOnClickListener(this)
        }
        if (imgnumber > 1) {
            binding.image2Section.setOnClickListener(this)
        }
        if (imgnumber > 2) {
            binding.image3Section.setOnClickListener(this)
        }
        if (imgnumber > 3) {
            binding.image4Section.setOnClickListener(this)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.image1_section -> {
                binding.image1Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                binding.image2Section.foreground = null
                binding.image3Section.foreground = null
                binding.image4Section.foreground = null
                MyPreference.writeInt(requireActivity(), "mainImage", 1)
            }
            R.id.image2_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                binding.image3Section.foreground = null
                binding.image4Section.foreground = null
                MyPreference.writeInt(requireActivity(), "mainImage", 2)
            }
            R.id.image3_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = null
                binding.image3Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                binding.image4Section.foreground = null
                MyPreference.writeInt(requireActivity(), "mainImage", 3)
            }
            R.id.image4_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = null
                binding.image3Section.foreground = null
                binding.image4Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                MyPreference.writeInt(requireActivity(), "mainImage", 4)
            }
        }
    }

    // 설정 변경 이벤트 처리
    val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
        when (key) {
            "date" -> {
                binding.imgvid1 = null
                binding.imgvid2 = null
                binding.imgvid3 = null
                binding.imgvid4 = null
                binding.date = sharedPreferences!!.getString("date", "")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 리스너 등록
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener)
    }

    override fun onPause() {
        super.onPause()
        // 리스너 해제
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefListener)
    }

    fun playVideo(videoView: VideoView, vidnum: Int) {
        videoView.visibility = View.VISIBLE

        // 동영상 주소 준비
        val videoUri = Uri.parse("http://i7c207.p.ssafy.io:8080/harufilm/upload/article/${play_userpid}/${play_articlecreatedate}/${vidnum}.mp4")

        // 동영상 주소 지정
        videoView.setVideoURI(videoUri)

        // 동영상 재생
        videoView.setOnPreparedListener {
            videoView.start()
        }
    }
}