package am.ik.blog

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class Page(
        @JsonProperty("content") val content: List<Entry>,
        @JsonProperty("sort") val sort: String? = null,
        @JsonProperty("totalPages") val totalPages: Long,
        @JsonProperty("totalElements") val totalElements: Long,
        @JsonProperty("isFirst") val isFirst: Boolean,
        @JsonProperty("isLast") val isLast: Boolean,
        @JsonProperty("numberOfElements") val numberOfElements: Long,
        @JsonProperty("size") val size: Int,
        @JsonProperty("number") val number: Int
)

data class Author(
        @JsonProperty("name") val name: String,
        @JsonProperty("date") val date: OffsetDateTime)

data class Entry(
        @JsonProperty("entryId") val entryId: Long,
        @JsonProperty("content") val content: String?,
        @JsonProperty("created") val created: Author?,
        @JsonProperty("updated") val updated: Author?,
        @JsonProperty("frontMatter") val frontMatter: FrontMatter) {

    fun isPremium() = this.frontMatter.point != null

    fun isWarning() = !this.isDanger() && this.updated!!.date.isBefore(OffsetDateTime.now().minusYears(1))

    fun isDanger() = this.updated!!.date.isBefore(OffsetDateTime.now().minusYears(3))
}

data class FrontMatter(
        @JsonProperty("title") val title: String,
        @JsonProperty("tags") val tags: List<String>,
        @JsonProperty("categories") val categories: List<String>,
        @JsonInclude(JsonInclude.Include.NON_NULL) @JsonProperty("point") val point: Int?)