package am.ik.blog.premium;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import am.ik.blog.CategoLJ3Client;
import am.ik.blog.Entry;
import am.ik.blog.point.BlogPointClient;
import am.ik.blog.point.ConsumePoint;
import am.ik.blog.point.SubscriptionFailedException;

@Controller
@RequestMapping(path = "/premium")
public class PremiumController {
	private final Logger log = LoggerFactory.getLogger(PremiumController.class);
	private final Source source;
	private final CategoLJ3Client categoLJ3Client;
	private final BlogPointClient blogPointClient;

	public PremiumController(Source source, CategoLJ3Client categoLJ3Client,
			BlogPointClient blogPointClient) {
		this.source = source;
		this.categoLJ3Client = categoLJ3Client;
		this.blogPointClient = blogPointClient;
	}

	@GetMapping
	String premium(Principal principal) {
		return "premium/index";
	}

	@PostMapping(path = "/subscribe")
	@ResponseBody
	String subscribe(@RequestBody ConsumePoint point,
			@AuthenticationPrincipal PremiumUser user) {
		Entry entry = categoLJ3Client.findByIdExcludeContent(point.getEntryId());
		point.setAmount(-entry.getFrontMatter().getPoint());
		point.setUsername(user.getGithub());
		Message<ConsumePoint> message = MessageBuilder.withPayload(point)
				.setHeader("eventType", "consume").build();
		log.info("Send {}", message);
		this.blogPointClient.checkConsumption(point);
		this.source.output().send(message);
		return "OK";
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(SubscriptionFailedException.class)
	ResponseEntity<Object> handleCannotConsumeException(SubscriptionFailedException e) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("message", e.getMessage());
		map.put("detail", e.detail());
		return ResponseEntity.status(e.statusCode()).body(map);
	}
}
