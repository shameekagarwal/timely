package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import config.Properties;
import utils.ExplicitWait;

public class ProjectsPage {

    private final WebDriver driver;

    public static final By buttonCreate = By.xpath("//a[@data-testid='button-create']");
    public static final By inputFilter = By.xpath("//input[@data-testid='input-filter']");

    public static By editButton(Integer index) {
        return By.xpath("(//mat-icon[contains(text(), 'edit')])[%d]".formatted(index));
    }

    public ProjectsPage(WebDriver driver) {
        this.driver = driver;
    }

    public static By firstProjectCell(Integer index) {
        return By.xpath("//tr/td[%d]".formatted(index));
    }

    public void navigate() throws Exception {
        driver.get(Properties.getBaseUrl());
        ExplicitWait.waitTillElementFound(driver, MiscElements.headerLinkProjects);
        driver.findElement(MiscElements.headerLinkProjects).click();
        ExplicitWait.waitTillElementFound(driver, buttonCreate);
    }

    public WebElement getButtonCreate() {
        return driver.findElement(buttonCreate);
    }

    public WebElement getFirstProjectCell(Integer index) {
        return driver.findElement(firstProjectCell(index));
    }

    public WebElement getInputFilter() {
        return driver.findElement(inputFilter);
    }

    public WebElement getEditButton(Integer index) {
        return driver.findElement(editButton(index));
    }

}
