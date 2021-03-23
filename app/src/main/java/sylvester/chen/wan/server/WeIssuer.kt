package sylvester.chen.wan.server

import com.google.gson.annotations.SerializedName

data class WeIssuer(
  @SerializedName("children")
  val children: List<Any>,
  @SerializedName("courseId")
  val courseId: Int,
  @SerializedName("id")
  val id: Int,
  @SerializedName("name")
  val name: String,
  @SerializedName("order")
  val order: Int,
  @SerializedName("parentChapterId")
  val parentChapterId: Int,
  @SerializedName("userControlSetTop")
  val userControlSetTop: Boolean,
  @SerializedName("visible")
  val visible: Int
)
