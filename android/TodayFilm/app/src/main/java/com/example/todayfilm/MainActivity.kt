package com.example.todayfilm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.todayfilm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG_FEED = "feed_fragment"
    private val TAG_HOME = "home_fragment"
    private val TAG_FILMS = "films_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 처음에 보여줄 프래그먼트 지정
        setFragment(TAG_HOME, HomeFragment())

        // 네비 항목 클릭 시 프래그먼트 변경
        binding.navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                // !!!!!!!!! 피드 프래그먼트로 변경 필요!!!!!!!!!
                R.id.feedFragment -> setFragment(TAG_FEED, FilmFragment())
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
}