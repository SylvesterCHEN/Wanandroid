package sylvester.chen.wan.server

import com.google.gson.annotations.SerializedName

data class KeyWord(
  @SerializedName("id")
  val id: Int,
  @SerializedName("link")
  val link: String,
  @SerializedName("name")
  val name: String,
  @SerializedName("order")
  val order: Int,
  @SerializedName("visible")
  val visible: Int
)
