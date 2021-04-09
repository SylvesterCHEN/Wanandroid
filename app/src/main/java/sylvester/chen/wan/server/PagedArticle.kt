package sylvester.chen.wan.server

import com.google.gson.annotations.SerializedName

data class PagedArticle(
  @SerializedName("curPage")
  val index: Int,
  @SerializedName("offset")
  val offset: Int,
  @SerializedName("datas")
  val articles: List<Article>,
  @SerializedName("over")
  val over: Boolean,
  @SerializedName("pageCount")
  val pageCount: Int,
  @SerializedName("size")
  val size: Int,
  @SerializedName("total")
  val total: Int
)
