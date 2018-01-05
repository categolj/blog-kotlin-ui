package am.ik.blog

import am.ik.blog.point.LoginRequiredException
import am.ik.blog.point.UnsubscribedException
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Pageable
import org.springframework.http.*
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.OffsetDateTime
import java.util.function.Supplier

@Component
class CategoLJ3Client(val restTemplate: RestTemplate, val oauth2RestTemplate: OAuth2RestTemplate,
                      val props: BlogProperties) {
    val typeReference = object : ParameterizedTypeReference<Page>() {}

    fun fallbackEntry(entryId: Long): Entry {
        val fallbackUrl: String = if (entryId > 0L) "https://github.com/making/blog.ik.am/blob/master/content/${String.format("%05d", entryId)}.md" else "https://github.com/making/blog.ik.am/tree/master/content"
        return Entry(entryId = entryId,
                content = """
Wait a minute ... 🙇<br>
During this downtime, you could also see this article at GitHub directly.<br>
⏩ **[$fallbackUrl]($fallbackUrl)**<br>
<br>
Sorry about that
                    """,
                frontMatter = FrontMatter(title = "Service is unavailable now x( !", categories = emptyList(), tags = emptyList(), point = null),
                created = Author(name = "system", date = OffsetDateTime.now()),
                updated = Author(name = "system", date = OffsetDateTime.now()))
    }

    fun fallbackPage(@Suppress("UNUSED_PARAMETER") pageable: Pageable, @Suppress("UNUSED_PARAMETER") excludeContent: Boolean) = Page(content = listOf(fallbackEntry(0)), isFirst = true, isLast = true, number = 1, numberOfElements = 1, totalElements = 1, totalPages = 1, size = 1)

    fun fallbackPage(@Suppress("UNUSED_PARAMETER") dummy: String, pageable: Pageable) = fallbackPage(pageable, true)

    fun fallbackTags() = listOf("Service", "is", "unavailable", "now")

    fun fallbackCategories() = listOf(fallbackTags());

    @HystrixCommand(fallbackMethod = "fallbackPage")
    fun findAll(pageable: Pageable, excludeContent: Boolean = true): Page {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", excludeContent)
                .build()
        val req = RequestEntity.get(uri.toUri())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build()
        return restTemplate.exchange(req, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage")
    fun findByQuery(query: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "entries")
                .queryParam("q", query)
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackEntry")
    fun findByIdExcludeContent(entryId: Long): Entry {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "entries", entryId.toString())
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, Entry::class.java).body
    }

    @HystrixCommand(fallbackMethod = "fallbackEntry", ignoreExceptions = arrayOf(LoginRequiredException::class, UnsubscribedException::class))
    fun findById(entryId: Long): Entry {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "entries", entryId.toString())
                .build()
        return withHandleException(entryId, Supplier {
            restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, Entry::class.java).body
        })
    }

    @HystrixCommand(fallbackMethod = "fallbackEntry",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")),
            ignoreExceptions = arrayOf(LoginRequiredException::class, UnsubscribedException::class))
    fun findPremiumById(entryId: Long): Entry {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "p", "entries", entryId.toString())
                .build()
        return withHandleException(entryId, Supplier {
            oauth2RestTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, Entry::class.java).body
        })
    }

    @HystrixCommand(fallbackMethod = "fallbackPage")
    fun findByTag(tag: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "tags", tag, "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage")
    fun findByCategories(categories: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "categories", categories, "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage")
    fun findByCreatedBy(name: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "users", name, "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage")
    fun findByUpdatedBy(name: String, pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
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
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "tags")
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<List<String>>() {}).body
    }

    @HystrixCommand(fallbackMethod = "fallbackCategories")
    fun findCategories(): List<List<String>> {
        val uri = UriComponentsBuilder.fromUriString(props.api.url)
                .pathSegment("api", "categories")
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<List<List<String>>>() {}).body
    }

    private fun withHandleException(entryId: Long, func: Supplier<Entry>): Entry {
        try {
            return func.get()
        } catch (e: HttpClientErrorException) {
            var headers = e.responseHeaders
            if (e.statusCode == HttpStatus.UNAUTHORIZED && isOauth2ResourceError(headers)) {
                throw LoginRequiredException(entryId)
            }
            if (e.statusCode == HttpStatus.PAYMENT_REQUIRED) {
                throw UnsubscribedException(entryId)
            }
            throw e
        } catch (e: AuthenticationException) {
            throw LoginRequiredException(entryId)
        }
    }

    private fun isOauth2ResourceError(headers: HttpHeaders): Boolean {
        val www = headers.getFirst(HttpHeaders.WWW_AUTHENTICATE)
        return !StringUtils.isEmpty(www) && www.startsWith("Bearer realm=\"oauth2-resource\"")
    }
}