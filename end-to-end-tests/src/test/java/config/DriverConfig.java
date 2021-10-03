package config;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverConfig {

    public static WebDriver getDriver() {
        Properties.configureDriverPaths();
        WebDriver driver = new ChromeDriver(getChromeOptions());
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        try {
            String envHeadless = System.getenv("HEADLESS");
            Boolean headless = envHeadless != null ? Boolean.parseBoolean(envHeadless) : false;
            options.setHeadless(headless);
        } catch (Exception ignored) {
            options.setHeadless(true);
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }

}
