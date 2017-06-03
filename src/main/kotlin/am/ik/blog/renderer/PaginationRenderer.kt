package am.ik.blog.renderer

import am.ik.blog.Page
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.util.UriUtils

@Component
class PaginationRenderer {

    fun render(page: Page): String {
        return Pager(page).render()
    }

    class Pager(val page: Page) {
        val maxDisplayCount = 5

        fun render(): String {
            val sb = StringBuilder()
            val current = page.number.toLong()
            val beginAndEnd = calcBeginAndEnd()
            val builder = ServletUriComponentsBuilder.fromCurrentRequest()

            sb.append("<li>")
            builder.replaceQueryParam("page", 0)
            sb.append("<a ")
            if (page.isFirst) {
                sb.append("class=\"pagination-link is-disabled\" ")
            } else {
                sb.append("class=\"pagination-link\" ")
            }
            sb.append("href=\"")
            sb.append(UriUtils.decode(builder.build().toString(), "UTF-8"))
            sb.append("\">")
            sb.append("&lt;&lt;")
            sb.append("</a>")
            sb.append("</li>")
            for (p in beginAndEnd.begin..beginAndEnd.end) {
                val active = p == current
                builder.replaceQueryParam("page", p)
                sb.append("<li>")
                sb.append("<a ")
                if (active) {
                    sb.append("class=\"pagination-link is-small is-current\" ")
                } else {
                    sb.append("class=\"pagination-link is-small\" ")
                }
                sb.append("href=\"")
                sb.append(UriUtils.decode(builder.build().toString(), "UTF-8"))
                sb.append("\">")
                sb.append(p + 1)
                if (!active) {
                    sb.append("</a>")
                }
                sb.append("</li>")
            }
            sb.append("<li>")
            builder.replaceQueryParam("page", page.totalPages - 1)
            sb.append("<a ")
            if (page.isLast) {
                sb.append("class=\"pagination-link is-small is-disabled\" ")
            } else {
                sb.append("class=\"pagination-link is-small\" ")
            }
            sb.append("href=\"")
            sb.append(UriUtils.decode(builder.build().toString(), "UTF-8"))
            sb.append("\">")
            sb.append("&gt;&gt;")
            sb.append("</a>")
            sb.append("</li>")
            return sb.toString()
        }

        fun calcBeginAndEnd(): BeginAndEnd {
            var begin = Math.max(0, this.page.number - Math.floor(this.maxDisplayCount * 0.5).toInt()).toLong()
            var end = begin + this.maxDisplayCount - 1
            val last = this.page.totalPages - 1
            if (end > last) {
                end = last
                begin = Math.max(0, end - (this.maxDisplayCount - 1))
            }
            return BeginAndEnd(begin, end)
        }
    }
}

data class BeginAndEnd constructor(val begin: Long, val end: Long)