package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;

import config.DriverConfig;
import pages.HomePage;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HomePageTest {

    private WebDriver driver;

    private HomePage homePage;

    @BeforeAll
    public void setup() throws Exception {
        driver = DriverConfig.getDriver();

        homePage = new HomePage(driver);
    }

    @AfterAll
    public void cleanup() throws Exception {
        driver.close();
    }

    @Test
    public void whenGetHeadLine_thenOk() throws Exception {
        homePage.navigate();
        assertEquals("timely is a lite project management tool", homePage.getHeadLine().getText());
    }

}
