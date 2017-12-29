package am.ik.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

@SpringBootApplication
@EnableCircuitBreaker
@EnableBinding(Source.class)
public class BlogUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogUiApplication.class, args);
	}
}