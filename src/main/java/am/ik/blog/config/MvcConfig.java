package am.ik.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import am.ik.blog.maintenance.MaintenanceInterceptor;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
	private final MaintenanceInterceptor maintenanceInterceptor;

	public MvcConfig(MaintenanceInterceptor maintenanceInterceptor) {
		this.maintenanceInterceptor = maintenanceInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(maintenanceInterceptor);
	}
}
