package com.example.todayfilm

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.todayfilm.databinding.FragmentNormalDialogBinding


class NormalDialogFragment(): DialogFragment() {

    private var _binding: FragmentNormalDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNormalDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        initDialog()
        return view
    }

    fun initDialog() {
        binding.dialogContent.text = arguments?.getString("bodyContext")
        binding.dialogTitle.text = arguments?.getString("bodyTitle")
        val btnBundle = arguments?.getStringArray("btnData")

        binding.button1.setOnClickListener {
            buttonClickListener.onButton1Clicked()
            dismiss()
        }
        binding.button1.text = btnBundle?.get(0)


        binding.button2.setOnClickListener {
            buttonClickListener.onButton2Clicked()
            dismiss()
        }
        binding.button2.text = btnBundle?.get(1)
    }


    interface OnButtonClickListener {
        fun onButton1Clicked()
        fun onButton2Clicked()
    }

    override fun onStart() {
        super.onStart();
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        val window: Window = dialog!!.window!!
        window.attributes = lp
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener

}