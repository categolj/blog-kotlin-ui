package am.ik.blog

import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class ObsoleteChecker {
    fun isWarning(entry: Entry) = entry.updated!!.date.isBefore(OffsetDateTime.now().minusYears(1))

    fun isDanger(entry: Entry) = entry.updated!!.date.isBefore(OffsetDateTime.now().minusYears(3))
}