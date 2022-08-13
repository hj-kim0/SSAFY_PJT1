package com.example.todayfilm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.todayfilm.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentHomeBinding
    var imgcount = 0
    var isComplete = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 넣어서 frame 프래그먼트 호출
        val frameFragment = FrameFragment()
        val bundle = Bundle()
        bundle.putString("parent", "home")
        bundle.putString("articlecreatedate", MyPreference.read(requireActivity(), "date"))
        frameFragment.arguments = bundle

        childFragmentManager.beginTransaction().add(R.id.fragment_content_home, frameFragment).commit()
    }

    override fun onResume() {
        super.onResume()
        imgcount = MyPreference.readInt(requireActivity(), "imgcount")
        isComplete = MyPreference.readInt(requireActivity(), "isComplete")

        setOnClickListener()
    }

    private fun setOnClickListener() {
        // 사진 개수 및 완성 여부에 따라 home_to_complete 버튼 보여주기 여부 변경
        if (0 < imgcount && isComplete == 0) {
            binding.homeToComplete.visibility = View.VISIBLE
        } else {
            binding.homeToComplete.visibility = View.GONE
        }

        if (isComplete == 0) {
            when (imgcount) {
                0 -> {
                    binding.fragmentContentHome.setOnClickListener(this)
                }

                in 1..3 -> {
                    binding.fragmentContentHome.setOnClickListener(this)
                    binding.homeToComplete.setOnClickListener(this)
                }

                4 -> {
                    binding.homeToComplete.setOnClickListener(this)
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fragment_content_home -> {
                val intent = Intent(activity, CameraActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            R.id.home_to_complete -> {
                if (imgcount < 4) {
                    val normaldialog = NormalDialogFragment()
                    val btn= arrayOf("네", "아니오")
                    normaldialog.arguments = bundleOf(
                        "bodyContext" to "네 선택 시, 설정의 '사진 반복 여부'에 따라 필름의 남은 칸을 채웁니다.",
                        "bodyTitle" to "필름이 채워지지 않은 상태로 완성 하시겠습니까?",
                        "btnData" to btn
                    )

                    normaldialog.show((activity as MainActivity).supportFragmentManager, "CustomDialog")

                    normaldialog.setButtonClickListener(object :
                        NormalDialogFragment.OnButtonClickListener {
                        override fun onButton1Clicked() {
                            //네
                            moveToComplete()
                        }

                        override fun onButton2Clicked() {}
                    })
                } else {
                    moveToComplete()
                }
            }
        }
    }

    private fun moveToComplete() {
        // complete 액티비티로 이동
        val intent = Intent(activity, CompleteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}