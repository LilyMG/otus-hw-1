import com.google.inject.Inject;
import org.example.extensions.UIExtensions;
import org.example.pages.CatalogPage;
import org.example.pages.CoursePage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UIExtensions.class)
public class Func_Test {

    @Inject
    private CatalogPage catalogPage;

    @Inject
    private CoursePage coursePage;

    @Test
    public void mainPageTest() {
        String courseName = "QA Automation Engineer";

        catalogPage.openWithSearch(courseName);
        catalogPage.findCourseByTitle(courseName);
        CatalogPage.CourseData course = catalogPage.findCourseByTitle(courseName)
                .orElseThrow(() -> new AssertionError(courseName + "course was not found"));
        catalogPage.getDriver().get("https://otus.ru" + course.href());
        String html = coursePage.getPageSource();
        Document doc = Jsoup.parse(html);
        String actualTitle = doc.select("h1").first().text();
        Assertions.assertTrue(actualTitle.contains(courseName),
                "incorrect page, expected : " + courseName + ", but found:  "
                        + actualTitle);
    }


}
