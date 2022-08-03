package com.example.todayfilm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.todayfilm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG_FEED = "feed_fragment"
    private val TAG_HOME = "home_fragment"
    private val TAG_PROFILE = "profile_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 처음에 보여줄 프래그먼트 지정
        setFragment(TAG_HOME, HomeFragment())
        binding.navBar.selectedItemId = R.id.homeFragment

        // 네비 항목 클릭 시 프래그먼트 변경
        binding.navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.feedFragment -> setFragment(TAG_FEED, FeedFragment())
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.profileFragment -> setFragment(TAG_PROFILE, ProfileFragment())
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
        val profile = fragmentManager.findFragmentByTag(TAG_PROFILE)

        // 모든 프래그먼트 숨기기
        if (feed != null) transaction.hide(feed)
        if (home != null) transaction.hide(home)
        if (profile != null) transaction.hide(profile)

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

            2 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_content_main, ProfileListFragment())
                    .addToBackStack(null).commit()
            }
        }
    }
}