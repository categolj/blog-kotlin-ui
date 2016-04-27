package am.ik.blog.renderer

import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@Component
class CategoriesRenderer {

    fun render(categories: List<String>): String {
        val builder = ServletUriComponentsBuilder.fromCurrentContextPath()
        val ret = ArrayList<String>(categories.size)
        val buf = ArrayList<String>(categories.size)
        for (category in categories) {
            buf.add(category)
            builder.replacePath("/categories/{categories}/entries")
            ret.add("""<a href="${builder.buildAndExpand(buf.joinToString(","))}">$category</a>""")
        }
        return ret.joinToString("::")
    }
}