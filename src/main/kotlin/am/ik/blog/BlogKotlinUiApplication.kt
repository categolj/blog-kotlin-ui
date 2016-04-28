package am.ik.blog

import am.ik.marked4j.MarkedBuilder
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.graphite.Graphite
import com.codahale.metrics.graphite.GraphiteReporter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate
import java.net.URL
import java.util.concurrent.TimeUnit

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
open class BlogKotlinUiApplication {
    @Bean
    open fun marked() = MarkedBuilder().breaks(true).build()


    @Bean
    open fun graphiteReporter(@Value("\${HOSTEDGRAPHITE_APIKEY:\${vcap.services.hostedgraphite.credentials.apikey:}}") prefix: String?,
                              @Value("\${HOSTEDGRAPHITE_URL:\${vcap.services.hostedgraphite.credentials.url:http://localhost}}") url: URL,
                              @Value("\${HOSTEDGRAPHITE_PORT:\${vcap.services.hostedgraphite.credentials.port:0}}") port: Int,
                              registry: MetricRegistry): GraphiteReporter {
        java.security.Security.setProperty("networkaddress.cache.ttl", "60")
        val reporter = GraphiteReporter.forRegistry(registry).prefixedWith(prefix).build(Graphite(url.host, port))
        reporter.start(1, TimeUnit.SECONDS)
        return reporter
    }
}

@Configuration
@Profile("!no-ribbon")
open class RestTemplateConfig {
    @LoadBalanced
    @Bean
    open fun restTemplate() = RestTemplate()
}

@Configuration
@Profile("no-ribbon") // for stand-alone dev
open class RestTemplateConfigNoRibbon {
    @Bean
    open fun restTemplate() = RestTemplate()
}

fun main(args: Array<String>) {
    SpringApplication.run(BlogKotlinUiApplication::class.java, *args)
}