package am.ik.blog.page;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ArticleElement {
	private final DomNode element;

	public ArticleElement(DomNode element) {
		this.element = element;
	}

	public DomNode getTitle() {
		return this.element.querySelector(".card-header-title");
	}

	public List<DomNode> getTags() {
		return this.element.querySelectorAll(".tag");
	}

	public DomNode getThis() {
		return element;
	}

	public DomNode read() throws Exception {
		HtmlButton button = this.element.getHtmlPageOrNull().getElementByName("read");
		HtmlPage click = button.click();
		TimeUnit.SECONDS.sleep(1);
		// TODO Exception invoking setInnerHTML
		return click.querySelector(".has-addons + p");
	}
}
