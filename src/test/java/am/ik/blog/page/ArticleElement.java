package am.ik.blog.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selectors.*;

public class ArticleElement {
    private final SelenideElement element;

    public ArticleElement(SelenideElement element) {
        this.element = element;
    }

    public SelenideElement getTitle() {
        return this.element.$(".card-header-title");
    }

    public ElementsCollection getTags() {
        return this.element.$$(".tag");
    }

    public SelenideElement getThis() {
        return element;
    }

    public SelenideElement read() {
        SelenideElement button = this.element.$(byText("Read this article"));
        button.click();
        button.waitUntil(Condition.disappears, TimeUnit.SECONDS.toMillis(5));
        return this.element.$(".control + p");
    }
}
