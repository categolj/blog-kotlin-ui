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
class ThisWeekInMakingUiController @Autowired constructor(@Qualifier("thisWeekInMakingClient") val categoLJ3Client: CategoLJ3Client, val marked: Marked,
                                                          @Value("\${blog.this-week-in-making.url:http://localhost:9832}") val apiUrl: String) {
    val log = LoggerFactory.getLogger(BlogUiController::class.java)

    @RequestMapping("/this-week-in-making")
    fun view(model: Model, @PageableDefault(size = 5) pageable: Pageable): String {
        val entries = categoLJ3Client.findAll(pageable)
        model.addAttribute("page", entries)
        return "this-week-in-making-list"
    }

    @RequestMapping("/this-week-in-making/{entryId}")
    fun byId(model: Model, @PathVariable("entryId") entryId: Long): String {
        val entry = categoLJ3Client.findById(entryId)
        model.addAttribute("entry", entry)
        return "this-week-in-making"
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


