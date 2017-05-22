package am.ik.blog;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(-10)
public class RefreshConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatchers() //
				.antMatchers(HttpMethod.POST, "/env", "/refresh", "/reload") //
				.and() //
				.authorizeRequests() //
				.anyRequest() //
				.authenticated() //
				.and() //
				.httpBasic() //
				.and() //
				.csrf().disable();
	}
}
