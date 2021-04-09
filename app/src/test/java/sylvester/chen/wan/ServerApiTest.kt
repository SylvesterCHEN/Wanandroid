package sylvester.chen.wan

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClientTestRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThatThrownBy
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
  fun validate_we_publisher_requests() {
    runBlocking {
      enqueueEmptyJsonResponse()
      subject.wePublisher()
      val request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/wxarticle/chapters/json")
      assertThat(request.bodySize).isZero
    }
  }

  @Test
  fun validate_we_publisher_responses() {
    runBlocking {
      enqueueEmptyJsonResponse()
      var wePublisher = subject.wePublisher()
      assertThat(wePublisher.data).isNull()

      enqueueResourceToResponse("response_null.json")
      wePublisher = subject.wePublisher()
      assertThat(wePublisher.data).isNull()

      enqueueResourceToResponse("we_publisher_response.json")
      wePublisher = subject.wePublisher()
      assertThat(wePublisher.data).isNotNull
      assertThat(wePublisher.errorCode).isZero
      assertThat(wePublisher.data.size).isEqualTo(14)
      assertThat(wePublisher.data[6].name).isEqualTo("谷歌开发者")
    }
  }

  @Test
  fun validate_we_articles_requests() {
    runBlocking {
      enqueueEmptyJsonResponse()
      subject.weArticles("001", 1)
      var request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/wxarticle/list/001/1/json")
      assertThat(request.bodySize).isZero

      enqueueEmptyJsonResponse()
      subject.weArticles("201", 5, "Kotlin")
      request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/wxarticle/list/201/5/json?k=Kotlin")
      assertThat(request.bodySize).isZero
    }
  }

  @Test
  fun validate_we_articles_response() {
    runBlocking {
      enqueueEmptyJsonResponse()
      var weArticles = subject.weArticles("303", 2)
      assertThat(weArticles.data).isNull()

      enqueueResourceToResponse("response_null.json")
      weArticles = subject.weArticles("008", 2)
      assertThat(weArticles.data).isNull()

      enqueueResourceToResponse("we_articles_response.json")
      weArticles = subject.weArticles("919", 2)
      assertThat(weArticles.data).isNotNull
      assertThat(weArticles.data.articles).isNotEmpty

      kotlin.runCatching {
        enqueueResourceToResponse("we_publisher_response.json")
        subject.weArticles("919", 2)
      }.run {
        assertThat(isFailure).isTrue
        assertThatThrownBy { getOrThrow() }
      }
    }
  }

  @Test
  fun validate_articles_requests() {
    runBlocking {
      enqueueEmptyJsonResponse()
      subject.articles(1)
      var request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/article/list/1/json")
      assertThat(request.bodySize).isZero

      enqueueEmptyJsonResponse()
      subject.articles(99, 66)
      request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/article/list/99/json?cid=66")
      assertThat(request.bodySize).isZero

      enqueueEmptyJsonResponse()
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
      enqueueEmptyJsonResponse()
      var articles = subject.articles(1)
      assertThat(articles.data).isNull()

      enqueueResourceToResponse("response_null.json")
      articles = subject.articles(2)
      assertThat(articles.data).isNull()

      enqueueResourceToResponse("articles_empty.json")
      articles = subject.articles(3)
      assertThat(articles.data).isNotNull
      assertThat(articles.data.articles).isEmpty()
      assertThat(articles.data.over).isTrue

      enqueueResourceToResponse("articles_response.json")
      articles = subject.articles(4)
      assertThat(articles.data).isNotNull
      assertThat(articles.data.articles).isNotEmpty
      assertThat(articles.data.over).isFalse
      assertThat(articles.data.articles[0].title).isEqualTo("温故而知新 | 打破Handler问到底")
    }
  }

  @Test
  fun validate_popular_articles_request() {
    runBlocking {
      enqueueEmptyJsonResponse()
      subject.popArticles()
      val request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/article/top/json")
      assertThat(request.bodySize).isZero
    }
  }

  @Test
  fun validate_popular_articles_response() {
    runBlocking {
      enqueueEmptyJsonResponse()
      var popArticles = subject.popArticles()
      assertThat(popArticles.data).isNull()

      enqueueResourceToResponse("response_null.json")
      popArticles = subject.popArticles()
      assertThat(popArticles.data).isNull()

      enqueueResourceToResponse("popular_articles_response.json")
      popArticles = subject.popArticles()
      assertThat(popArticles.data).isNotNull
      assertThat(popArticles.data).isNotEmpty
      assertThat(popArticles.data[2].title).isEqualTo("每日一问 | 听说你做过内存优化 之 Bitmap内存占用到底在哪？")
    }
  }

  @Test
  fun validate_search_request() {
    runBlocking {
      enqueueEmptyJsonResponse()
      subject.search(2, "Jetpack Compose")
      var request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("POST")
      assertThat(request.path).isEqualTo("/mock/article/query/2/json")
      assertThat(request.bodySize).isNotZero
      assertThat(request.body.readUtf8()).isEqualTo("k=Jetpack%20Compose")

      enqueueEmptyJsonResponse()
      subject.search(90, "Kotlin 协程")
      request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("POST")
      assertThat(request.path).isEqualTo("/mock/article/query/90/json")
      assertThat(request.bodySize).isNotZero
      assertThat(request.body.readUtf8()).isEqualTo("k=Kotlin%20%E5%8D%8F%E7%A8%8B")
    }
  }

  @Test
  fun validate_search_response() {
    runBlocking {
      enqueueEmptyJsonResponse()
      var response = subject.search(2, "Android")
      assertThat(response.data).isNull()

      enqueueResourceToResponse("response_null.json")
      response = subject.search(2, "Android")
      assertThat(response.data).isNull()

      enqueueResourceToResponse("search_response.json")
      response = subject.search(2, "Kotlin 协程")
      assertThat(response.data).isNotNull
      assertThat(response.data.articles).isNotEmpty
      assertThat(response.data.articles[0].author).isEqualTo("郭霖")
    }
  }

  @Test
  fun validate_banner_request() {
    runBlocking {
      enqueueEmptyJsonResponse()
      subject.banner()
      val request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/banner/json")
      assertThat(request.bodySize).isZero
    }
  }

  @Test
  fun validate_banner_response() {
    runBlocking {
      enqueueEmptyJsonResponse()
      var banner = subject.banner()
      assertThat(banner.data).isNull()

      enqueueResourceToResponse("response_null.json")
      banner = subject.banner()
      assertThat(banner.data).isNull()

      enqueueResourceToResponse("banner_response.json")
      banner = subject.banner()
      assertThat(banner.data).isNotEmpty
      assertThat(banner.data[0].title).isEqualTo("声明式 UI？Android 官方怒推的 Jetpack Compose 到底是什么？")
    }
  }

  @Test
  fun validate_popular_words_request() {
    runBlocking {
      enqueueEmptyJsonResponse()
      subject.popWords()
      val request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/hotkey/json")
      assertThat(request.bodySize).isZero
    }
  }

  @Test
  fun validate_popular_words_response() {
    runBlocking {
      enqueueEmptyJsonResponse()
      var popWords = subject.popWords()
      assertThat(popWords.data).isNull()

      enqueueResourceToResponse("response_null.json")
      popWords = subject.popWords()
      assertThat(popWords.data).isNull()

      enqueueResourceToResponse("popular_words_response.json")
      popWords = subject.popWords()
      assertThat(popWords.data).isNotEmpty
      assertThat(popWords.data[3].name).isEqualTo("自定义View")
    }
  }


  @Test
  fun validate_category_request() {
    runBlocking {
      enqueueEmptyJsonResponse()
      subject.category()
      val request = fakeServer.takeRequest()
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.path).isEqualTo("/mock/tree/json")
      assertThat(request.bodySize).isZero
    }
  }

  @Test
  fun validate_category_response() {
    runBlocking {
      enqueueEmptyJsonResponse()
      var category = subject.category()
      assertThat(category.data).isNull()

      enqueueResourceToResponse("response_null.json")
      category = subject.category()
      assertThat(category.data).isNull()

      enqueueResourceToResponse("category_response.json")
      category = subject.category()
      assertThat(category.data).isNotEmpty
      assertThat(category.data[0].name).isEqualTo("开发环境")
      assertThat(category.data[0].children).isNotEmpty
      assertThat(category.data[0].children[1].name).isEqualTo("gradle")
    }
  }

  @After
  fun tearDown() {
    fakeServer.shutdown()
  }

  private fun enqueueEmptyJsonResponse() {
    fakeServer.enqueue(MockResponse().setBody("{}"))
  }

  private fun enqueueResourceToResponse(resourceName: String) {
    fakeServer.enqueue(resourceToResponse(resourceName))
  }

  private fun resourceToResponse(resourceName: String): MockResponse {
    return javaClass.classLoader!!
      .getResourceAsStream(resourceName)
      .use { MockResponse().setBody(Buffer().readFrom(it)) }
  }
}
