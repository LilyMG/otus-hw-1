package org.example.pages;

import com.google.inject.Inject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;


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

        Elements lessonLinks = document.select("a[link^='/lessons/']");
        List<CourseData> courseList = new ArrayList<>();

        for (Element element : lessonLinks) {
            String url = element.attr("link").trim();
            String text = element.text().trim();

            if (url.isEmpty() || text.isEmpty()) {
                continue;
            }

            // Attempt to extract the course title (excluding the start date)
            String courseTitle = text.replaceAll("\\d{1,2}\\s+[а-яА-ЯёЁ]+(?:,\\s*\\d{4})?.*", "").trim();

            courseList.add(new CourseData(courseTitle, url, ""));
        }

        return courseList;
    }

    public Optional<CourseData> findCourseByTitle(String targetTitle) {
        return getAllCoursesFromPageData().stream()
                .filter(c -> c.title().toLowerCase().contains(targetTitle.toLowerCase())).findFirst();
    }

    public List<CourseData> getAllCoursesFromJsoup() {
        String html = driver.getPageSource();
        Document doc = Jsoup.parse(html);

        Elements courseLinks = doc.select("a[href^='/lessons/']");

        List<CourseData> courses = new ArrayList<>();

        for (Element link : courseLinks) {
            String href = link.attr("href").trim();
            String fullText = link.text().trim();

            if (fullText.isEmpty() || href.isEmpty()) {
                continue;
            }

            // Извлекаем название курса до даты начала
            String title = fullText.replaceAll("\\d{1,2}\\s+[а-яА-ЯёЁ]+(?:,\\s*\\d{4})?.*", "").trim();

            // Пытаемся извлечь дату начала курса
            LocalDate parsed = parseDateFromOtusText(fullText);

            if (parsed != null) {
                String startDateString = parsed.format(
                        DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru")));
                courses.add(new CourseData(title, href, startDateString));
            } else {
                System.out.println("Date is not present: " + fullText);
            }
        }
        return courses;
    }

    public static LocalDate parseDateFromOtusText(String text) {
        // Уникальный шаблон для дня, месяца и года
        Pattern pattern = Pattern.compile("(\\d{1,2})\\s([а-яА-ЯёЁ]+)(?:,\\s?(\\d{4}))?");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String day = matcher.group(1);
            String month = matcher.group(2);
            String year =
                    matcher.group(3) != null ? matcher.group(3) : String.valueOf(LocalDate.now().getYear());

            String fullDate = day + " " + month + " " + year;

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru"));
                return LocalDate.parse(fullDate, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("unknown date՝ " + fullDate);
                return null;
            }
        }

        // Если не удалось найти ни день, ни месяц

        return null;
    }



}
