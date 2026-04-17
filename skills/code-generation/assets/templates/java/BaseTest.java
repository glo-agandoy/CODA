package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.testng.annotations.*;
import utils.ConfigReader;

/**
 * BaseTest — WebDriver lifecycle management for all test classes.
 *
 * Responsibilities:
 * - WebDriver setup per test method (function scope)
 * - Browser configuration from config.properties
 * - Automatic teardown on test completion or failure
 * - Screenshot capture for Allure
 *
 * Every Test class MUST extend this class.
 *
 * @author Code Generation Skill
 * @version 2.0
 */
public abstract class BaseTest {

    protected WebDriver  driver;
    protected ConfigReader config;

    // ==================== SETUP ====================

    @BeforeClass
    @Step("Load test configuration")
    public void setupClass() {
        config = ConfigReader.getInstance();
    }

    @BeforeMethod
    @Step("Launch browser and navigate to application")
    public void setupMethod() {
        driver = createDriver();
        driver.get(config.getBaseUrl());
    }

    private WebDriver createDriver() {
        String browser = config.getBrowser().toLowerCase();
        switch (browser) {
            case "firefox": {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (config.isHeadless()) {
                    options.addArguments("--headless");
                }
                return new FirefoxDriver(options);
            }
            default: {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                if (config.isHeadless()) {
                    options.addArguments("--headless=new");
                }
                return new ChromeDriver(options);
            }
        }
    }

    // ==================== TEARDOWN ====================

    @AfterMethod
    @Step("Close browser session")
    public void teardownMethod() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // ==================== SCREENSHOT ====================

    @Attachment(value = "Screenshot on Failure", type = "image/png")
    public byte[] captureScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
