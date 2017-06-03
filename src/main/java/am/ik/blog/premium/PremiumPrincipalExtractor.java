package am.ik.blog.premium;

import java.util.Map;
import java.util.Optional;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.stereotype.Component;

import am.ik.blog.point.BlogPointClient;

@Component
public class PremiumPrincipalExtractor implements PrincipalExtractor {
	private final BlogPointClient blogPointClient;

	public PremiumPrincipalExtractor(BlogPointClient blogPointClient) {
		this.blogPointClient = blogPointClient;
	}

	@Override
	public PremiumUser extractPrincipal(Map<String, Object> map) {
		String github = getValue(map, "login");
		String email = getValue(map, "email");
		return new PremiumUser(github, email, blogPointClient);
	}

	private String getValue(Map<String, Object> map, String key) {
		return Optional.ofNullable(map.get(key)).map(Object::toString).orElse("");
	}

}
