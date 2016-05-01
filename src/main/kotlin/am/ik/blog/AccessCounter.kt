package am.ik.blog

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.stereotype.Component

@Component
class AccessCounter @Autowired constructor(val counterService: CounterService) {
    open fun countEntry(entryId: Long) = counterService.increment("blog.entry." + entryId)
}