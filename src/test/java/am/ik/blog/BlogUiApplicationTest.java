package am.ik.blog;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.util.StreamUtils.copyToString;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;

import am.ik.blog.page.ArticleElement;
import am.ik.blog.page.EntryPage;
import am.ik.blog.page.TopPage;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Ignore
public class BlogUiApplicationTest {
	@LocalServerPort
	int port;
	@Autowired
	RestTemplate restTemplate;
	MockRestServiceServer mockServer;
	WebClient webClient;
	private String entries;
	private String entry1;
	private String entry2;

	@Before
	public void setUp() throws Exception {
		webClient = new WebClient();
		webClient.getOptions().setCssEnabled(false);
		setupMock();
	}

	void setupMock() throws Exception {
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
		entries = copyToString(
				new ClassPathResource("mock/get-entries.json").getInputStream(), UTF_8);
		entry1 = copyToString(
				new ClassPathResource("mock/get-entry1.json").getInputStream(), UTF_8);
		entry2 = copyToString(
				new ClassPathResource("mock/get-entry2.json").getInputStream(), UTF_8);
	}

	@After
	public void close() throws Exception {
		webClient.close();
	}

	@Test
	public void checkTopPage() throws Exception {
		mockServer
				.expect(requestTo(
						"http://blog-api/api/entries?page=0&size=10&excludeContent=true"))
				.andRespond(withSuccess(entries, MediaType.APPLICATION_JSON_UTF8));
		List<ArticleElement> articles = new TopPage(webClient, port).getArticles();
		assertThat(articles).hasSize(2);
		assertThat(articles.get(0).getTitle().asText()).isEqualTo("Hello Spring Boot");
		assertThat(articles.get(0).getTags().stream().map(DomNode::asText)
				.collect(Collectors.toList())).containsExactly("Java", "Spring",
						"Spring Boot");
		assertThat(articles.get(1).getTitle().asText()).isEqualTo("Hello Java8");
		assertThat(articles.get(1).getTags().stream().map(DomNode::asText)
				.collect(Collectors.toList())).containsExactly("Java", "Java8", "Stream");
	}

	@Test
	public void checkEntryPage1() throws Exception {
		mockServer.expect(requestTo("http://blog-api/api/entries/1"))
				.andRespond(withSuccess(entry1, MediaType.APPLICATION_JSON_UTF8));

		ArticleElement article = new EntryPage(1, webClient, port).getArticle();
		assertThat(article.getTitle().asText()).isEqualTo("Hello Java8");
		assertThat(article.getTags().stream().map(DomNode::asText)
				.collect(Collectors.toList())).containsExactly("Java", "Java8", "Stream");
	}

	@Test
	public void checkEntryPage2() throws Exception {
		mockServer.expect(requestTo("http://blog-api/api/entries/2"))
				.andRespond(withSuccess(entry2, MediaType.APPLICATION_JSON_UTF8));

		ArticleElement article = new EntryPage(2, webClient, port).getArticle();
		assertThat(article.getTitle().asText()).isEqualTo("Hello Spring Boot");
		assertThat(article.getTags().stream().map(DomNode::asText)
				.collect(Collectors.toList())).containsExactly("Java", "Spring",
						"Spring Boot");
	}
}