package am.ik.blog.premium;

import java.util.Map;
import java.util.Optional;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.stereotype.Component;

@Component
public class PremiumPrincipalExtractor implements PrincipalExtractor {

	@Override
	public PremiumUser extractPrincipal(Map<String, Object> map) {
		String github = getValue(map, "login");
		String email = getValue(map, "email");
		return new PremiumUser(github, email);
	}

	private String getValue(Map<String, Object> map, String key) {
		return Optional.ofNullable(map.get(key)).map(Object::toString).orElse("");
	}

}
