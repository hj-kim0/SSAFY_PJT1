package com.example.todayfilm

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.todayfilm.data.Imgvid
import com.example.todayfilm.databinding.FragmentFrameBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class FrameFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentFrameBinding
    var parent: String? = null
    var articleidx: String? = null
    var articlecreatedate: String? = null
    var article_userpid: String? = null
    var imgcount = 0
    lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrameBinding.inflate(inflater, container, false)
        parent = arguments?.getString("parent")
        articleidx = arguments?.getString("articleidx")
        articlecreatedate = arguments?.getString("articlecreatedate")
        article_userpid = arguments?.getString("article_userpid")

        sharedPreferences = requireActivity().getSharedPreferences(MyPreference.sp_name, Context.MODE_PRIVATE)

        // 부모가 home이나 complete라면 내부 저장소의 데이터 가져와서 보여주기
        if (parent == "home" || parent == "complete") {
            // 내부 저장소에 저장된 사진 정보 확인
            val prev = MyPreference.read(requireActivity(), "imgvids") // 내부 저장소에 저장된 string(json)
            val gson = GsonBuilder().create()
            val groupListType: Type = object: TypeToken<ArrayList<Imgvid?>?>() {}.type // json을 객체로 바꿀 때 타입 추론을 위함
            val tempArray = ArrayList<Imgvid>()

            // 데이터가 비어있지 않다면 객체 수 확인해서 imgcount값 지정하고 tempArray에 기존 데이터 추가
            if (prev != "none" && prev != "" && prev != "[]") {
                tempArray.addAll(gson.fromJson(prev, groupListType))
            }

//            imgcount = MyPreference.readInt(requireActivity(), "imgcount")
            imgcount = tempArray.size



            // 데이터 바인딩
            if (imgcount > 0) {
                binding.imgvid1 = tempArray[0]
            }
            if (imgcount > 1) {
                binding.imgvid2 = tempArray[1]
            }
            if (imgcount > 2) {
                binding.imgvid3 = tempArray[2]

            }
            if (imgcount > 3) {
                binding.imgvid4 = tempArray[3]
            }

            // 설정에서 반복하기로 한 경우
            val isRepeat = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getBoolean("repeat", true)
            if (parent == "complete" && isRepeat && imgcount < 4) {
                when (imgcount) {
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

            binding.date = articlecreatedate

        } else {
            Log.d("확인1", articleidx.toString())
            Log.d("확인2", articlecreatedate.toString())
            Log.d("확인3", article_userpid.toString())

            // 부모가 film이라면 넘겨받은 정보로 데이터 보여주기
            val imgview1 = binding.image1Photo
            Glide.with(requireActivity()).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/article/${article_userpid}/${articlecreatedate}/1.png").into(imgview1)
            val imgview2 = binding.image2Photo
            Glide.with(requireActivity()).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/article/${article_userpid}/${articlecreatedate}/2.png").into(imgview2)
            val imgview3 = binding.image3Photo
            Glide.with(requireActivity()).load("http://i7c307.p.ssafy.io:8080/harufilm/upload/article/${article_userpid}/${articlecreatedate}/3.png").into(imgview3)
            val imgview4 = binding.image4Photo
            Glide.with(requireActivity()).load("http://i7c407.p.ssafy.io:8080/harufilm/upload/article/${article_userpid}/${articlecreatedate}/4.png").into(imgview4)

            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val temp = LocalDate.parse(articlecreatedate, formatter)
            val changed = temp.format(DateTimeFormatter.ofPattern("yyyy/MM/dd (E)"))

            binding.date = changed
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 부모가 complete면 대표 이미지 지정할 수 있게 함
        if (parent == "complete") {
            setOnClickListener()
        }

        var isPlay = false

        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.getIsPlay().observe(
            requireActivity(),
        ) {
            isPlay = it
        }

        if (isPlay) {
            binding.image1Photo.visibility = View.GONE
            binding.image2Photo.visibility = View.GONE
            binding.image3Photo.visibility = View.GONE
            binding.image4Photo.visibility = View.GONE

            playVideo(binding.image1Video, 1)
            playVideo(binding.image2Video, 2)
            playVideo(binding.image3Video, 3)
            playVideo(binding.image4Video, 4)
            sharedViewModel.setIsPlay(false)
        }
    }

    private fun setOnClickListener() {
        if (imgcount > 0) {
            binding.image1Section.setOnClickListener(this)
        }
        if (imgcount > 1) {
            binding.image2Section.setOnClickListener(this)
        }
        if (imgcount > 2) {
            binding.image3Section.setOnClickListener(this)
        }
        if (imgcount > 3) {
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
                MyPreference.writeInt(requireActivity(), "articlethumbnail", 1)
            }
            R.id.image2_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                binding.image3Section.foreground = null
                binding.image4Section.foreground = null
                MyPreference.writeInt(requireActivity(), "articlethumbnail", 2)
            }
            R.id.image3_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = null
                binding.image3Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                binding.image4Section.foreground = null
                MyPreference.writeInt(requireActivity(), "articlethumbnail", 3)
            }
            R.id.image4_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = null
                binding.image3Section.foreground = null
                binding.image4Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                MyPreference.writeInt(requireActivity(), "articlethumbnail", 4)
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
        val videoUri = Uri.parse("http://i7c207.p.ssafy.io:8080/harufilm/upload/article/${article_userpid}/${articlecreatedate}/${vidnum}.mp4")

        // 동영상 주소 지정
        videoView.setVideoURI(videoUri)

        // 동영상 재생
        videoView.setOnPreparedListener {
            videoView.start()
        }
    }
}