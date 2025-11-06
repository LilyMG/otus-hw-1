package org.example.extensions;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.modules.WebDriverModule;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;

public class UIExtensions implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

  private static Injector injector;

  @Override
  public void beforeAll(ExtensionContext context) {
    injector = Guice.createInjector(new WebDriverModule());
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    Object testInstance = context.getRequiredTestInstance();
    injector.injectMembers(testInstance);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    WebDriver driver = injector.getInstance(WebDriver.class);
    driver.quit();
  }

}
