package com.example.todayfilm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private var liveString: MutableLiveData<String> = MutableLiveData()
    private var articleIdx: MutableLiveData<String> = MutableLiveData()

    fun getLiveText(): LiveData<String> {
        return liveString
    }

    fun setLiveText(string: String) {
        liveString.value = string
    }

    fun getArticleIdx(): LiveData<String> {
        return articleIdx
    }

    fun setArticleIdx(string: String) {
        articleIdx.value = string
    }
}