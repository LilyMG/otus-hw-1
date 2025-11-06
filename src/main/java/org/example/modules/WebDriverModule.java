package org.example.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;

import java.time.Duration;

public class WebDriverModule extends AbstractModule {
  @Override
  protected void configure() {
    // empty configure
  }


  @Provides
  @Singleton
  public WebDriver provideWebDriver() {
    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();
    ChromeDriver baseDriver = new ChromeDriver(options);

    // add highlight
    WebDriver driver = new EventFiringDecorator()
        .decorate(baseDriver);
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    return driver;
  }


}
