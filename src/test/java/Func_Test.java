import com.google.inject.Inject;
import org.example.extensions.UIExtensions;
import org.example.pages.CatalogPage;
import org.example.pages.CourseData;
import org.example.pages.CoursePage;
import org.example.pages.HomePage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(UIExtensions.class)
public class Func_Test {

    @Inject
    private CatalogPage catalogPage;
    @Inject
    private CoursePage coursePage;
    @Inject
    private HomePage homePage;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));

    /**
     * Сценарий 1:
     * Открываем страницу каталога курсов https://otus.ru/catalog/courses
     * Найти курс по имени (имя курса должно передаваться как данные в тесте)
     * Кликнуть по плитке курса и проверить, что открыта страница верного курса
     **/
    @Test
    public void scenario1Test() {
        String courseName = "QA Automation Engineer";

        catalogPage.openWithSearch(courseName);
        catalogPage.findCourseByTitle(courseName);
        CourseData course = catalogPage.findCourseByTitle(courseName)
                .orElseThrow(() -> new AssertionError(courseName + "course was not found"));
        catalogPage.getDriver().get("https://otus.ru" + course.link());
        String html = coursePage.getPageSource();
        Document doc = Jsoup.parse(html);
        String actualTitle = doc.select("h1").first().text();
        Assertions.assertTrue(actualTitle.contains(courseName),
                "incorrect page, expected : " + courseName + ", but found:  "
                        + actualTitle);
    }

    /**
     * Сценарий 2:
     * Открываем страницу каталога курсов https://otus.ru/catalog/courses
     * Найти курсы, которые стартуют раньше и позже всех. Если даты совпадают, то выбрать все такие курсы у которых дата совпадает.
     * Проверить, что на карточке самого раннего/позднего курсов отображается верное название курса и дата его начала
     **/
    @Test
    public void scenario2Test() {
        catalogPage.open();
        List<CourseData> allCourses = catalogPage.getAllCoursesFromPageData();
        Assertions.assertFalse(allCourses.isEmpty(), "no courses are found");
        Optional<CourseData> earliestCourseOpt = allCourses.stream()
                .reduce((c1, c2) -> {
                    LocalDate d1 = LocalDate.parse(c1.startDate(), formatter);
                    LocalDate d2 = LocalDate.parse(c2.startDate(), formatter);
                    return d1.isBefore(d2) ? c1 : c2;
                });

        CourseData earliestCourse = earliestCourseOpt.orElseThrow(
                () -> new AssertionError("unable to find earlier course.")
        );
        LocalDate earliestDate = LocalDate.parse(earliestCourse.startDate(), formatter);
        Optional<CourseData> latestCourseOpt = allCourses.stream()
                .reduce((c1, c2) -> {
                    LocalDate d1 = LocalDate.parse(c1.startDate(), formatter);
                    LocalDate d2 = LocalDate.parse(c2.startDate(), formatter);
                    return d1.isAfter(d2) ? c1 : c2;
                });
        CourseData latestCourse = latestCourseOpt.orElseThrow(
                () -> new AssertionError("unable to find latest course.")
        );
        LocalDate latestDate = LocalDate.parse(latestCourse.startDate(), formatter);

        System.out.println("first course" + earliestCourse.title() + " | " + earliestCourse.startDate());
        System.out.println("last course" + latestCourse.title() + " | " + latestCourse.startDate());
        List<CourseData> boundaryCourses = allCourses.stream()
                .filter(c -> {
                    LocalDate d = LocalDate.parse(c.startDate(), formatter);
                    return d.equals(earliestDate) || d.equals(latestDate);
                })
                .collect(Collectors.toList());

        for (CourseData course : boundaryCourses) {
            openAndVerifyCourse(course);
        }
    }


    /**
     * Открыть главную страницу https://otus.ru
     * В заголовке страницы открыть меню "Обучение" и выбрать случайную категорию курсов
     * Проверить, что открыт каталог курсов верной категории * Проверить, что на карточке самого раннего/позднего курсов отображается верное название курса и дата его начала
     **/
    @Test
    public void scenario3Test() {
        homePage.open();
        homePage.openLearningDropdown();
        String chosenCategory = homePage.chooseRandomCategory();
        String currentUrl = homePage.getCurrentUrl();
        Assertions.assertTrue(
                currentUrl.contains("categories"),
                "Не открылась страница категории! Текущий URL: " + currentUrl
        );
        Assertions.assertFalse(
                catalogPage.getAllCoursesFromJsoup().isEmpty(),
                "В выбранной категории («" + chosenCategory + "») нет ни одного курса!"
        );

    }

    private void openAndVerifyCourse(CourseData course) {
        String fullUrl = "https://otus.ru" + course.link();
        catalogPage.getDriver().get(fullUrl);

        String html = coursePage.getPageSource();
        Document doc = Jsoup.parse(html);

        String actualTitle = "";
        Element h1 = doc.selectFirst("h1");
        if (h1 != null) {
            actualTitle = h1.text();
        } else {
            Element fallback = doc.selectFirst("div.sc-1ai2ech-8");
            if (fallback != null) {
                actualTitle = fallback.text();
            }
        }
        Assertions.assertTrue(
                actualTitle.toLowerCase().contains(course.title().toLowerCase()) ||
                        course.title().toLowerCase().contains(actualTitle.toLowerCase()),
                String.format(
                        "incorrect name. expected: '%s', on page: '%s'",
                        course.title(), actualTitle
                )
        );
    }

}
