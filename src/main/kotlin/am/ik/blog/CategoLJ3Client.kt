package am.ik.blog

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.OffsetDateTime

@Component
class CategoLJ3Client(val restTemplate: RestTemplate, val accessCounter: AccessCounter,
                      @Value("\${blog.api.url:http://localhost:8080}") val apiUrl: String) {
    val typeReference = object : ParameterizedTypeReference<Page>() {}

    fun fallbackEntry(entryId: Long) = Entry(entryId = entryId,
            content = "Wait a minute...",
            frontMatter = FrontMatter(title = "Service is unavailable now x( !", categories = emptyList(), tags = emptyList()),
            created = Author(name = "system", date = OffsetDateTime.now()),
            updated = Author(name = "system", date = OffsetDateTime.now()))

    fun fallbackPage(@Suppress("UNUSED_PARAMETER") pageable: Pageable, @Suppress("UNUSED_PARAMETER") excludeContent: Boolean) = Page(content = listOf(fallbackEntry(0)), isFirst = true, isLast = true, number = 1, numberOfElements = 1, totalElements = 1, totalPages = 1, size = 1)

    fun fallbackPage(@Suppress("UNUSED_PARAMETER") dummy: String, pageable: Pageable) = fallbackPage(pageable, true)

    fun fallbackTags() = listOf("Service", "is", "unavailable", "now")

    fun fallbackCategories() = listOf(fallbackTags());

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    fun findAll(pageable: Pageable, excludeContent: Boolean = true): Page {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", excludeContent)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    fun findByQuery(query: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "entries")
                .queryParam("q", query)
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackEntry")
    fun findById(entryId: Long): Entry {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "entries", entryId.toString())
                .build()
        accessCounter.countEntry(entryId)
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, Entry::class.java).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    fun findByTag(tag: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "tags", tag, "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    fun findByCategories(categories: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "categories", categories, "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    fun findByCreatedBy(name: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "users", name, "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    fun findByUpdatedBy(name: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "users", name, "entries")
                .queryParam("updated")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackTags")
    fun findTags(): List<String> {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "tags")
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<List<String>>() {}).body
    }

    @HystrixCommand(fallbackMethod = "fallbackCategories")
    fun findCategories(): List<List<String>> {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "categories")
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<List<List<String>>>() {}).body
    }
}