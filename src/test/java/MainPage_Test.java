import com.google.inject.Inject;
import org.example.extensions.UIExtensions;
import org.example.pages.CatalogPage;
import org.example.pages.CoursePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UIExtensions.class)
public class MainPage_Test {

    @Inject
    private CatalogPage catalogPage;

    @Inject
    private CoursePage coursePage;

    @Test
    public void mainPageTest(){
        String targetCourseName = "QA Automation Engineer";

        // Открываем страницу с результатами поиска
        catalogPage.openWithSearch(targetCourseName);
        catalogPage.getDriver().get("https://otus.ru");


    }


}
