package com.example.todayfilm

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.todayfilm.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val TAG_FEED = "feed_fragment"
    val TAG_HOME = "home_fragment"
    val TAG_PROFILE = "profile_fragment"
    val TAG_SEARCH = "search_fragment"
    val TAG_PROFILE_LIST = "profile_list_fragment"
    val TAG_FILM = "film_fragment"

    var fromResetNotification = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isComplete = MyPreference.readInt(this, "isComplete")

        // 처음에 보여줄 프래그먼트 지정
        setFragment(TAG_HOME, HomeFragment())
        binding.navBar.selectedItemId = R.id.homeFragment

        // 네비 항목 클릭 시 프래그먼트 변경
        binding.navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.feedFragment -> {
                    clearBackStack()
                    setFragment(TAG_FEED, FeedFragment())
                }

                R.id.homeFragment -> {
                    clearBackStack()
                    setFragment(TAG_HOME, HomeFragment())
                }

                R.id.profileFragment -> {
                    clearBackStack()
                    val fragment = ProfileFragment()
                    val bundle = Bundle()
                    bundle.putString("search_userpid", MyPreference.read(this, "userpid"))
                    fragment.arguments = bundle
                    setFragment(TAG_PROFILE, fragment)
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        // changeprofile에서 왔다면 Profile Fragment 띄우기
        val parent = intent.getStringExtra("parent")
        if (parent == "changeprofile") {
            changeFragment(4, MyPreference.read(this, "userpid"))
        }

        // reset 알림을 받고 왔거나, 내부 저장소의 date가 오늘 날짜와 다르면 다이얼로그 띄우기
        fromResetNotification = intent.getBooleanExtra("fromResetNotification", false)
        val today = SimpleDateFormat("yyyy/MM/dd (E)", Locale.KOREA)
            .format(System.currentTimeMillis())
        val date = MyPreference.read(this, "date")

        if (fromResetNotification || today != date) {
            // 완성이 아니고 이미지가 1개 이상인 상태


            val normaldialog = NormalDialogFragment()
            val duration = Toast.LENGTH_SHORT
            val btn= arrayOf("네","아니오")
            normaldialog.arguments = bundleOf(
                "bodyContext" to "예 선택 시, 설정의 '사진 반복 여부'에 따라\\n필름의 남은 칸을 채웁니다.\\n아니오 선택 시, 필름을 초기화합니다",
                "bodyTitle" to "아직 필름이 다 채워지지 않았습니다.\\n이대로 완성하시겠습니까?",
                "btnData" to btn
            )

            normaldialog.show(this.supportFragmentManager, "CustomDialog")

            normaldialog.setButtonClickListener(object :
                NormalDialogFragment.OnButtonClickListener {
                override fun onButton1Clicked() {
                    //취소버튼을 눌렀을 때 처리할 곳
//                    fromResetNotification = false
//                    val intent = Intent(this, CompleteActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(intent)
//
//
//                    Toast.makeText(context, "로그아웃이 완료되었습니다.", duration).show()
                }

                override fun onButton2Clicked() {
//                    fromResetNotification = false
//                    // 필름 데이터 초기화, date 갱신
//                    resetData(this)
//                    MyPreference.write(this, "date", today)
//
//
//                    //확인버튼을 눌렀을 때 처리할 곳
//                    Toast.makeText(context, "로그아웃이 취소되었습니다.", duration).show()
                }
            })



//            val builder = AlertDialog.Builder(this)
//
//            builder.setTitle("아직 필름이 다 채워지지 않았습니다.\n이대로 완성하시겠습니까?")
//                .setMessage("예 선택 시, 설정의 '사진 반복 여부'에 따라\n필름의 남은 칸을 채웁니다.\n아니오 선택 시, 필름을 초기화합니다.")
//                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, id ->
//                    fromResetNotification = false
//
//                    // complete 액티비티로 이동
//                    val intent = Intent(this, CompleteActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    startActivity(intent)
//                })
//                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, id ->
//                    fromResetNotification = false
//
//                    // 필름 데이터 초기화, date 갱신
//                    resetData(this)
//                    MyPreference.write(this, "date", today)
//                })
//            builder.show()
        }

    }

    fun setFragment(tag: String, fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // 트랜잭션에 tag로 전달된 fragment가 없을 경우 add
        if (fragmentManager.findFragmentByTag(tag) == null) {
            transaction.add(R.id.fragment_content_main, fragment, tag)
        }

        // 작업이 수월하도록 manager에 add 되어있는 fragment들을 변수로 할당해둠
        val feed = fragmentManager.findFragmentByTag(TAG_FEED)
        val home = fragmentManager.findFragmentByTag(TAG_HOME)
        val profile = fragmentManager.findFragmentByTag(TAG_PROFILE)
        val search = fragmentManager.findFragmentByTag(TAG_SEARCH)
        val profileList = fragmentManager.findFragmentByTag(TAG_PROFILE_LIST)
        val film = fragmentManager.findFragmentByTag(TAG_FILM)

        // 모든 프래그먼트 숨기기
        if (feed != null) transaction.hide(feed)
        if (home != null) transaction.hide(home)
        if (profile != null) transaction.hide(profile)
        if (search != null) transaction.hide(search)
        if (profileList != null) transaction.hide(profileList)
        if (film != null) transaction.hide(film)

        // 선택한 프래그먼트만 보이기
        if (tag == TAG_FEED) {
            if (feed != null) {
                transaction.show(feed)
            }
        } else if (tag == TAG_HOME) {
            if (home != null) {
                transaction.show(home)
            }
        } else if (tag == TAG_PROFILE) {
            if (profile != null) {
                transaction.show(profile)
            }
        } else if (tag == TAG_SEARCH) {
            if (search != null) {
                transaction.show(search)
            }
        } else if (tag == TAG_PROFILE_LIST) {
            if (profileList != null) {
                transaction.show(profileList)
            }
        } else if (tag == TAG_FILM) {
            if (film != null) {
                transaction.show(film)
            }
        }

        transaction.commitAllowingStateLoss()
    }

    fun changeFragment(index: Int, data: String?=null, data1: String?=null, data2: String?=null, data3: String?=null, data4: String?=null){
        when(index){
            1 -> {
                moveFragment(SearchFragment())
            }

            2 -> {
                moveFragment(ProfileListFragment())
            }

            3 -> {
                if (data != null) {
                    val fragment = FilmFragment()
                    val bundle = Bundle()
                    bundle.putString("articleidx", data)
                    bundle.putString("articlecreatedate", data1)
                    bundle.putString("article_userpid", data2)
                    bundle.putString("likey", data3)
                    bundle.putString("hashstring", data4)
                    fragment.arguments = bundle
                    moveFragment(fragment)
                }
            }

            4 -> {
                if (data != null) {
                    val fragment = ProfileFragment()
                    val bundle = Bundle()
                    bundle.putString("search_userpid", data)
                    fragment.arguments = bundle
                    moveFragment(fragment)
                }
            }
        }
    }

    fun clearBackStack() {
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun moveFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.xml.enter, R.xml.none, R.xml.none, R.xml.exit)
            .replace(R.id.fragment_content_main, fragment)
            .addToBackStack(null).commit()
    }
}
