package am.ik.blog;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import am.ik.marked4j.Marked;
import am.ik.marked4j.MarkedBuilder;

@Configuration
public class BlogConfig {
	@Bean
	Marked marked() {
		return new MarkedBuilder().breaks(true).build();
	}

	@Bean
    @LoadBalanced
    RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
