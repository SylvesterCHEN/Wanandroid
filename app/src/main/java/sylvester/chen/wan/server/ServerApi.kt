package sylvester.chen.wan.server

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ServerApi {

  // https://wanandroid.com/wxarticle/chapters/json
  @GET("wxarticle/chapters/json")
  suspend fun wePublisher(): Response<List<WeIssuer>>

  // https://wanandroid.com/wxarticle/list/408/1/json
  // https://wanandroid.com/wxarticle/list/405/1/json?k=Java
  @GET("wxarticle/list/{wechatId}/{page}/json")
  suspend fun weArticles(
    @Path("wechatId") wechatId: String,
    @Path("page") page: Int,
    @Query("k") search: String? = null
  ): Response<PagedArticle>

  // https://www.wanandroid.com/article/list/0/json
  // https://www.wanandroid.com/article/list/0/json?cid=60
  @GET("article/list/{page}/json")
  suspend fun articles(
    @Path("page") page: Int,
    @Query("cid") categoryId: Int? = null
  ): Response<PagedArticle>

  // https://www.wanandroid.com/article/list/0/json?author=鸿洋
  @GET("article/list/{page}/json")
  suspend fun articles(
    @Path("page") page: Int,
    @Query("author") author: String
  ): Response<PagedArticle>

  // https://www.wanandroid.com/article/top/json
  @GET("article/top/json")
  suspend fun popArticles(): Response<List<Article>>

  // https://www.wanandroid.com/article/query/0/json
  @POST("article/query/{page}/json")
  @FormUrlEncoded
  suspend fun search(
    @Path("page") page: Int,
    @Field("k") objection: String
  ): Response<PagedArticle>

  // https://www.wanandroid.com/banner/json
  @GET("banner/json")
  suspend fun banner(): Response<List<Banner>>

  // https://www.wanandroid.com/hotkey/json
  @GET("hotkey/json")
  suspend fun popWords(): Response<List<KeyWord>>

  // https://www.wanandroid.com/tree/json
  @GET("tree/json")
  suspend fun category(): Response<List<Category>>
}
