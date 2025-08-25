package org.example.pages;

import com.google.inject.Inject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CoursePage {

  private final WebDriver driver;

  private static final By COURSE_HEADER = By.cssSelector("h1.course-header2__title");

  @Inject
  public CoursePage(WebDriver driver) {
    this.driver = driver;
  }

  public String getCourseTitle() {
    return driver.findElement(COURSE_HEADER).getText().trim();
  }

  public String getPageSource() {
    return driver.getPageSource();
  }

  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }
}

