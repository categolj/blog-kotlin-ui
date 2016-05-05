package am.ik.blog.page;

import static com.codeborne.selenide.Selenide.$;

public class EntryPage {
    private final ArticleElement article;

    public EntryPage() {
        this.article = new ArticleElement($("article.card"));
    }

    public ArticleElement getArticle() {
        return article;
    }
}
