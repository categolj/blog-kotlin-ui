package am.ik.blog

import am.ik.marked4j.Marked
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException


@Controller
class BlogUiController @Autowired constructor(@Qualifier("blogClient") val categoLJ3Client: CategoLJ3Client, val marked: Marked,
                                              @Value("\${blog.api.url:http://localhost:8080}") val apiUrl: String) {
    val log = LoggerFactory.getLogger(BlogUiController::class.java)

    @RequestMapping(value = *arrayOf("/", "/entries"))
    fun home(model: Model, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findAll(pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(value = *arrayOf("/", "/entries"), params = arrayOf("q"))
    fun search(model: Model, @RequestParam("q") query: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByQuery(query, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/entries/{entryId}"))
    fun byId(model: Model, @PathVariable("entryId") entryId: Long): String {
        val entry = categoLJ3Client.findById(entryId)
        model.addAttribute("entry", entry)
        return "entry"
    }

    @RequestMapping(path = arrayOf("/entries/{entryId}"), params = arrayOf("partial"))
    @ResponseBody
    fun partialById(@PathVariable("entryId") entryId: Long): String {
        val entry = categoLJ3Client.findById(entryId)
        return marked.marked(entry.content)
    }

    @RequestMapping(path = arrayOf("/tags/{tag}/entries"))
    fun byTag(model: Model, @PathVariable("tag") tag: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByTag(tag, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/categories/{categories}/entries"))
    fun byCategories(model: Model, @PathVariable("categories") categories: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByCategories(categories, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/users/{name}/entries"))
    fun byCreatedBy(model: Model, @PathVariable("name") name: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByCreatedBy(name, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/users/{name}/entries"), params = arrayOf("updated"))
    fun byUpdatedBy(model: Model, @PathVariable("name") name: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByUpdatedBy(name, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping(path = arrayOf("/tags"))
    fun tags(model: Model): String {
        val tags = categoLJ3Client.findTags()
        model.addAttribute("tags", tags)
        return "tags"
    }

    @RequestMapping(path = arrayOf("/categories"))
    fun categories(model: Model): String {
        val categories = categoLJ3Client.findCategories()
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


