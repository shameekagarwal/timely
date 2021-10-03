package config;

public class Properties {

    public static String getBaseUrl() throws Exception {
        return System.getenv("BASE_URL");
    }

    public static void configureDriverPaths() {
        String pathToDriver = System.getProperty("user.dir") + "/chromedriver";
        System.setProperty("webdriver.chrome.driver", pathToDriver);
    }

}
