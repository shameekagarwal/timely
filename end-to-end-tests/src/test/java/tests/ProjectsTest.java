package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import config.DriverConfig;
import models.User;
import pages.AuthorizePage;
import pages.MiscElements;
import pages.ProjectsEditPage;
import pages.ProjectsPage;
import utils.ExplicitWait;
import utils.UserBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectsTest {

    private WebDriver driver;

    private ProjectsEditPage projectsEditPage;
    private AuthorizePage authorizePage;
    private MiscElements miscElements;
    private ProjectsPage projectsPage;

    @BeforeAll
    public void setup() throws Exception {
        driver = DriverConfig.getDriver();

        projectsEditPage = new ProjectsEditPage(driver);
        authorizePage = new AuthorizePage(driver);
        projectsPage = new ProjectsPage(driver);
        miscElements = new MiscElements(driver);

        User user = UserBuilder.getManagerOne();
        authorizePage.navigate();
        authorizePage.signin(user);
    }

    @AfterAll
    public void cleanup() throws Exception {
        driver.close();
    }

    @Test
    public void whenCreateProject_thenOk() throws Exception {
        User user = UserBuilder.getAssociateOne();
        projectsEditPage.navigateToCreate();
        projectsEditPage.getInputTitle().sendKeys("new selenium project");
        String associateEmail = user.getEmail();
        projectsEditPage.getInputAssociates().sendKeys(associateEmail.substring(0, 1));
        Thread.sleep(1000);
        projectsEditPage.getInputAssociates().sendKeys(associateEmail.substring(1, 2));
        Thread.sleep(1000);
        projectsEditPage.getInputAssociates().sendKeys(associateEmail.substring(2, 3));
        ExplicitWait.waitTillElementFound(driver, ProjectsEditPage.associateOption(user.getEmail()));
        projectsEditPage.getAssociateOption(user.getEmail()).click();
        assertTrue(projectsEditPage.getAssociateChip(user.getEmail()).isDisplayed());
        projectsEditPage.getButtonSave().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
        assertEquals("project created successfully", miscElements.getSnackBar().getText());
    }

    @Test
    public void whenInvalidUpdateProject_thenFail() throws Exception {
        projectsPage.navigate();
        ExplicitWait.waitTillElementFound(driver, ProjectsPage.editButton(1));
        projectsPage.getEditButton(1).click();
        String updatedTitle = RandomStringUtils.randomAlphabetic(3);
        ExplicitWait.waitTillElementFound(driver, ProjectsEditPage.headLine);
        projectsEditPage.getInputTitle().clear();
        projectsEditPage.getInputTitle().sendKeys(updatedTitle);
        projectsEditPage.getButtonSave().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
        assertEquals("project title should be between 5 and 25 characters long", miscElements.getSnackBar().getText());
    }

    @Test
    public void whenValidUpdateProject_thenOk() throws Exception {
        projectsPage.navigate();
        ExplicitWait.waitTillElementFound(driver, ProjectsPage.editButton(1));
        projectsPage.getEditButton(1).click();
        String updatedTitle = RandomStringUtils.randomAlphabetic(10);
        ExplicitWait.waitTillElementFound(driver, ProjectsEditPage.headLine);
        projectsEditPage.getInputTitle().clear();
        projectsEditPage.getInputTitle().sendKeys(updatedTitle);
        projectsEditPage.getButtonSave().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
        assertEquals("project updated successfully", miscElements.getSnackBar().getText());
        projectsPage.getInputFilter().sendKeys(updatedTitle);
        assertEquals(updatedTitle, projectsPage.getFirstProjectCell(1).getText());
    }

    @Test
    public void whenDeleteProject_thenOk() throws Exception {
        projectsPage.navigate();
        ExplicitWait.waitTillElementFound(driver, ProjectsPage.editButton(1));
        projectsPage.getEditButton(1).click();
        ExplicitWait.waitTillElementFound(driver, ProjectsEditPage.headLine);
        projectsEditPage.getButtonDelete().click();
        ExplicitWait.waitTillElementFound(driver, MiscElements.snackBar);
        assertEquals("project deleted successfully", miscElements.getSnackBar().getText());
    }

}
