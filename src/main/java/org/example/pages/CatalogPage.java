package org.example.pages;

import com.google.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;


public class CatalogPage {

  private final WebDriver driver;
  private static final By SHOW_MORE_BUTTON = By.xpath("//button[contains(text(), 'Показать еще')]");

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

  public List<CourseData> getAllCoursesFromJsoup() {

    return null;
  }

  public void clickShowMoreUntilEnd() {
    int lastCount = 0;
    int stableTries = 0;

    while (stableTries < 3) {
      try {
        // Прокручиваем страницу до самого низа
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(500); // Даем время на подгрузку DOM

        // Ищем кнопку "Показать ещё"
        List<WebElement> buttons = driver.findElements(
                By.xpath("//button[contains(text(), 'Показать еще')]"));
        if (!buttons.isEmpty()) {
          WebElement button = buttons.get(0);
          if (button.isDisplayed() && button.isEnabled()) {
            System.out.println("🖱️ Кликаем на кнопку 'Показать ещё' через JavaScript");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            Thread.sleep(700); // Небольшая пауза для стабильной подгрузки
          }
        }

        // Проверяем текущее количество курсов
        List<WebElement> courses = driver.findElements(By.cssSelector("a[href^='/lessons/']"));
        int currentCount = courses.size();

        if (currentCount > lastCount) {
          System.out.println("📈 Загрузились новые курсы: " + currentCount);
          lastCount = currentCount;
          stableTries = 0;
        } else {
          stableTries++;
          System.out.println("⏸️ Количество курсов не изменилось (попытка " + stableTries + ")");
        }

      } catch (Exception e) {
        System.out.println("⚠️ Ошибка при прокрутке или клике: " + e.getMessage());
        break;
      }
    }

    System.out.println("📦 Финальное количество загруженных курсов: " + lastCount);
  }

  public void waitForCourseToAppear(String courseName) {
    new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            d -> driver.getPageSource().toLowerCase().contains(courseName.toLowerCase()));
  }

  public void debugPrintAllCourseTitles() {
    System.out.println("🔍 Названия курсов (debug):");
    getAllCoursesFromJsoup().forEach(course -> System.out.println("📘 " + course.title()));
  }

  public Optional<CourseData> findCourseByTitle(String targetTitle) {
    return getAllCoursesFromJsoup().stream()
            .filter(c -> c.title.toLowerCase().contains(targetTitle.toLowerCase())).findFirst();
  }

  public record CourseData(String title, String href, String startDate) {
  }
}
