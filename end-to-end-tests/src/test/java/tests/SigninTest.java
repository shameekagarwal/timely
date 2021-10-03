package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;

import config.DriverConfig;
import models.User;
import pages.AuthorizePage;
import pages.HomePage;
import pages.MiscElements;
import utils.UserBuilder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SigninTest {

    private WebDriver driver;

    private AuthorizePage authorizePage;
    private HomePage homePage;
    private MiscElements miscElements;

    @BeforeAll
    public void setup() throws Exception {
        driver = DriverConfig.getDriver();

        authorizePage = new AuthorizePage(driver);
        homePage = new HomePage(driver);
        miscElements = new MiscElements(driver);
    }

    @AfterAll
    public void cleanup() throws Exception {
        driver.close();
    }

    @Test
    public void whenValidSigninAndSignout_thenOk() throws Exception {
        authorizePage.navigate();
        User user = UserBuilder.getAssociateOne();
        authorizePage.signin(user);
        assertEquals("signed in successfully", miscElements.getSnackBar().getText());
        // ensure navigated back to homepage
        assertEquals("timely is a lite project management tool", homePage.getHeadLine().getText());
        miscElements.getHeaderLinkSignout().click();
    }

    @Test
    public void whenInvalidSignin_thenFail() throws Exception {
        authorizePage.navigate();
        User user = UserBuilder.getAssociateOne();
        user.setPassword(user.getPassword() + "_invalid");
        authorizePage.signin(user);
        assertEquals("The password is invalid or the user does not have a password.",
                miscElements.getSnackBar().getText());
    }

}
