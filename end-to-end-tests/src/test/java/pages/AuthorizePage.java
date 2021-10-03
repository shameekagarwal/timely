package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import config.Properties;
import models.User;
import utils.ExplicitWait;

public class AuthorizePage {

    private final WebDriver driver;

    public static final By signinInputEmail = By.xpath("//input[@data-testid='signin-input-email']");
    public static final By signinInputPassword = By.xpath("//input[@data-testid='signin-input-password']");
    public static final By signinButton = By.xpath("//button[@data-testid='signin-button']");
    public static final By signinHeadLine = By.xpath("//h1[@data-testid='signin-headline']");

    public AuthorizePage(WebDriver driver) throws Exception {
        this.driver = driver;
    }

    public void navigate() throws Exception {
        driver.get(Properties.getBaseUrl());
        ExplicitWait.waitTillElementFound(driver, MiscElements.headerLinkAuthorize);
        driver.findElement(MiscElements.headerLinkAuthorize).click();
        ExplicitWait.waitTillElementFound(driver, signinHeadLine);
    }

    public WebElement getSigninInputEmail() {
        return driver.findElement(signinInputEmail);
    }

    public WebElement getSigninInputPassword() {
        return driver.findElement(signinInputPassword);
    }

    public WebElement getSigninButton() {
        return driver.findElement(signinButton);
    }

    public void signin(User user) {
        getSigninInputEmail().sendKeys(user.getEmail());
        getSigninInputPassword().sendKeys(user.getPassword());
        getSigninButton().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
    }

}
