package am.ik.blog

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(classes = arrayOf(BlogKotlinUiApplication::class))
@WebAppConfiguration
@ActiveProfiles("no-graphite")
class BlogKotlinUiApplicationTests {

	@Test
	fun contextLoads() {
	}

}
