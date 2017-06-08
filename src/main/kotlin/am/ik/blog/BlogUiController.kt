package am.ik.blog

import am.ik.blog.point.LoginRequiredException
import am.ik.blog.point.UnsubscribedException
import am.ik.marked4j.Marked
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.security.Principal
import java.util.*
import javax.servlet.http.HttpServletRequest


@Controller
class BlogUiController(val categoLJ3Client: CategoLJ3Client, val marked: Marked) {
    val log = LoggerFactory.getLogger(BlogUiController::class.java)

    @GetMapping("/", "/entries")
    fun home(model: Model, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findAll(pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @GetMapping(path = arrayOf("/", "/entries"), params = arrayOf("q"))
    fun search(model: Model, @RequestParam("q") query: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByQuery(query, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @RequestMapping("/entries/{entryId}")
    fun byId(model: Model, @PathVariable("entryId") entryId: Long): String {
        val entry = categoLJ3Client.findById(entryId)
        model.addAttribute("entry", entry)
        return "entry"
    }

    @RequestMapping("/p/entries/{entryId}")
    fun premiumById(model: Model, @PathVariable("entryId") entryId: Long, @AuthenticationPrincipal principal: Principal?): String {
        if (principal == null) {
            throw LoginRequiredException(entryId)
        }
        val entry = categoLJ3Client.findPremiumById(entryId)
        model.addAttribute("entry", entry)
        return "entry"
    }

    @GetMapping(path = arrayOf("/entries/{entryId}"), params = arrayOf("partial"))
    @ResponseBody
    fun partialById(@PathVariable("entryId") entryId: Long, builder: UriComponentsBuilder): ResponseEntity<Any> {
        try {
            val entry = categoLJ3Client.findById(entryId)
            return ResponseEntity.ok(marked.marked(entry.content))
        } catch (e: LoginRequiredException) {
            val uri = builder.pathSegment("p", "entries", entryId.toString()).queryParam("partial").build().toUri()
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(uri).build()
        }
    }

    @GetMapping(path = arrayOf("/p/entries/{entryId}"), params = arrayOf("partial"))
    @ResponseBody
    fun premiumPartialById(@PathVariable("entryId") entryId: Long, builder: UriComponentsBuilder): ResponseEntity<Any> {
        val entry = categoLJ3Client.findPremiumById(entryId)
        return ResponseEntity.ok(marked.marked(entry.content))
    }

    @GetMapping("/tags/{tag}/entries")
    fun byTag(model: Model, @PathVariable("tag") tag: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByTag(tag, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @GetMapping("/categories/{categories}/entries")
    fun byCategories(model: Model, @PathVariable("categories") categories: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByCategories(categories, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @GetMapping("/users/{name}/entries")
    fun byCreatedBy(model: Model, @PathVariable("name") name: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByCreatedBy(name, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @GetMapping(path = arrayOf("/users/{name}/entries"), params = arrayOf("updated"))
    fun byUpdatedBy(model: Model, @PathVariable("name") name: String, @PageableDefault(size = 10) pageable: Pageable): String {
        val entries = categoLJ3Client.findByUpdatedBy(name, pageable)
        model.addAttribute("page", entries)
        return "index"
    }

    @GetMapping("/tags")
    fun tags(model: Model): String {
        val tags = categoLJ3Client.findTags()
        model.addAttribute("tags", tags)
        return "tags"
    }

    @GetMapping("/categories")
    fun categories(model: Model): String {
        val categories = categoLJ3Client.findCategories()
        model.addAttribute("categories", categories)
        return "categories"
    }

    @ExceptionHandler(LoginRequiredException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleLoginRequiredException(e: LoginRequiredException, model: Model, req: HttpServletRequest): String {
        val entry = categoLJ3Client.findByIdExcludeContent(e.entryId())
        model.addAttribute("entry", entry)
        return Optional.ofNullable(req.getParameter("partial"))
                .map { "error/partial/login-required" }
                .orElse("error/login-required")
    }

    @ExceptionHandler(UnsubscribedException::class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    fun handleUnsubscribedException(e: UnsubscribedException, model: Model, req: HttpServletRequest): String {
        val entry = categoLJ3Client.findByIdExcludeContent(e.entryId())
        model.addAttribute("entry", entry)
        return Optional.ofNullable(req.getParameter("partial"))
                .map { "error/partial/unsubscribed" }
                .orElse("error/unsubscribed")
    }
}


