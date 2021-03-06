package am.ik.blog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "blog")
@Component
public class BlogProperties {
	private boolean maintenance = false;
	private Service api;
	private Service point;

	public boolean isMaintenance() {
		return maintenance;
	}

	public void setMaintenance(boolean maintenance) {
		this.maintenance = maintenance;
	}

	public Service getApi() {
		return api;
	}

	public void setApi(Service api) {
		this.api = api;
	}

	public Service getPoint() {
		return point;
	}

	public void setPoint(Service point) {
		this.point = point;
	}

	public static class Service {
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}
