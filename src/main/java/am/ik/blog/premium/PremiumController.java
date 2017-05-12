package am.ik.blog.premium;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/premium")
public class PremiumController {
	@GetMapping
	String premium(Principal principal) {
		return "premium/index";
	}
}
