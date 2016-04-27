package am.ik.blog

import am.ik.marked4j.Marked
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

import java.time.OffsetDateTime


@Controller
class BlogUiController @Autowired constructor(val blogClient: BlogClient, val marked: Marked,
                                              @Value("\${blog.api.url:http://localhost:8080}") val apiUrl: String) {
    val log = LoggerFactory.getLogger(BlogUiController::class.java)

    @RequestMapping(value = *arrayOf("/", "/entries"))
    fun home(model: Model, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = blogClient.findAll(pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(value = *arrayOf("/", "/entries"), params = arrayOf("q"))
    fun search(model: Model, @RequestParam("q") query: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = blogClient.findByQuery(query, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/entries/{entryId}"))
    fun byId(model: Model, @PathVariable("entryId") entryId: Long): String {
        val entry = blogClient.findById(entryId)
        model.addAttribute("entry", entry)
        return "entry"
    }

    @RequestMapping(path = arrayOf("/entries/{entryId}"), params = arrayOf("partial"))
    @ResponseBody
    fun partialById(@PathVariable("entryId") entryId: Long): String {
        val entry = blogClient.findById(entryId)
        return marked.marked(entry.content)
    }

    @RequestMapping(path = arrayOf("/tags/{tag}/entries"))
    fun byTag(model: Model, @PathVariable("tag") tag: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = blogClient.findByTag(tag, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/categories/{categories}/entries"))
    fun byCategories(model: Model, @PathVariable("categories") categories: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = blogClient.findByCategories(categories, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/users/{name}/entries"))
    fun byCreatedBy(model: Model, @PathVariable("name") name: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = blogClient.findByCreatedBy(name, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/users/{name}/entries"), params = arrayOf("updated"))
    fun byUpdatedBy(model: Model, @PathVariable("name") name: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = blogClient.findByUpdatedBy(name, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/tags"))
    fun tags(model: Model): String {
        val tags = blogClient.findTags()
        model.addAttribute("tags", tags)
        return "tags"
    }

    @RequestMapping(path = arrayOf("/categories"))
    fun categories(model: Model): String {
        val categories = blogClient.findCategories()
        model.addAttribute("categories", categories)
        return "categories"
    }

    @ExceptionHandler(ResourceAccessException::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun handleResourceAccessException(e: ResourceAccessException): String {
        log.error("Cannot access " + apiUrl, e)
        return "error/api-server-down"
    }

    @ExceptionHandler(HttpServerErrorException::class)
    fun handleHttpServerErrorException(e: HttpServerErrorException): String {
        log.error("API Server Error", e)
        return "error/api-server-error"
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): String {
        log.error("Unexpected Error", e)
        return "error/unexpected-error"
    }
}


@Component
open class BlogClient @Autowired constructor(val restTemplate: RestTemplate,
                                             @Value("\${blog.api.url:http://localhost:8080}") val apiUrl: String) {
    val typeReference = object : ParameterizedTypeReference<Page>() {}

    fun fallbackEntry(entryId: Long) = Entry(entryId = entryId,
            content = "Wait a minute...",
            frontMatter = FrontMatter(title = "Service is unavailable now x( !", categories = emptyList(), tags = emptyList()),
            created = Author(name = "system", date = OffsetDateTime.now()),
            updated = Author(name = "system", date = OffsetDateTime.now()))

    fun fallbackPage(@Suppress("UNUSED_PARAMETER") pageable: Pageable) = Page(content = listOf(fallbackEntry(0)), isFirst = true, isLast = true, number = 1, numberOfElements = 1, totalElements = 1, totalPages = 1, size = 1)

    fun fallbackPage(@Suppress("UNUSED_PARAMETER") dummy: String, pageable: Pageable) = fallbackPage(pageable)

    fun fallbackTags() = listOf("Service", "is", "unavailable", "now")

    fun fallbackCategories() = listOf(fallbackTags());

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    open fun findAll(pageable: Pageable): Page {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "entries")
                .queryParam("page", pageable.pageNumber)
                .queryParam("size", pageable.pageSize)
                .queryParam("excludeContent", true)
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeReference).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    open fun findByQuery(query: String, pageable: Pageable): Page {
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
    open fun findById(entryId: Long): Entry {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "entries", entryId.toString())
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, Entry::class.java).body
    }

    @HystrixCommand(fallbackMethod = "fallbackPage",
            commandProperties = arrayOf(HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")))
    open fun findByTag(tag: String, pageable: Pageable): Page {
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
    open fun findByCategories(categories: String, pageable: Pageable): Page {
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
    open fun findByCreatedBy(name: String, pageable: Pageable): Page {
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
    open fun findByUpdatedBy(name: String, pageable: Pageable): Page {
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
    open fun findTags(): List<String> {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "tags")
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<List<String>>() {}).body
    }

    @HystrixCommand(fallbackMethod = "fallbackCategories")
    open fun findCategories(): List<List<String>> {
        val uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("api", "categories")
                .build()
        return restTemplate.exchange(uri.toUri(), HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<List<List<String>>>() {}).body
    }
}
