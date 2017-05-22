package am.ik.blog.maintenance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import am.ik.blog.BlogProperties;

@Component
@RefreshScope
public class MaintenanceInterceptor extends HandlerInterceptorAdapter {
	private final BlogProperties blogProperties;

	public MaintenanceInterceptor(BlogProperties blogProperties) {
		this.blogProperties = blogProperties;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		if (blogProperties.isMaintenance()) {
			throw new MaintenanceException();
		}
		return true;
	}
}
