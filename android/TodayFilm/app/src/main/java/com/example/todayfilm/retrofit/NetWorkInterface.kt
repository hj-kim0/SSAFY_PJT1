package com.example.todayfilm.retrofit

import com.example.todayfilm.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NetWorkInterface {
    @POST("api/account/signup")
    fun signUp(
        @Body user: User
    ): Call<SignupData>

    @POST("api/account/signin")
    fun login(
        @Body user: User
    ): Call<LoginData>

    @POST("api/account/signout")
    fun singout(
        @Body deleteUser: DeleteAccountRequest
    ): Call<DeleteAccountResponse>

    @POST("api/account/changepw")
    fun changepw(
        @Body changePw: ChangePwRequest
    ): Call<ChangePwResponse>

    @GET("api/account/findpw")
    fun findpw(
        @Body findPw: FindPwRequest
    ): Call<FindPwResponse>

    @POST("api/account/changeuserdetail")
    fun changeuserdetail(
        @Body changeUserDetail: ChangeUserDetailRequest
    ): Call<ChangeUserDetailResponse>

    @Multipart
    @POST("api/article/create")
    fun createarticle(
        @Part imgdata: List<MultipartBody.Part?>,
        @Part videodata: List<MultipartBody.Part?>,
        @Part("userpid") userpid: RequestBody,
        @Part("articlethumbnail") articlethumbnail: RequestBody,
        @Part("articleshare") articleshare: RequestBody
    ): Call<FindPwResponse>

    @POST("api/article/sharecontrol")
    fun changearticleshare(
        @Body changeArticleShare: ChangeArticleShareRequest
    ): Call<ChangeUserDetailResponse>
}