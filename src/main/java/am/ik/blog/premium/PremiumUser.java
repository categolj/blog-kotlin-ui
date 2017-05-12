package am.ik.blog.premium;

import java.io.Serializable;

public class PremiumUser implements Serializable {
	private final String github;
	private final String email;

	public PremiumUser(String github, String email) {
		this.github = github;
		this.email = email;
	}

	public String getGithub() {
		return github;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "PremiumUser{" + "github='" + github + '\'' + ", email='" + email + '\''
				+ '}';
	}
}
