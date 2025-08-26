package org.example.pages;

import com.google.inject.Inject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;


public class CatalogPage {

    private final WebDriver driver;
    @Inject
    public CatalogPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return this.driver;
    }

    public void open() {
        driver.get("https://otus.ru/catalog/courses");
    }

    public void openWithSearch(String query) {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        driver.get("https://otus.ru/catalog/courses?search=" + encoded);
    }

    public List<CourseData> getAllCoursesFromPageData() {
        String pageSource = driver.getPageSource();
        Document document = Jsoup.parse(pageSource);

        Elements lessonLinks = document.select("a[href^='/lessons/']");
        List<CourseData> courseList = new ArrayList<>();

        for (Element element : lessonLinks) {
            String url = element.attr("href").trim();
            String text = element.text().trim();

            if (url.isEmpty() || text.isEmpty()) {
                continue;
            }

            // Attempt to extract the course title (excluding the start date)
            String courseTitle = text.replaceAll("\\d{1,2}\\s+[а-яА-ЯёЁ]+(?:,\\s*\\d{4})?.*", "").trim();

            courseList.add(new CourseData(courseTitle, url));
        }

        return courseList;
    }

    public Optional<CourseData> findCourseByTitle(String targetTitle) {
        return getAllCoursesFromPageData().stream()
                .filter(c -> c.title.toLowerCase().contains(targetTitle.toLowerCase())).findFirst();
    }

    public record CourseData(String title, String href) {
    }
}
