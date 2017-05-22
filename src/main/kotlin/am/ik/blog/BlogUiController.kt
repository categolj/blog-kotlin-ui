package am.ik.blog

import am.ik.marked4j.Marked
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*


@Controller
class BlogUiController(val categoLJ3Client: CategoLJ3Client, val marked: Marked,
                       @Value("\${blog.api.url:http://localhost:8080}") val apiUrl: String) {
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

    @GetMapping(path = arrayOf("/entries/{entryId}"), params = arrayOf("partial"))
    @ResponseBody
    fun partialById(@PathVariable("entryId") entryId: Long): String {
        val entry = categoLJ3Client.findById(entryId)
        return marked.marked(entry.content)
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
}


