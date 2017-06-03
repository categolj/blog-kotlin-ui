package am.ik.blog.premium;

import java.io.Serializable;

import am.ik.blog.point.BlogPoint;
import am.ik.blog.point.BlogPointClient;

public class PremiumUser implements Serializable {
	private final String github;
	private final String email;
	private final BlogPointClient blogPointClient;
	private BlogPoint lastPoint;

	public PremiumUser(String github, String email, BlogPointClient blogPointClient) {
		this.github = github;
		this.email = email;
		this.blogPointClient = blogPointClient;
	}

	public String getGithub() {
		return github;
	}

	public String getEmail() {
		return email;
	}

	public BlogPoint getPoint() {
		BlogPoint point = blogPointClient.getPoint();
		if (point == BlogPoint.FAILURE && lastPoint != null) {
			return lastPoint;
		}
		lastPoint = point;
		return point;
	}

	@Override
	public String toString() {
		return "PremiumUser{" + "github='" + github + '\'' + ", email='" + email + '\''
				+ '}';
	}
}
