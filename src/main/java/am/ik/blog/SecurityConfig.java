package am.ik.blog;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http //
				.requestMatchers() //
				.antMatchers("/**") //
				.and() //
				.authorizeRequests() //
				.mvcMatchers("/cloudfoundryapplication/**", "/", "/entries/**",
						"/tags/**", "/categories/**", "/users/**", "/error", "/login",
						"/logout")
				.permitAll() //
				.mvcMatchers("/p/entries/{entryId}").permitAll() //
				.mvcMatchers("/premium/**").authenticated() //
				.antMatchers("/*.png", "/css/**", "/js/**").permitAll() //
				.anyRequest().hasRole("ADMIN") //
				.and() //
				.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())//
				.and() //
				.logout() //
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/");
	}
}
