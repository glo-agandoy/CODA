package pages;

import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;
import utils.ConfigReader;
import utils.WaitUtils;

import java.time.Duration;

/**
 * BasePage — Foundation for all airline Page Objects.
 *
 * Responsibilities:
 * - WebDriver + WebDriverWait initialization
 * - Loading spinner handling (mandatory after every page transition)
 * - Cookie consent modal handling (GDPR)
 * - Promo/newsletter popup handling
 * - Common JS utilities (scroll, JS click)
 *
 * Every Page Object MUST extend this class.
 * Every Page Object constructor MUST call handleCookieConsent() + closePromoPopup() + waitForPageLoad().
 *
 * NOTE: Selectors below use the generic data-testid values. Update with real selectors
 * from web-exploration after running against the target airline's website.
 * See skills/airline-domain/references/ui-patterns.md for airline-specific selector guidance.
 *
 * @author Code Generation Skill
 * @version 2.0
 */
public abstract class BasePage {

    protected final WebDriver      driver;
    protected final WebDriverWait  wait;
    protected final WaitUtils      waitUtils;
    protected final ConfigReader   config;

    // ==================== UI CONSTANTS ====================
    // Update these selectors with real values from web-exploration.
    // See skills/airline-domain/references/ui-patterns.md for airline-specific selectors.

    /** Loading spinner — appears during all server-side transitions.
     *  Primary: data-testid='loading-spinner' (generic, works for most airlines)
     *  Fallback: airline-specific CSS class — update after web-exploration. */
    private static final By LOADING_SPINNER =
        By.cssSelector("[data-testid='loading-spinner']");

    /** GDPR cookie consent modal container */
    private static final By COOKIE_MODAL =
        By.cssSelector("[data-testid='cookie-banner']");

    /** Cookie accept button */
    private static final By COOKIE_ACCEPT =
        By.cssSelector("[data-testid='cookie-accept']");

    /** Promo / newsletter popup close button */
    private static final By PROMO_CLOSE =
        By.cssSelector("[data-testid='promo-close'], .modal-close, .newsletter-close");

    // ==================== CONSTRUCTOR ====================

    protected BasePage(WebDriver driver) {
        this.driver    = driver;
        this.config    = ConfigReader.getInstance();
        this.wait      = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitTimeout()));
        this.waitUtils = new WaitUtils(driver);
        PageFactory.initElements(driver, this);
    }

    // ==================== PAGE LOAD ====================

    /**
     * Wait for the page to be fully ready:
     * 1. Airplane spinner disappears
     * 2. document.readyState == "complete"
     *
     * MUST be called after every server-side navigation.
     */
    @Step("Wait for page to fully load")
    protected void waitForPageLoad() {
        waitUtils.waitForSpinnerDisappear(LOADING_SPINNER);
        wait.until(d -> ((JavascriptExecutor) d)
            .executeScript("return document.readyState").equals("complete"));
    }

    // ==================== INITIAL STATE HANDLERS ====================

    /**
     * Handle cookie consent modal (GDPR).
     * Silently passes if modal is not present.
     * MUST be called in every page constructor.
     */
    @Step("Handle cookie consent if present")
    protected void handleCookieConsent() {
        try {
            WebElement acceptBtn = driver.findElement(COOKIE_ACCEPT);
            if (acceptBtn.isDisplayed()) {
                acceptBtn.click();
                waitUtils.waitForElementDisappear(COOKIE_MODAL);
            }
        } catch (NoSuchElementException | TimeoutException ignored) {
            // Cookie modal not present — continue
        }
    }

    /**
     * Close promotional or newsletter popup if present.
     * Silently passes if popup is not present.
     * MUST be called in every page constructor.
     */
    @Step("Close promotional popup if present")
    protected void closePromoPopup() {
        try {
            WebElement closeBtn = driver.findElement(PROMO_CLOSE);
            if (closeBtn.isDisplayed()) {
                closeBtn.click();
                // Wait briefly for popup animation to complete
                waitUtils.waitForElementDisappear(PROMO_CLOSE);
            }
        } catch (NoSuchElementException | TimeoutException ignored) {
            // No popup — continue
        }
    }

    // ==================== COMMON ACTIONS ====================

    /**
     * Scroll an element into the center of the viewport.
     * Use before clicking elements that may be below the fold.
     */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", element);
    }

    /**
     * Click an element via JavaScript.
     * Use as fallback when a regular click is intercepted by an overlay.
     */
    protected void clickWithJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // ==================== SCREENSHOT ====================

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] captureScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    // ==================== CURRENT URL ====================

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getPageTitle() {
        return driver.getTitle();
    }
}
