package com.example.todayfilm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todayfilm.databinding.FragmentSearchBinding

class SearchFragment : Fragment(),View.OnClickListener {
    lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnclickListner()
    }

    private fun setOnclickListner(){
        binding.goProfileList.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.go_profile_list -> {

                (activity as MainActivity).changeFragment(2)
            }
        }
    }


}
// 여기야 예지야!!
//val search = SearchRequest()
//search.keyword = LoginId.toString()
//val call = NetWorkClient.GetNetwork.search(search)
//call.enqueue(object : Callback<SearchResponse> {
//    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
//        val result: SearchResponse? = response.body()
//        Log.d("test", result!!.userlist.get(0).userid)
//        Log.d("test", result!!.userlist.get(0).userpid.toString())
//        Log.d("test", result!!.userlist.get(0).username)
//
//    }
//
//    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
//        Log.d("", "실패"+t.message.toString())
//    }
//})


