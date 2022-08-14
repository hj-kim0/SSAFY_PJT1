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

    @POST("api/account/signdown")
    fun signdown(
        @Body deleteUser: DeleteAccountRequest
    ): Call<DeleteAccountResponse>

    @POST("api/account/signout")
    fun signout(
        @Body logoutUser: LogoutRequest
    ): Call<LogoutResponse>

    @POST("api/account/changepw")
    fun changepw(
        @Body changePw: ChangePwRequest
    ): Call<ChangePwResponse>

    @POST("api/account/findpw")
    fun findpw(
        @Body findPw: FindPwRequest
    ): Call<FindPwResponse>

    @Multipart
    @POST("api/profile/modify")
    fun changeuserdetail(
        @Part userimg: MultipartBody.Part,
        @Part("userpid") userpid: RequestBody,
        @Part("username") username: RequestBody,
        @Part("userdesc") userdesc: RequestBody,
    ): Call<ChangeUserDetailResponse>

    @Multipart
    @POST("api/profile/modify")
    fun changeuserdetail2(
        @Part("userpid") userpid: RequestBody,
        @Part("username") username: RequestBody,
        @Part("userdesc") userdesc: RequestBody,
    ): Call<ChangeUserDetailResponse>

    @Multipart
    @POST("api/article/create")
    fun createarticle(
        @Part imgdata: List<MultipartBody.Part?>,
        @Part videodata: List<MultipartBody.Part?>,
        @Part("userpid") userpid: RequestBody,
        @Part("articlethumbnail") articlethumbnail: RequestBody,
        @Part("articleshare") articleshare: RequestBody,
        @Part("hashlist") hashlist: RequestBody
    ): Call<FindPwResponse>

    @POST("api/article/sharecontrol")
    fun changearticleshare(
        @Body changeArticleShare: ChangeArticleShareRequest
    ): Call<ChangeUserDetailResponse>

    @POST("api/profile/join")
    fun getprofile(
        @Body getProfile: GetProfile
    ): Call<CompleteProfile>

    @POST("api/article/showarticle")
    fun showarticle(
        @Body getArticle : GetArticle
    ): Call<List<ArticleResponse>>

    @POST("api/search/user")
    fun searchuser(
        @Body search: SearchRequest
    ): Call<SearchUserResponse>

    @POST("api/search/hash")
    fun searcharticle(
        @Body search: SearchRequest
    ): Call<List<ArticleResponse>>

    @POST("api/subscribe/delete")
    fun deletefollow(
        @Body follow: FollowRequest
    ): Call<noRseponse>

    @POST("api/subscribe/create")
    fun createfollow(
        @Body follow: FollowRequest
    ): Call<noRseponse>

    @POST("api/article/showsubarticle")
    fun showsubarticle(
        @Body getProfile: GetProfile
    ): Call<List<ArticleResponse>>

    @POST("api/subscribe/follow")
    fun followsearch(
        @Body follow: FollowRequest
    ): Call<FollowBoolean>

    @POST("api/subscribe/following")
    fun getfollowing(
        @Body getProfile: GetProfile
    ): Call<ProfileListResponse>

    @POST("api/subscribe/followed")
    fun getfollower(
        @Body getProfile: GetProfile
    ): Call<ProfileListResponse>

    @POST("api/article/delete")
    fun articledelete(
        @Body articledelete: ArticleDeleteRequest
    ): Call<ArticleDeleteResponse>

    @POST("api/subscribe/followed")
    fun followed(
        @Body getProfile: GetProfile
    ):Call<FollowList>

    @POST("api/subscribe/following")
    fun following(
        @Body getProfile: GetProfile
    ):Call<FollowList>

    //좋아요
    @POST("api/likey/create")
    fun likedcreate(
        @Body likecreate : LikeRequest
    ): Call<noRseponse>

    //좋아요 취소
    @POST("api/likey/delete")
    fun likeddelete(
        @Body likecreate : LikeRequest
    ): Call<noRseponse>

    // 좋아요 여부
    @POST("api/article/likey")
    fun likedcheck(
        @Body likecheck : LikeCheck
    ): Call<LikeBoolean>

    @POST("api/article/getarticle")
    fun getarticle(
        @Body likeynumber : getArticleRequest
     ): Call<getArticleResponse>

    @POST("api/article/getarticle")
    fun getarticledetail(
        @Body getarticlerequest : getArticleRequest
    ): Call<ArticleResponse>
}

