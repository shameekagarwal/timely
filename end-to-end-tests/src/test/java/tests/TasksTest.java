package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import config.DriverConfig;
import models.User;
import pages.AuthorizePage;
import pages.MiscElements;
import pages.TasksEditPage;
import pages.TasksPage;
import utils.ExplicitWait;
import utils.UserBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TasksTest {

    private WebDriver driver;

    private AuthorizePage authorizePage;
    private TasksPage tasksPage;
    private TasksEditPage tasksEditPage;
    private MiscElements miscElements;

    @BeforeAll
    public void setup() throws Exception {
        driver = DriverConfig.getDriver();

        tasksPage = new TasksPage(driver);
        tasksEditPage = new TasksEditPage(driver);
        authorizePage = new AuthorizePage(driver);
        miscElements = new MiscElements(driver);

        User user = UserBuilder.getAssociateOne();
        authorizePage.navigate();
        authorizePage.signin(user);
    }

    @AfterAll
    public void cleanup() throws Exception {
        driver.close();
    }

    @Test
    public void whenCreateTask_thenOk() throws Exception {
        tasksEditPage.navigateToCreate();
        tasksEditPage.getInputTitle().sendKeys("new selenium task");
        tasksEditPage.getRadioInProgress().click();
        tasksEditPage.getSelectProject().sendKeys(Keys.ENTER);
        ExplicitWait.waitTillElementFound(driver, TasksEditPage.projectOption(1));
        tasksEditPage.getProjectOption(1).click();
        tasksEditPage.getButtonSave().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
        assertEquals("task created successfully", miscElements.getSnackBar().getText());
    }

    @Test
    public void whenInvalidUpdateTask_thenFail() throws Exception {
        tasksPage.navigate();
        ExplicitWait.waitTillElementFound(driver, TasksPage.editButton(1));
        tasksPage.getEditButton(1).click();
        ExplicitWait.waitTillElementFound(driver, TasksEditPage.headLine);
        String updatedTitle = RandomStringUtils.randomAlphabetic(3);
        tasksEditPage.getInputTitle().clear();
        tasksEditPage.getInputTitle().sendKeys(updatedTitle);
        tasksEditPage.getButtonSave().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
        assertEquals("task title should be between 5 and 25 characters long", miscElements.getSnackBar().getText());
    }

    @Test
    public void whenValidUpdateTask_thenOk() throws Exception {
        tasksPage.navigate();
        ExplicitWait.waitTillElementFound(driver, TasksPage.editButton(1));
        tasksPage.getEditButton(1).click();
        ExplicitWait.waitTillElementFound(driver, TasksEditPage.headLine);
        String updatedTitle = RandomStringUtils.randomAlphabetic(10);
        tasksEditPage.getInputTitle().clear();
        tasksEditPage.getInputTitle().sendKeys(updatedTitle);
        tasksEditPage.getRadioDone().click();
        tasksEditPage.getButtonSave().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
        assertEquals("task updated successfully", miscElements.getSnackBar().getText());
        tasksPage.getInputFilter().sendKeys(updatedTitle);
        assertEquals(updatedTitle, tasksPage.getFirstProjectCell(1).getText());
        assertEquals("Done", tasksPage.getFirstProjectCell(2).getText());
    }

    @Test
    public void whenDeleteTask_thenOk() throws Exception {
        tasksPage.navigate();
        ExplicitWait.waitTillElementFound(driver, TasksPage.editButton(1));
        tasksPage.getEditButton(1).click();
        ExplicitWait.waitTillElementFound(driver, TasksEditPage.headLine);
        tasksEditPage.getButtonDelete().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
        assertEquals("task deleted successfully", miscElements.getSnackBar().getText());
    }

}
