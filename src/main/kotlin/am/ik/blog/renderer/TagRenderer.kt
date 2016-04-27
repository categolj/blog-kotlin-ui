package am.ik.blog.renderer

import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Component
class TagRenderer {

    fun render(tags: List<String>): String {
        val builder = ServletUriComponentsBuilder.fromCurrentContextPath()
        return tags.map {
            """
            <span class="button is-primary is-outlined is-small">
                <a href="${builder.replacePath("/tags/{tag}/entries").buildAndExpand(it)}">$it</a>
            </span>
             """
        }.joinToString(separator = "&nbsp;")
    }

    fun renderOne(tag: String): String {
        val builder = ServletUriComponentsBuilder.fromCurrentContextPath()
        return """
        <i class="fa fa-circle"></i>&nbsp;
        <span class="button is-primary is-outlined is-small">
            <a href="${builder.replacePath("/tags/{tag}/entries").buildAndExpand(tag)}">$tag</a>
        </span>
        """
    }
}