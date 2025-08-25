package org.example.commons;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public abstract class AbsCommonObject<T> {

    protected WebDriver driver;

    public AbsCommonObject(WebDriver webDriver) {
        this.driver = webDriver;
        PageFactory.initElements(driver, this);
    }
}
