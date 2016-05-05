package am.ik.blog.page;

import com.codeborne.selenide.ElementsCollection;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$$;

public class TopPage {
    private List<ArticleElement> articles;

    public TopPage() {
        ElementsCollection collection = $$("article.card");
        this.articles = Arrays.asList(new ArticleElement(collection.get(0)), new ArticleElement(collection.get(1)));
        //this.articles = collection.stream().map(ArticleElement::new).collect(Collectors.toList()); //<- does not work X(
    }

    public List<ArticleElement> getArticles() {
        return articles;
    }
}
