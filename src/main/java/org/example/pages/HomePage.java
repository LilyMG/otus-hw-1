package org.example.pages;

import com.google.inject.Inject;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class HomePage {

  private final WebDriver driver;
  private final Actions actions;
  private final WebDriverWait wait;

  private static final String BASE_URL = "https://otus.ru/";

  private static final By MENU_LEARNING = By.xpath("//span[@title='Обучение']");
  private static final By CATEGORY_LINKS = By.xpath("//a[contains(@link, '/categories/')]");
  private static final By COOKIE_BUTTON = By.cssSelector("button[data-testid='button-cookie']");

  @Inject
  public HomePage(WebDriver driver) {
    this.driver = driver;
    this.actions = new Actions(driver);
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void open() {
    driver.get(BASE_URL);
    handleCookiePopup();
  }

  private void handleCookiePopup() {
    List<WebElement> cookieButtons = driver.findElements(COOKIE_BUTTON);
    if (!cookieButtons.isEmpty()) {
      cookieButtons.get(0).click();
      System.out.println("✅ Cookies accepted.");
    }
  }

  public void openLearningDropdown() {
    final int maxTries = 3;

    for (int attempt = 1; attempt <= maxTries; attempt++) {
      try {
        WebElement learningMenu = wait.until(ExpectedConditions.elementToBeClickable(MENU_LEARNING));
        actions.moveToElement(learningMenu).perform();

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(CATEGORY_LINKS));
        System.out.println("📂 Dropdown opened successfully (Attempt " + attempt + ")");
        return;

      } catch (StaleElementReferenceException e) {
        System.out.println("⚠️ Stale element detected (Attempt " + attempt + ")");
      } catch (Exception e) {
        System.out.println("⚠️ Unexpected error: " + e.getMessage());
      }
    }

    throw new RuntimeException("❌ Failed to open dropdown after " + maxTries + " attempts.");
  }

  public String chooseRandomCategory() {
    List<WebElement> categoryElements = wait.until(
        ExpectedConditions.visibilityOfAllElementsLocatedBy(CATEGORY_LINKS)
    );

    if (categoryElements.isEmpty()) {
      throw new RuntimeException("❌ No categories found in dropdown.");
    }

    // Log all category names for debugging
    categoryElements.forEach(el -> System.out.println("• Found category: " + el.getText()));

    // Pick a random category
    WebElement chosenCategory = categoryElements.get(new Random().nextInt(categoryElements.size()));
    String categoryName = chosenCategory.getText();

    // Scroll and click via JS for reliability
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", chosenCategory);
    wait.until(ExpectedConditions.elementToBeClickable(chosenCategory));
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chosenCategory);

    System.out.println("🟢 Clicked category: " + categoryName);
    return categoryName;
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }
}
