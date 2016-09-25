package am.ik.blog

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.ExportMetricReader
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.boot.actuate.metrics.GaugeService
import org.springframework.boot.actuate.metrics.aggregate.AggregateMetricReader
import org.springframework.boot.actuate.metrics.export.MetricExportProperties
import org.springframework.boot.actuate.metrics.reader.MetricReader
import org.springframework.boot.actuate.metrics.repository.MetricRepository
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository
import org.springframework.boot.actuate.metrics.writer.DefaultCounterService
import org.springframework.boot.actuate.metrics.writer.DefaultGaugeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory

@Profile("cloud")
@Configuration
open class MetricsConfig {

    @Autowired
    var redisConnectionFactory: RedisConnectionFactory? = null

    @Autowired
    var export: MetricExportProperties? = null

    // Metrics for an instance
    @Bean
    @ExportMetricReader
    open fun metricRepository(): MetricRepository = RedisMetricRepository(redisConnectionFactory!!,
            export!!.redis.prefix, export!!.redis.key)

    @Bean
    open fun gaugeWriter(): GaugeService = DefaultGaugeService(metricRepository())

    @Bean
    open fun counterService(): CounterService = DefaultCounterService(metricRepository())

    // Metrics for all instances
    @Bean
    open fun aggregateMetricRepository(): MetricRepository = RedisMetricRepository(redisConnectionFactory!!,
            export!!.redis.aggregatePrefix, export!!.redis.key)

    @Bean
    @ExportMetricReader
    open fun aggregateMetricReader(): MetricReader = AggregateMetricReader(aggregateMetricRepository())

}