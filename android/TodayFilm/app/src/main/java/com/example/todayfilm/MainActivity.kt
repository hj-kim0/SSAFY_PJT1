package com.example.todayfilm

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.todayfilm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val TAG_FEED = "feed_fragment"
    val TAG_HOME = "home_fragment"
    val TAG_PROFILE = "profile_fragment"
    val TAG_SEARCH = "search_fragment"
    val TAG_PROFILE_LIST = "profile_list_fragment"
    val TAG_FILM = "film_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 앱 최초 실행일 경우, 내부 저장소에 imgcount, isComplete, imgvids 변수 초기화
        val sp = getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        val first = sp.getBoolean("isFirst", false)
        if (!first) {
            val editor = sp.edit()
            editor.putBoolean("isFirst", true)
            editor.apply()

            MyPreference.writeInt(this, "imgcount", 0)
            MyPreference.writeInt(this, "isComplete", 0)
            MyPreference.write(this, "imgvids", "")
        }

        // 처음에 보여줄 프래그먼트 지정
        setFragment(TAG_HOME, HomeFragment())
        binding.navBar.selectedItemId = R.id.homeFragment

        // 네비 항목 클릭 시 프래그먼트 변경
        binding.navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.feedFragment ->{ clearBackStack()
                    setFragment(TAG_FEED, FeedFragment())}
                R.id.homeFragment -> { clearBackStack()
                    setFragment(TAG_HOME, HomeFragment())}
                R.id.profileFragment -> { clearBackStack()
                        setFragment(TAG_PROFILE, ProfileFragment())}
            }
            true
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

    fun changeFragment(index: Int){
        when(index){
            1 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_content_main, SearchFragment())
                    .addToBackStack(null).commit()
            }
            2-> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_content_main, ProfileListFragment())
                    .addToBackStack(null).commit()
            }
        }
    }

    fun clearBackStack() {
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}