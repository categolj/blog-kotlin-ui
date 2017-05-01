package am.ik.blog

import am.ik.marked4j.MarkedBuilder
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BlogConfig {
    @Bean
    fun marked() = MarkedBuilder().breaks(true).build()
}

@Configuration
class RestTemplateConfig {
    @LoadBalanced
    @Bean
    fun restTemplate(builder: RestTemplateBuilder) = builder
            .build()
}