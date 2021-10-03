package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import config.Properties;
import utils.ExplicitWait;

public class ProjectsEditPage {

    private final WebDriver driver;

    public static final By headLine = By.xpath("//h1[@data-testid='projects-edit-headline']");
    public static final By inputTitle = By.xpath("//input[@data-testid='input-title']");
    public static final By inputAssociates = By.xpath("//input[@data-testid='input-associates']");
    public static final By buttonSave = By.xpath("//button[@data-testid='button-save']");
    public static final By buttonDelete = By.xpath("//button[@data-testid='button-delete']");

    public static By associateOption(String email) {
        return By.xpath("//mat-option/span[contains(text(), '%s')]".formatted(email));
    }

    public static By associateChip(String email) {
        return By.xpath("//mat-chip[contains(text(), '%s')]".formatted(email));
    }

    public ProjectsEditPage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCreate() throws Exception {
        driver.get(Properties.getBaseUrl());
        ExplicitWait.waitTillElementFound(driver, MiscElements.headerLinkProjects);
        driver.findElement(MiscElements.headerLinkProjects).click();
        ExplicitWait.waitTillElementFound(driver, ProjectsPage.buttonCreate);
        driver.findElement(ProjectsPage.buttonCreate).click();
        ExplicitWait.waitTillElementFound(driver, headLine);
    }

    public WebElement getInputTitle() {
        return driver.findElement(inputTitle);
    }

    public WebElement getInputAssociates() {
        return driver.findElement(inputAssociates);
    }

    public WebElement getHeadLine() {
        return driver.findElement(headLine);
    }

    public WebElement getAssociateOption(String email) {
        return driver.findElement(associateOption(email));
    }

    public WebElement getAssociateChip(String email) {
        return driver.findElement(associateChip(email));
    }

    public WebElement getButtonSave() {
        return driver.findElement(buttonSave);
    }

    public WebElement getButtonDelete() {
        return driver.findElement(buttonDelete);
    }


}
