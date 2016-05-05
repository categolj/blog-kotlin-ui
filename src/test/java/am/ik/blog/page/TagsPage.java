package am.ik.blog.page;

import com.codeborne.selenide.ElementsCollection;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class TagsPage {
    @FindBy(how = How.CSS, using = ".tag")
    private ElementsCollection tags;

    public ElementsCollection getTags() {
        return tags;
    }
}
