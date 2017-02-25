package am.ik.blog

import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.stereotype.Component

@Component
class AccessCounter(val counterService: CounterService) {
    fun countEntry(entryId: Long) = counterService.increment("blog.entry." + entryId)
}