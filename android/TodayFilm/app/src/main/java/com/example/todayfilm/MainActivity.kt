package com.example.todayfilm

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.todayfilm.data.ArticleResponse
import com.example.todayfilm.data.getArticleRequest
import com.example.todayfilm.databinding.ActivityMainBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG_FEED = "feed_fragment"
    private val TAG_HOME = "home_fragment"
    private val TAG_PROFILE = "profile_fragment"
    private val TAG_SEARCH = "search_fragment"
    private val TAG_PROFILE_LIST = "profile_list_fragment"
    private val TAG_FILM = "film_fragment"

    private var fromResetNotification = false
    private var isComplete = 0
    private var todayarticleidx = "-1"

    private var FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isComplete = MyPreference.readInt(this, "isComplete")
        todayarticleidx = MyPreference.read(this, "todayarticleidx")
        val parent = intent.getStringExtra("parent")

        // 처음에 보여줄 프래그먼트 지정
        if (parent == "changeprofile") {
            changeFragment(4, MyPreference.read(this, "userpid"))
        } else if (parent == "complete") {
            changeFragment(4, MyPreference.read(this, "userpid"), "complete")
        } else if (isComplete == 1 && todayarticleidx != "-1") {
            isLoginAfterComplete()
        } else {
            setFragment(TAG_HOME, HomeFragment())
        }

        if (parent == "changeprofile") {
            binding.navBar.selectedItemId = R.id.profileFragment
        } else {
            binding.navBar.selectedItemId = R.id.homeFragment
        }

        // 네비 항목 클릭 시 프래그먼트 변경
        binding.navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.feedFragment -> {
                    clearBackStack()
                    setFragment(TAG_FEED, FeedFragment())
                }

                R.id.homeFragment -> {
                    clearBackStack()
                    todayarticleidx = MyPreference.read(this, "todayarticleidx")
                    if (isComplete == 1 && todayarticleidx != "-1") {
                        isLoginAfterComplete()
                    } else {
                        setFragment(TAG_HOME, HomeFragment())
                    }
                }

                R.id.profileFragment -> {
                    myProfile()
                }
            }
            true
        }
    }

    fun myProfile() {
        clearBackStack()
        val fragment = ProfileFragment()
        val bundle = Bundle()
        bundle.putString("search_userpid", MyPreference.read(this, "userpid"))
        fragment.arguments = bundle
        setFragment(TAG_PROFILE, fragment)
    }

    override fun onResume() {
        super.onResume()

        // reset 알림을 받고 왔거나, 내부 저장소의 date가 오늘 날짜와 다르면 다이얼로그 띄우기
        fromResetNotification = intent.getBooleanExtra("fromResetNotification", false)
        val today = SimpleDateFormat("yyyy/MM/dd (E)", Locale.KOREA)
            .format(System.currentTimeMillis())
        val date = MyPreference.read(this, "date")

        if (fromResetNotification || today != date) {
            val normaldialog = NormalDialogFragment()
            val btn= arrayOf("네","아니오")
            normaldialog.arguments = bundleOf(
                "bodyContext" to "네 선택 시, 오늘 날짜로 이어서 기록됩니다.\n아니오 선택 시, 필름을 초기화합니다",
                "bodyTitle" to "이전 필름이 완성되지 않았습니다.\n이어서 기록하시겠습니까?",
                "btnData" to btn
            )
            normaldialog.isCancelable = false

            normaldialog.show(this.supportFragmentManager, "CustomDialog")

            normaldialog.setButtonClickListener(object :
                NormalDialogFragment.OnButtonClickListener {
                override fun onButton1Clicked() {
                    // 네
                    fromResetNotification = false
                    // date 갱신
                    MyPreference.write(this@MainActivity, "date", today)
                }

                override fun onButton2Clicked() {
                    // 아니오
                    fromResetNotification = false
                    // 필름 데이터 초기화, date 갱신
                    resetData(this@MainActivity)
                    MyPreference.write(this@MainActivity, "date", today)
                }
            })
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
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

    fun changeFragment(index: Int, data: String?=null, data1: String?=null, data2: String?=null, data3: String?=null, data4: ArrayList<String>?=null) {
        when (index) {
            1 -> {
                if (data != null) {
                    MyPreference.write(this, "keyword", data)
                }
                moveFragment(SearchFragment())
            }

            2 -> {
                if (data != null) {
                    val fragment = ProfileListFragment()
                    val bundle = Bundle()
                    bundle.putString("title", data)
                    bundle.putString("search_userpid", data1)
                    fragment.arguments = bundle
                    moveFragment(fragment)
                }
            }

            3 -> {
                if (data != null) {
                    val fragment = FilmFragment()
                    val bundle = Bundle()
                    bundle.putString("articleidx", data)
                    bundle.putString("articlecreatedate", data1)
                    bundle.putString("article_userpid", data2)
                    bundle.putString("likey", data3)
                    bundle.putStringArrayList("hash", data4)
                    fragment.arguments = bundle

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_content_main, fragment)
                        .addToBackStack(null).commit()
                }
            }

            4 -> {
                if (data != null) {
                    val fragment = ProfileFragment()
                    val bundle = Bundle()
                    bundle.putString("search_userpid", data)
                    bundle.putString("parent", data1)
                    fragment.arguments = bundle
                    moveFragment(fragment)
                }
            }
        }
    }

    private fun clearBackStack() {
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun moveFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.animator.enter, R.animator.none, R.animator.none, R.animator.exit)
            .replace(R.id.fragment_content_main, fragment)
            .addToBackStack(null).commit()
    }

    private fun isLoginAfterComplete() {
    // 완성했으면서 todayarticleidx 값이 -1이 아니라면 통신해서 게시글 정보 가져오고 Film Fragment 띄우기
        val getArticleReqeust = getArticleRequest()
        getArticleReqeust.articleidx = todayarticleidx
        val call =  NetWorkClient.GetNetwork.getarticledetail(getArticleReqeust)
        call.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                val result: ArticleResponse? = response.body()

                if (result != null) {
                    changeFragment(3, result.article.articleidx, result.article.articlecreatedate, result.article.userpid, result.likey, result.hash as ArrayList)
                } else {
                    Toast.makeText(this@MainActivity, "게시글 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "게시글 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("게시글 조회 실패", t.message.toString())
            }
        })
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            val tempTime = System.currentTimeMillis()
            val intervalTime = tempTime - backPressedTime
            if (intervalTime in 0..FINISH_INTERVAL_TIME) {
                super.onBackPressed()
            } else {
                backPressedTime = tempTime
                Toast.makeText(this, "종료하시려면 뒤로가기를 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        super.onBackPressed()
    }
}
