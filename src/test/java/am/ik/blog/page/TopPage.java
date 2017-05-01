package am.ik.blog.page;

import java.util.List;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TopPage {
	private List<ArticleElement> articles;

	public TopPage(WebClient webClient, int port) throws Exception {
		HtmlPage top = webClient.getPage("http://localhost:" + port);
		this.articles = top.querySelectorAll("article.card").stream()
				.map(ArticleElement::new).collect(Collectors.toList());
	}

	public List<ArticleElement> getArticles() {
		return articles;
	}
}
