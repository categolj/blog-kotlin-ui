package am.ik.blog

import am.ik.marked4j.MarkedBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
class BlogKotlinUiApplication {
    @Bean
    fun marked() = MarkedBuilder().breaks(true).build()
}

@Configuration
@Profile("!no-ribbon")
class RestTemplateConfig {
    @LoadBalanced
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.interceptors(arrayListOf(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0")
            execution.execute(request, body)
        })).build()
    }
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