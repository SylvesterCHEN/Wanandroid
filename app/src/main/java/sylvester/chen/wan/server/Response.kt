package sylvester.chen.wan.server

import com.google.gson.annotations.SerializedName

class Response<T>(
  @SerializedName("data")
  val data: T,
  @SerializedName("errorCode")
  val errorCode: Int,
  @SerializedName("errorMsg")
  val errorMsg: String?
)
