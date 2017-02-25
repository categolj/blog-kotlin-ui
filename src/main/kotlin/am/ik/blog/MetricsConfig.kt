package am.ik.blog

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
class MetricsConfig(val redisConnectionFactory: RedisConnectionFactory, val export: MetricExportProperties) {

    // Metrics for an instance
    @Bean
    @ExportMetricReader
    fun metricRepository(): MetricRepository = RedisMetricRepository(redisConnectionFactory,
            export.redis.prefix, export.redis.key)

    @Bean
    fun gaugeWriter(): GaugeService = DefaultGaugeService(metricRepository())

    @Bean
    fun counterService(): CounterService = DefaultCounterService(metricRepository())

    // Metrics for all instances
    @Bean
    fun aggregateMetricRepository(): MetricRepository = RedisMetricRepository(redisConnectionFactory,
            export.redis.aggregatePrefix, export.redis.key)

    @Bean
    @ExportMetricReader
    fun aggregateMetricReader(): MetricReader = AggregateMetricReader(aggregateMetricRepository())

}