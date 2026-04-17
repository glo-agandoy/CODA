package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

/**
 * WaitUtils — Centralized explicit wait strategies for airline UI automation.
 *
 * Provides typed waits for common airline web UI patterns:
 * - Loading spinner disappearance (mandatory after all page transitions)
 * - Element appearance, visibility, clickability
 * - Element disappearance
 *
 * NEVER use Thread.sleep() — use these methods instead.
 *
 * @author Code Generation Skill
 * @version 2.0
 */
public class WaitUtils {

    private final WebDriver      driver;
    private final WebDriverWait  wait;
    private final int            timeoutSeconds;

    public WaitUtils(WebDriver driver) {
        this.driver         = driver;
        this.timeoutSeconds = ConfigReader.getInstance().getExplicitTimeout();
        this.wait           = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // ==================== SPINNER ====================

    /**
     * Wait for the airline's loading spinner to disappear.
     * Use after every server-side transition (search, navigate, submit).
     * Silently passes if spinner was never present.
     *
     * @param spinnerLocator Locator for the spinner element (from BasePage.LOADING_SPINNER)
     */
    public void waitForSpinnerDisappear(By spinnerLocator) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInstance().getSpinnerTimeout()))
                .until(ExpectedConditions.invisibilityOfElementLocated(spinnerLocator));
        } catch (TimeoutException ignored) {
            // Spinner already gone or never appeared
        }
    }

    // ==================== VISIBILITY ====================

    /**
     * Wait for an element to be visible.
     * @param locator CSS selector or By locator
     * @return The visible WebElement
     */
    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for a located element to be visible.
     */
    public WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    // ==================== CLICKABILITY ====================

    /**
     * Wait for an element to be clickable (visible + enabled).
     */
    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // ==================== PRESENCE ====================

    /**
     * Wait for an element to be present in the DOM (not necessarily visible).
     */
    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ==================== DISAPPEARANCE ====================

    /**
     * Wait for an element to disappear from the DOM or become invisible.
     * Silently passes if element was never present.
     */
    public void waitForElementDisappear(By locator) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException ignored) {
            // Element already gone
        }
    }

    // ==================== URL ====================

    /**
     * Wait for the current URL to contain a specific fragment.
     */
    public boolean waitForUrlContains(String urlFragment) {
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    // ==================== IFRAME ====================

    /**
     * Wait for an iframe to be available and switch context into it.
     * Use for the payment page iframe.
     */
    public void switchToFrame(By frameLocator) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
    }

    public void switchToFrame(WebElement frameElement) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }
}
