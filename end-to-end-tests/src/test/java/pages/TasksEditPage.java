package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import config.Properties;
import utils.ExplicitWait;

public class TasksEditPage {

    private final WebDriver driver;

    public static final By headLine = By.xpath("//h1[@data-testid='tasks-edit-headline']");
    public static final By inputTitle = By.xpath("//input[@data-testid='input-title']");
    public static final By buttonSave = By.xpath("//button[@data-testid='button-save']");
    public static final By selectProject = By.xpath("//mat-select[@data-testid='select-project']");
    public static final By radioTodo = By.xpath("//mat-radio-button[@data-testid='radio-todo']");
    public static final By radioInProgress = By.xpath("//mat-radio-button[@data-testid='radio-inprogress']");
    public static final By radioDone = By.xpath("//mat-radio-button[@data-testid='radio-done']");
    public static final By buttonDelete = By.xpath("//button[@data-testid='button-delete']");

    public static By editButton(Integer index) {
        return By.xpath("(//mat-icon[contains(text(), 'edit')])[%d]".formatted(index));
    }

    public static By projectOption(Integer index) {
        return By.xpath("(//mat-option)[%d]".formatted(index));
    }

    public TasksEditPage(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCreate() throws Exception {
        driver.get(Properties.getBaseUrl());
        ExplicitWait.waitTillElementFound(driver, MiscElements.headerLinkTasks);
        driver.findElement(MiscElements.headerLinkTasks).click();
        ExplicitWait.waitTillElementFound(driver, TasksPage.buttonCreate);
        driver.findElement(ProjectsPage.buttonCreate).click();
        ExplicitWait.waitTillElementFound(driver, headLine);
    }

    public WebElement getHeadLine() {
        return driver.findElement(headLine);
    }

    public WebElement getInputTitle() {
        return driver.findElement(inputTitle);
    }

    public WebElement getButtonSave() {
        return driver.findElement(buttonSave);
    }

    public WebElement getSelectProject() {
        return driver.findElement(selectProject);
    }

    public WebElement getRadioTodo() {
        return driver.findElement(radioTodo);
    }

    public WebElement getRadioInProgress() {
        return driver.findElement(radioInProgress);
    }

    public WebElement getRadioDone() {
        return driver.findElement(radioDone);
    }

    public WebElement getButtonDelete() {
        return driver.findElement(buttonDelete);
    }

    public WebElement getEditButton(Integer index) {
        return driver.findElement(editButton(index));
    }

    public WebElement getProjectOption(Integer index) {
        return driver.findElement(projectOption(index));
    }

}
