package com.example.todayfilm.retrofit

import com.example.todayfilm.data.*
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
}