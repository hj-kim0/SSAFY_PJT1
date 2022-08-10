package com.example.todayfilm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.todayfilm.data.SearchUser

class SearchArticleAdapter(private val context: Context) : RecyclerView.Adapter<SearchArticleAdapter.ViewHolder>() {
    var datas = mutableListOf<SearchUser>()

    // ViewHolder 객체 생성 후 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_search_profile, parent, false)
        return ViewHolder(view)
    }

    // 전체 데이터 개수 조회
    override fun getItemCount(): Int = datas.size

    // ViewHolder 재활용 시 사용
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userid: TextView = itemView.findViewById(R.id.recycler_profile_userid)
        private val userimg: ImageView = itemView.findViewById(R.id.recycler_profile_image)

        fun bind(item: SearchUser) {
            userid.text = item.userid
            Glide.with(itemView).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/profile/" + item.userimg).into(userimg)
        }
    }
}