package am.ik.blog.metrics;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import is.tagomor.woothee.Classifier;
import is.tagomor.woothee.DataSet;

@Component
public class UserAgentMetricsInterceptor extends HandlerInterceptorAdapter {

	final CounterService counterService;

	public UserAgentMetricsInterceptor(CounterService counterService) {
		this.counterService = counterService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		Map r = Classifier.parse(request.getHeader(HttpHeaders.USER_AGENT));
		String name = (String) r.get(DataSet.DATASET_KEY_NAME);
		String category = (String) r.get(DataSet.DATASET_KEY_CATEGORY);

		if (!StringUtils.isEmpty(name)) {
			counterService.increment(counterName("name", name));
		}
		if (!StringUtils.isEmpty(category)) {
			counterService.increment(counterName("category", category));
		}
		return true;
	}

	private String counterName(String type, String attr) {
		return "useragent." + type + "." + attr.toLowerCase().replace(" ", "");
	}
}
