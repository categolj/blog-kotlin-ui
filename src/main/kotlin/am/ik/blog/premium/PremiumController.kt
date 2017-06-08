package am.ik.blog.premium;

import am.ik.blog.CategoLJ3Client
import am.ik.blog.point.BlogPointClient
import am.ik.blog.point.ConsumePoint
import am.ik.blog.point.SubscriptionFailedException
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.messaging.Source
import org.springframework.http.ResponseEntity
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*

@Controller
@RequestMapping("/premium")
class PremiumController(val source: Source, val categoLJ3Client: CategoLJ3Client,
                        val blogPointClient: BlogPointClient) {
    private val log = LoggerFactory.getLogger(PremiumController::class.java)


    @GetMapping
    fun premium(principal: Principal): String {
        return "premium/index"
    }

    @PostMapping(path = arrayOf("/subscribe"))
    @ResponseBody
    fun subscribe(@RequestBody point: ConsumePoint,
                  @AuthenticationPrincipal user: PremiumUser): String {
        val (entryId, content, created, updated, frontMatter) = categoLJ3Client.findByIdExcludeContent(point.entryId!!)
        point.amount = -(frontMatter.point)!!
        point.username = user.github
        val message = MessageBuilder.withPayload(point)
                .setHeader("eventType", "consume").build()
        log.info("Send {}", message)
        this.blogPointClient.checkConsumption(point)
        this.source.output().send(message)
        return "OK"
    }

    @ExceptionHandler(SubscriptionFailedException::class)
    fun handleCannotConsumeException(e: SubscriptionFailedException): ResponseEntity<Any> {
        val map = LinkedHashMap<String, String?>()
        map.put("message", e.message)
        map.put("detail", e.detail())
        return ResponseEntity.status(e.statusCode()).body<Any>(map)
    }
}
