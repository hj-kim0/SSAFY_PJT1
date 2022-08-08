package com.example.todayfilm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private var liveString: MutableLiveData<String> = MutableLiveData()

    fun getLiveText(): LiveData<String> {
        return liveString
    }

    fun setLiveText(string: String) {
        liveString.value = string
    }
}