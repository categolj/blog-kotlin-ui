package am.ik.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import am.ik.blog.maintenance.MaintenanceInterceptor;
import am.ik.blog.metrics.UserAgentMetricsInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	private final MaintenanceInterceptor maintenanceInterceptor;
	private final UserAgentMetricsInterceptor userAgentMetricsInterceptor;

	public MvcConfig(MaintenanceInterceptor maintenanceInterceptor,
			UserAgentMetricsInterceptor userAgentMetricsInterceptor) {
		this.maintenanceInterceptor = maintenanceInterceptor;
		this.userAgentMetricsInterceptor = userAgentMetricsInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(maintenanceInterceptor);
		registry.addInterceptor(userAgentMetricsInterceptor) //
				.excludePathPatterns("/js/**", "/css/**", "/*.png");
	}
}
