package am.ik.blog

import am.ik.blog.page.EntryPage
import am.ik.blog.page.TagsPage
import am.ik.blog.page.TopPage
import com.codeborne.selenide.Configuration
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import com.codeborne.selenide.Condition.*
import com.codeborne.selenide.Selenide.*

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(classes = arrayOf(BlogKotlinUiApplication::class))
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("no-graphite")
class BlogKotlinUiApplicationTests {
    @Value("\${local.server.port}")
    var port: Int = 0
    @Value("\${WAIT_MILLIS:0}")
    var waitMillis: Long = 0


    @Before
    fun setup() {
        Configuration.baseUrl = "http://localhost:$port"
    }

    @Test
    fun checkTopPage() {
        val topPage = open("/", TopPage::class.java)
        val articles = topPage.articles
        if (waitMillis > 0) Thread.sleep(waitMillis)
        articles[0].title.shouldBe(text("Hello Spring Boot"))
        articles[0].tags[0].shouldHave(text("Java"))
        articles[0].tags[1].shouldHave(text("Spring"))
        articles[0].tags[2].shouldHave(text("SpringBoot"))
        articles[0].read().shouldHave(text("Spring Boot!"))

        articles[1].title.shouldBe(text("Hello Java8"))
        articles[1].tags[0].shouldHave(text("Java"))
        articles[1].tags[1].shouldHave(text("Java8"))
        articles[1].tags[2].shouldHave(text("Stream"))
        articles[1].read().shouldHave(text("Java8!"))
    }

    @Test
    fun checkEntryPage() {
        val entryPage = open("/entries/1", EntryPage::class.java)
        val article = entryPage.article
        if (waitMillis > 0) Thread.sleep(waitMillis)
        article.title.shouldBe(text("Hello Java8"))
        article.tags[0].shouldHave(text("Java"))
        article.tags[1].shouldHave(text("Java8"))
        article.tags[2].shouldHave(text("Stream"))
    }

    @Test
    fun checkTagsPage() {
        val tagPage = open("/tags", TagsPage::class.java)
        val tags = tagPage.tags
        if (waitMillis > 0) Thread.sleep(waitMillis)
        tags[0].shouldHave(text("Java"))
        tags[1].shouldHave(text("Java8"))
        tags[2].shouldHave(text("Spring"))
        tags[3].shouldHave(text("SpringBoot"))
        tags[4].shouldHave(text("Stream"))
    }
}
