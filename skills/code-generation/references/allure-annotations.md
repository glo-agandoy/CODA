# Reference: allure-annotations
# Allure Annotations Guide — Airline UI Automation

## Required Annotations Per Test Method

Every test method MUST have ALL of the following:

```java
@Test(description = "Short test description")
@Epic("{AIRLINE_NAME} [Category]")    // e.g., "{AIRLINE_NAME} Booking"
@Feature("[Feature Name]")            // e.g., "Flight Search"
@Story("[User Story / Scenario]")     // e.g., "One-way Flight Search"
@Severity(SeverityLevel.CRITICAL)     // See severity mapping below
@Description("[Full description]")    // Copy from normalized test case
@TmsLink("TC-XXX")                    // Test case ID from normalized test cases
```

No test method ships without all 7 annotations.

## Severity Mapping

| Normalized Priority | Allure SeverityLevel |
|---------------------|----------------------|
| P0 (Smoke) | `SeverityLevel.BLOCKER` |
| P1 (Core flows) | `SeverityLevel.CRITICAL` |
| P2 (Secondary) | `SeverityLevel.NORMAL` |
| P3 (Edge cases) | `SeverityLevel.MINOR` |

## Epic → Feature → Story Hierarchy

Map normalized test case classification to Allure hierarchy:

| Category | @Epic | @Feature | @Story |
|----------|-------|----------|--------|
| Smoke | "{AIRLINE_NAME} Smoke" | "Critical Path" | [Test title] |
| Booking | "{AIRLINE_NAME} Booking" | "Flight Search" / "Booking Flow" | [Test title] |
| Check-in | "{AIRLINE_NAME} Check-in" | "Online Check-in" | [Test title] |
| Payment | "{AIRLINE_NAME} Payment" | "Payment Methods" | [Test title] |
| Manage | "{AIRLINE_NAME} My Booking" | "Booking Management" | [Test title] |
| Ancillaries | "{AIRLINE_NAME} Ancillaries" | "Bags" / "Seats" / "Insurance" | [Test title] |
| Edge Case | "{AIRLINE_NAME} Edge Cases" | [Feature area] | [Test title] |

## @Step on Page Object Methods

Every action method in a Page Object MUST have `@Step`:

```java
@Step("Enter origin airport: {iataCode}")
public HomePage enterOrigin(String iataCode) { ... }

@Step("Select outbound date: T+{daysFromToday} days")
public HomePage selectOutboundDate(int daysFromToday) { ... }

@Step("Click search flights button")
public AvailabilityPage clickSearch() { ... }
```

Use `{paramName}` to inject parameter values into step descriptions.

## Screenshot Capture

Add at the end of each test method:

```java
// Java
captureScreenshot(); // defined in BaseTest
```

```python
# Python
allure.attach(
    driver.get_screenshot_as_png(),
    name="test-result",
    attachment_type=allure.attachment_type.PNG
)
```

## Full Java Example

```java
@Test(description = "Search one-way flight from origin to destination")
@Epic("{AIRLINE_NAME} Booking")
@Feature("Flight Search")
@Story("One-way Flight Search")
@Severity(SeverityLevel.CRITICAL)
@Description("Verify user can search for a one-way flight from hub to a key station "
           + "and see available flights in the results page.")
@TmsLink("TC-001")
public void testSearchOneWayFlight() {
    // Arrange
    HomePage homePage  = new HomePage(driver);
    String origin      = System.getenv("TEST_ORIGIN_IATA");   // e.g., hub IATA from airline-domain
    String destination = System.getenv("TEST_DEST_IATA");     // e.g., key station IATA
    int travelDays     = 7;

    // Act
    AvailabilityPage availabilityPage =
        homePage.searchFlight(origin, destination, travelDays);

    // Assert
    Assert.assertTrue(availabilityPage.isFlightListDisplayed(),
        "Flight results list should be visible after search");
    Assert.assertTrue(availabilityPage.hasAvailableFlights(),
        "At least one flight should be available for origin→destination T+7");

    captureScreenshot();
}
```

## Allure Links

The `@TmsLink` annotation uses the pattern from `allure.properties`:
```properties
allure.link.tms.pattern={TMS_URL}/case/{}
```
So `@TmsLink("TC-001")` generates: `{TMS_URL}/case/TC-001`

Similarly for `@Issue`:
```properties
allure.link.issue.pattern={JIRA_URL}/browse/{}
```

> Configure `{TMS_URL}` and `{JIRA_URL}` in `skills/framework-scaffolding/assets/templates/java/allure.properties`.

## Step Parameters in Reports

Use `{paramName}` in `@Step` descriptions to show actual values in Allure:

```java
// Good — shows actual value in report
@Step("Enter origin airport: {iataCode}")
public void enterOrigin(String iataCode) { ... }
// Report shows: "Enter origin airport: {IATA_CODE}"

// Bad — generic, no value shown
@Step("Enter origin airport")
public void enterOrigin(String iataCode) { ... }
// Report shows: "Enter origin airport"
```
