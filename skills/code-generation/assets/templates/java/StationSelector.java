package components;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

/**
 * StationSelector — Handles the airline's airport autocomplete dropdown.
 *
 * Usage pattern:
 *   originInput.clear();
 *   originInput.sendKeys("${TEST_ORIGIN_IATA}");
 *   stationSelector.selectFirstSuggestion();
 *
 * NOTE: Selectors are generic defaults. Verify during web-exploration.
 * The data-iata attribute in selectByCode() is common but not universal —
 * the attribute name (data-iata, data-code, data-value) varies by airline implementation.
 * See skills/airline-domain/references/ui-patterns.md (Section 5: Station Autocomplete).
 *
 * @author Code Generation Skill
 * @version 2.0
 */
public class StationSelector {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    private static final By SUGGESTION_LIST  = By.cssSelector("[data-testid='station-suggestions']");
    private static final By FIRST_SUGGESTION = By.cssSelector("[data-testid='station-suggestion']:first-child");

    public StationSelector(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Wait for the autocomplete dropdown to appear and click the first suggestion.
     * Called immediately after typing an IATA code into an origin/destination field.
     */
    @Step("Select first airport suggestion from autocomplete dropdown")
    public void selectFirstSuggestion() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUGGESTION_LIST));
        WebElement firstItem = wait.until(
            ExpectedConditions.elementToBeClickable(FIRST_SUGGESTION));
        firstItem.click();
    }

    /**
     * Wait for the autocomplete dropdown to appear and select a specific IATA code.
     * Use when there may be multiple suggestions and you need a specific one.
     *
     * @param iataCode IATA code to match (e.g., "BCN")
     */
    @Step("Select airport: {iataCode}")
    public void selectByCode(String iataCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUGGESTION_LIST));
        By specificSuggestion = By.cssSelector(
            "[data-testid='station-suggestion'][data-iata='" + iataCode + "']");
        try {
            WebElement item = wait.until(
                ExpectedConditions.elementToBeClickable(specificSuggestion));
            item.click();
        } catch (TimeoutException e) {
            // Fallback: select first suggestion if specific code not found
            selectFirstSuggestion();
        }
    }
}
