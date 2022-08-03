package com.example.todayfilm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.todayfilm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG_FEED = "feed_fragment"
    private val TAG_HOME = "home_fragment"
    private val TAG_FILMS = "films_fragment"
    private var doubleBackToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val userpid = MyPreference.read(this, "userid")
//        val usertoken = MyPreference.read(this, "usertoken")
//        val up = MyPreference.read(this, "userpassword")
//        Log.d("test:", up)
//        println(userpid)
//        println(usertoken)
        // 처음에 보여줄 프래그먼트 지정
        setFragment(TAG_HOME, HomeFragment())
        binding.navBar.selectedItemId = R.id.homeFragment

        // 네비 항목 클릭 시 프래그먼트 변경
        binding.navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                // !!!!!!!!! 피드 프래그먼트로 변경 필요!!!!!!!!!
                R.id.feedFragment -> setFragment(TAG_FEED, FeedFragment())
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.filmsFragment -> setFragment(TAG_FILMS, FilmsFragment())
            }
            true
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
        val films = fragmentManager.findFragmentByTag(TAG_FILMS)

        // 모든 프래그먼트 숨기기
        if (feed != null) transaction.hide(feed)
        if (home != null) transaction.hide(home)
        if (films != null) transaction.hide(films)

        // 선택한 프래그먼트만 보이기
        if (tag == TAG_FEED) {
            if (feed != null) {
                transaction.show(feed)
            }
        } else if (tag == TAG_HOME) {
            if (home != null) {
                transaction.show(home)
            }
        } else if (tag == TAG_FILMS) {
            if (films != null) {
                transaction.show(films)
            }
        }

        transaction.commitAllowingStateLoss()
    }

    fun changeFragment(index: Int){
        when(index){

            1 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_content_main, SearchFragment())
                    .addToBackStack(null).commit()
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, "종료하시려면 뒤로가기를 한번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
            }
        }
    }
    fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }


}