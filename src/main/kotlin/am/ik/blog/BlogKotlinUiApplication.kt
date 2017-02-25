package am.ik.blog

import am.ik.marked4j.MarkedBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter
import org.springframework.boot.actuate.metrics.export.MetricExportProperties
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
class BlogKotlinUiApplication {
    @Bean
    fun marked() = MarkedBuilder().breaks(true).build()

    @Bean
    @Profile("cloud")
    @ExportMetricWriter
    fun redisMetricWriter(export: MetricExportProperties, connectionFactory: RedisConnectionFactory): RedisMetricRepository {
        return RedisMetricRepository(connectionFactory, export.redis.prefix, export.redis.key);
    }

    @Bean
    fun blogClient(restTemplate: RestTemplate, accessCounter: AccessCounter,
                   @Value("\${blog.api.url:http://localhost:8080}") apiUrl: String): CategoLJ3Client {
        return CategoLJ3Client(restTemplate, accessCounter, apiUrl)
    }
}

@Configuration
@Profile("!no-ribbon")
class RestTemplateConfig {
    @LoadBalanced
    @Bean
    fun restTemplate() = RestTemplate()
}

@Configuration
@Profile("no-ribbon") // for stand-alone dev
class RestTemplateConfigNoRibbon {
    @Bean
    fun restTemplate() = RestTemplate()
}

fun main(args: Array<String>) {
    SpringApplication.run(BlogKotlinUiApplication::class.java, *args)
}