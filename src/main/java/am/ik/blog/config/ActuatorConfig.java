package am.ik.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@ConfigurationProperties("security")
@Order(-5)
public class ActuatorConfig extends WebSecurityConfigurerAdapter {
	private User user;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.regexMatcher("/health|/info|/env|/configprops|/beans|/dump|/loggers") //
				.authorizeRequests() //
				.mvcMatchers("/health", "/info").permitAll() //
				.mvcMatchers("/env", "/configprops", "/beans", "/dump", "/loggers")
				.hasRole("ACTUATOR") //
				.and() //
				.httpBasic() //
				.and() //
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //
				.and() //
				.csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication() //
				.withUser(this.user.name).password(this.user.password).roles("ACTUATOR");
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static class User {
		private String name = "user";
		private String password = "password";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
