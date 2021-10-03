package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MiscElements {

    private final WebDriver driver;

    public static final By snackBar = By.xpath("//simple-snack-bar");
    public static final By headerLinkAuthorize = By.xpath("//a[@data-testid='header-link-authorize']");
    public static final By headerLinkSignout = By.xpath("//a[@data-testid='header-link-signout']");
    public static final By headerLinkProjects = By.xpath("//a[@data-testid='header-link-projects']");
    public static final By headerLinkTasks = By.xpath("//a[@data-testid='header-link-tasks']");

    public MiscElements(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement getSnackBar() {
        return driver.findElement(snackBar);
    }

    public WebElement getHeaderLinkAuthorize() {
        return driver.findElement(headerLinkAuthorize);
    }

    public WebElement getHeaderLinkSignout() {
        return driver.findElement(headerLinkSignout);
    }

    public WebElement getHeaderLinkTasks() {
        return driver.findElement(headerLinkTasks);
    }

}
