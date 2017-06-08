package am.ik.blog.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import am.ik.blog.BlogProperties;

@Component
public class BlogPointClient {
	private final Logger log = LoggerFactory.getLogger(BlogPointClient.class);
	private final OAuth2RestTemplate oauth2RestTemplate;
	private final BlogProperties props;
	private final ObjectMapper objectMapper;

	public BlogPointClient(OAuth2RestTemplate oauth2RestTemplate, BlogProperties props,
			ObjectMapper objectMapper) {
		this.oauth2RestTemplate = oauth2RestTemplate;
		this.props = props;
		this.objectMapper = objectMapper;
	}

	@HystrixCommand(fallbackMethod = "getPointFallback")
	public BlogPoint getPoint() {
		return this.oauth2RestTemplate
				.getForObject(props.getPoint().getUrl() + "/v1/user", BlogPoint.class);
	}

	@HystrixCommand(fallbackMethod = "checkConsumptionFallback", ignoreExceptions = {
			SubscriptionFailedException.class, AuthenticationException.class })
	public void checkConsumption(ConsumePoint consumePoint) {
		try {
			this.oauth2RestTemplate.postForObject(
					props.getPoint().getUrl() + "/v1/check_consumption", consumePoint,
					String.class);
		}
		catch (HttpClientErrorException e) {
			throw new SubscriptionFailedException(
					"Subscription failed (entry " + consumePoint.getEntryId() + ") 😭.",
					detail(e), e.getRawStatusCode());
		}
	}

	private String detail(HttpClientErrorException e) {
		try {
			JsonNode node = objectMapper.readValue(e.getResponseBodyAsByteArray(),
					JsonNode.class);
			return node.has("message") ? node.get("message").asText() : "";
		}
		catch (Exception ignored) {
			return "";
		}
	}

	public BlogPoint getPointFallback() {
		return BlogPoint.FAILURE;
	}

	public void checkConsumptionFallback(ConsumePoint consumePoint) {
		log.warn("cannot check consumption. subscribe anyway. ({})", consumePoint);
	}
}
