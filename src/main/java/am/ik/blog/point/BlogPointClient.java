package am.ik.blog.point;

import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import am.ik.blog.BlogProperties;

@Component
public class BlogPointClient {
	private final OAuth2RestTemplate oauth2RestTemplate;
	private final BlogProperties props;

	public BlogPointClient(OAuth2RestTemplate oauth2RestTemplate, BlogProperties props) {
		this.oauth2RestTemplate = oauth2RestTemplate;
		this.props = props;
	}

	@HystrixCommand(fallbackMethod = "getPointFallback")
	public BlogPoint getPoint() {
		return this.oauth2RestTemplate
				.getForObject(props.getPoint().getUrl() + "/v1/user", BlogPoint.class);
	}

	public BlogPoint getPointFallback() {
		return BlogPoint.FAILURE;
	}
}
