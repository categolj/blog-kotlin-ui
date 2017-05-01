package am.ik.blog.page;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class EntryPage {
	private ArticleElement article;

	public EntryPage(int entryId, WebClient webClient, int port) throws Exception {
		HtmlPage entry = webClient
				.getPage("http://localhost:" + port + "/entries/" + entryId);
		this.article = new ArticleElement(entry.querySelector("article.card"));
	}

	public ArticleElement getArticle() {
		return article;
	}
}
