package org.example.pages;

import com.google.inject.Inject;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class MainPage {

  private final WebDriver driver;
  private final Actions actions;

  private static final By MENU_LEARNINGS = By.xpath("//span[@title='Обучение']");
  private static final By DROPDOWN_CATEGORIES = By.xpath("//a[contains(@link, '/categories/')]");

  @Inject
  public MainPage(WebDriver driver) {
    this.driver = driver;
    this.actions = new Actions(driver);
  }

  public void open() {
    driver.get("https://otus.ru/");
    acceptCookiesIfVisible();
  }

  private void acceptCookiesIfVisible() {
    By cookieAcceptBtn = By.cssSelector("button[data-testid='button-cookie']");
    List<WebElement> cookieButtons = driver.findElements(cookieAcceptBtn);
    if (!cookieButtons.isEmpty()) {
      cookieButtons.get(0).click();
    }
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }
}
