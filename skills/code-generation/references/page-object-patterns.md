# Reference: page-object-patterns
# Page Object Model Patterns — Airline UI Automation

## Core Principles

1. **One class per page** — each page in the airline booking flow gets its own Page Object class
2. **Fluent API** — methods that stay on the same page return `this`; navigation methods return the target page class
3. **No assertions in Page Objects** — assertions belong in test classes only
4. **Constructor handles initial state** — every constructor calls `handleCookieConsent()`, `closePromoPopup()`, and `waitForPageLoad()` via `BasePage`
5. **Components are shared** — `CalendarComponent` and `StationSelector` are injected, not duplicated

## Class Hierarchy

```
BasePage (abstract)
├── HomePage extends BasePage
├── AvailabilityPage extends BasePage
├── PassengerDetailsPage extends BasePage
├── AncillariesPage extends BasePage
├── ExtrasPage extends BasePage
├── PaymentPage extends BasePage        ← also handles iFrame context
├── ConfirmationPage extends BasePage
├── CheckInPage extends BasePage
├── BoardingPassPage extends BasePage
└── MyBookingPage extends BasePage

BaseTest (abstract)
├── BookingTest extends BaseTest
├── CheckInTest extends BaseTest
├── ManageBookingTest extends BaseTest
├── AncillaryTest extends BaseTest
└── PaymentTest extends BaseTest

Components (not extending BasePage):
├── CalendarComponent
└── StationSelector
```

## Method Return Type Convention

```java
// Same page — fluent chaining
@Step("Enter origin: {iataCode}")
public HomePage enterOrigin(String iataCode) {
    originInput.clear();
    originInput.sendKeys(iataCode);
    stationSelector.selectFirstSuggestion();
    return this;
}

// Navigation — return new page
@Step("Click search button")
public AvailabilityPage clickSearch() {
    scrollToElement(searchButton);
    searchButton.click();
    waitForPageLoad();
    return new AvailabilityPage(driver);
}

// State check — return boolean/string (for assertions in test class)
public boolean isFlightListDisplayed() {
    return flightResultsList.isDisplayed();
}

public String getConfirmationPNR() {
    return pnrElement.getText();
}
```

## Locator Placement

All locators are declared as class fields using `@FindBy`:

```java
// Group locators at the top of the class
// ==================== LOCATORS ====================

@FindBy(css = "[data-testid='search-origin']")
private WebElement originInput;

@FindBy(css = "[data-testid='search-destination']")
private WebElement destinationInput;

@FindBy(css = "[data-testid='search-submit']")
private WebElement searchButton;

// Components initialized in constructor
private final CalendarComponent calendar;
private final StationSelector stationSelector;
```

## Component Injection Pattern

```java
public HomePage(WebDriver driver) {
    super(driver);
    this.calendar        = new CalendarComponent(driver);
    this.stationSelector = new StationSelector(driver);
    // Initial state handled by BasePage
    handleCookieConsent();
    closePromoPopup();
    waitForPageLoad();
}
```

## iFrame Handling (PaymentPage)

```java
@FindBy(css = "#payment-iframe")
private WebElement paymentIframe;

@Step("Switch to payment iFrame context")
private void enterPaymentIframe() {
    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(paymentIframe));
}

@Step("Return to main document context")
private void exitPaymentIframe() {
    driver.switchTo().defaultContent();
}

@Step("Enter card number: {maskedNumber}")
public PaymentPage enterCardNumber(String cardNumber, String maskedNumber) {
    enterPaymentIframe();
    cardNumberInput.clear();
    cardNumberInput.sendKeys(cardNumber);
    exitPaymentIframe();
    return this;
}
```

## Selector Validation Comment

Every `@FindBy` must document its validation status:

```java
/**
 * AvailabilityPage — Flight results grid.
 *
 * Validated Selectors (via web-exploration, {DATE}):
 * - flightResultsList:  [data-testid='flight-results']     ✓ validated
 * - firstFlightCard:   [data-testid='flight-card']:first-child  ✓ validated
 * - fareFamilyBasic:   [data-testid='fare-basic']           ✓ validated
 * - loadingSpinner:    [data-testid='loading-spinner']      ✓ validated (inherited)
 * - noPriceAvailable: [data-testid='no-availability']       ✓ validated (fallback L5)
 */
```

## Test Class Structure

```java
@Epic("{AIRLINE_NAME} Booking")
@Feature("Flight Search")
public class BookingTest extends BaseTest {

    // ==================== SMOKE ====================
    @Test
    @Story("...")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("TC-001")
    public void testHomePageLoads() { ... }

    // ==================== HAPPY PATH ====================
    @Test
    @Story("One-way Flight Search")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("TC-002")
    public void testSearchOneWayFlight() { ... }

    // ==================== SAD PATH ====================
    @Test
    @Story("Search with invalid origin")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("TC-003")
    public void testSearchInvalidOrigin() { ... }

    // ==================== EDGE CASE ====================
    @Test
    @Story("Search with INF exceeding ADT count")
    @Severity(SeverityLevel.MINOR)
    @TmsLink("TC-004")
    public void testSearchINFExceedsADT() { ... }
}
```

## Python Equivalent Patterns

```python
class HomePage(BasePage):
    """Airline main landing page with flight search."""

    ORIGIN_INPUT    = (By.CSS_SELECTOR, "[data-testid='search-origin']")
    DESTINATION     = (By.CSS_SELECTOR, "[data-testid='search-destination']")
    SEARCH_BUTTON   = (By.CSS_SELECTOR, "[data-testid='search-submit']")

    def __init__(self, driver):
        super().__init__(driver)
        self.calendar         = CalendarComponent(driver)
        self.station_selector = StationSelector(driver)
        self.handle_cookie_consent()
        self.close_promo_popup()
        self.wait_for_page_load()

    @allure.step("Enter origin airport: {iata_code}")
    def enter_origin(self, iata_code: str) -> "HomePage":
        self.find_element(self.ORIGIN_INPUT).clear()
        self.find_element(self.ORIGIN_INPUT).send_keys(iata_code)
        self.station_selector.select_first_suggestion()
        return self

    @allure.step("Click search button")
    def click_search(self) -> "AvailabilityPage":
        self.find_clickable(self.SEARCH_BUTTON).click()
        self.wait_for_page_load()
        return AvailabilityPage(self.driver)
```
