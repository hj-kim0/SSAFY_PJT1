package com.example.todayfilm

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import com.example.todayfilm.databinding.FragmentFilmBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_film.*

class FilmFragment : Fragment(), View.OnClickListener,PopupMenu.OnMenuItemClickListener{
    lateinit var binding: FragmentFilmBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().add(R.id.fragment_content_film, FrameFragment()).commit()
        setOnClickListener()
    }

    private fun setOnClickListener() {

        binding.menu.setOnClickListener(this)
    }



    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.menu -> { showPopup(binding.menu)




            }
        }
    }
    private fun showPopup(v: View) {

        val popup = PopupMenu(context,v) // PopupMenu 객체 선언
        popup.menuInflater.inflate(R.menu.popup, popup.menu) // 메뉴 레이아웃 inflate
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        val builder = AlertDialog.Builder(activity)



        when (p0?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
            R.id.delete ->  { builder.setTitle("정말 삭제 하시겠습니까?")
                .setMessage("삭제하시면 복구되지 않습니다.")
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, id ->

                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, id -> })
            builder.show() }

            R.id.share -> { builder.setTitle("공유")
                .setMessage("공유여부를 어떻게 설정 하시겠습니까?")
                .setNegativeButton("팔로워에게 공유",DialogInterface.OnClickListener { dialog, id ->

                })
                .setNeutralButton("전체공개",DialogInterface.OnClickListener { dialog, id ->

                })
                .setPositiveButton("비공개",DialogInterface.OnClickListener { dialog, id ->

                })

                builder.show() }

            R.id.save -> { builder.setTitle("정말 저장 하시겠습니까?")
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, id ->

                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, id -> })
                builder.show() }
        }

        return p0 != null
    }




}