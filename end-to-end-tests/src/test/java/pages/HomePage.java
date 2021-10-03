package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import config.Properties;
import utils.ExplicitWait;

public class HomePage {

    private final WebDriver driver;

    public static final By headLine = By.xpath("//h1[@data-testid='home-headline']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigate() throws Exception {
        driver.get(Properties.getBaseUrl());
        ExplicitWait.waitTillElementFound(driver, headLine);
    }

    public WebElement getHeadLine() {
        return driver.findElement(headLine);
    }

}
