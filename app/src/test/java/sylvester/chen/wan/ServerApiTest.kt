package sylvester.chen.wan

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClientTestRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.After
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sylvester.chen.wan.server.ServerApi

class ServerApiTest {

  @Rule
  @JvmField
  val okHttp = OkHttpClientTestRule()

  private val fakeServer = MockWebServer()
  private val retrofit = Retrofit.Builder()
    .client(okHttp.client)
    .addConverterFactory(GsonConverterFactory.create())
    .validateEagerly(true)
    .baseUrl(fakeServer.url("mock/"))
    .build()
  private val subject = retrofit.create(ServerApi::class.java)

  @Test
  fun validate_articles_requests() {
    runBlocking {
      fakeServer.enqueue(MockResponse().setBody("{}"))
      subject.articles(1)
      var request = fakeServer.takeRequest()

      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/article/list/1/json")
      assertThat(request.bodySize).isZero

      fakeServer.enqueue(MockResponse().setBody("{}"))
      subject.articles(99, 66)
      request = fakeServer.takeRequest()

      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/article/list/99/json?cid=66")
      assertThat(request.bodySize).isZero

      fakeServer.enqueue(MockResponse().setBody("{}"))
      subject.articles(101, "66")
      request = fakeServer.takeRequest()

      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/article/list/101/json?author=66")
      assertThat(request.bodySize).isZero
    }
  }

  @Test
  fun validate_articles_responses() {
    runBlocking {

      fakeServer.enqueue(MockResponse().setBody("{}"))
      var articles = subject.articles(1)
      assertThat(articles.data).isNull()

      fakeServer.enqueue(resourceToResponse("response_null.json"))
      articles = subject.articles(2)
      assertThat(articles.data).isNull()

      fakeServer.enqueue(resourceToResponse("articles_empty.json"))
      articles = subject.articles(3)
      assertThat(articles.data).isNotNull
      assertThat(articles.data.articles).isEmpty()
      assertThat(articles.data.over).isTrue

      fakeServer.enqueue(resourceToResponse("articles_response.json"))
      articles = subject.articles(4)
      assertThat(articles.data).isNotNull
      assertThat(articles.data.articles).isNotEmpty
      assertThat(articles.data.over).isFalse
      assertThat(articles.data.articles[0].title).isEqualTo("温故而知新 | 打破Handler问到底")
    }
  }

  @After
  fun tearDown() {
    fakeServer.shutdown()
  }

  private fun resourceToResponse(resourceName: String): MockResponse {
    return javaClass.classLoader!!
      .getResourceAsStream(resourceName)
      .use { MockResponse().setBody(Buffer().readFrom(it)) }
  }
}
