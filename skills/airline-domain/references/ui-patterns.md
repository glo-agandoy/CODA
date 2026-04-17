# Reference: ui-patterns
# {AIRLINE_NAME} — UI Patterns & Automation Handling Guide

> **CONFIGURATION REQUIRED:** Fill in the airline-specific selectors in each pattern below.
> Generic patterns (marked G) apply to most airlines with minor selector adjustments.
> Airline-specific patterns (marked A) must be discovered during web-exploration.
> See `references/vueling-example.md` for a complete filled-in example.

---

## Critical UI Patterns

### 1. [G] Loading Spinner

**What it is:** An animated indicator shown during all server-side page transitions.
Most airlines have one — the selector and visual style vary.

**When it appears:**
- After clicking "Search flights"
- After selecting a flight or fare
- After clicking "Continue" on any step
- After payment submission

**Selector — CONFIGURE THIS:**
```css
[data-testid='loading-spinner']
/* fallback — airline-specific CSS class, discover during web-exploration */
{.airline-specific-loading-class}
```

**Automation rule:**
```java
// ALWAYS wait for spinner disappearance before any next action
waitUtils.waitForSpinnerDisappear(By.cssSelector("[data-testid='loading-spinner'], {.airline-loading-fallback}"));
```

**Never proceed** to the next step without confirming the spinner has disappeared.
Set max wait based on observed SLA (default: 30s is a safe starting point).

---

### 2. [G] Cookie Consent Modal

**What it is:** GDPR cookie consent dialog. Appears on first load and after cookie clear.
Standard pattern for all airlines operating in the EU.

**Selectors — CONFIGURE THIS:**
```css
{[data-testid='cookie-banner']}        /* modal container */
{[data-testid='cookie-accept']}        /* accept all button */
{[data-testid='cookie-reject']}        /* reject non-essential */
{[data-testid='cookie-settings']}      /* manage preferences */
```

**Automation rule:**
- Handle at the **beginning of every test** that starts from the homepage
- Click accept, then wait for modal to disappear
- If modal is not present, continue silently (do not fail)

```java
protected void handleCookieConsent() {
    try {
        WebElement btn = driver.findElement(By.cssSelector("{cookie-accept-selector}"));
        if (btn.isDisplayed()) {
            btn.click();
            waitUtils.waitForElementDisappear(By.cssSelector("{cookie-banner-selector}"));
        }
    } catch (NoSuchElementException | TimeoutException ignored) { }
}
```

---

### 3. [G] Promotional / Newsletter Popup

**What it is:** Marketing overlay that may appear after login or during the booking flow.
Present in most major airline websites.

**Selectors — CONFIGURE THIS:**
```css
{[data-testid='promo-close']}
{.modal-close}
{.newsletter-close}
```

**Automation rule:**
- Close silently if present — do not fail if absent
- May reappear mid-flow — close again if needed
- Use `closePromoPopup()` in `BasePage` before any critical action

---

### 4. [G] Payment iFrame

**What it is:** Credit card form rendered inside a sandboxed iframe for PCI compliance.
Standard pattern for airline payment pages.

**Iframe selector — CONFIGURE THIS:**
```css
{#payment-iframe}
{[data-testid='payment-frame']}
{iframe[src*='payment']}
```

> Discover the exact iframe selector during web-exploration.
> The iframe source domain may be a third-party payment provider — note this for security context switching.

**Automation rule:**
```java
// REQUIRED before interacting with any payment field
WebElement iframe = wait.until(
    ExpectedConditions.presenceOfElementLocated(By.cssSelector("{payment-iframe-selector}")));
driver.switchTo().frame(iframe);

// ... interact with card fields ...

driver.switchTo().defaultContent(); // return to main document
```

**Never** attempt to interact with card fields without switching iframe context first.

---

### 5. [A] Station / Airport Autocomplete Dropdown

**What it is:** Airport search dropdown that appears when typing in origin/destination fields.
The selector attribute for the selected IATA code may vary by airline implementation.

**Selectors — CONFIGURE THIS:**
```css
{[data-testid='station-suggestions']}          /* dropdown container */
{[data-testid='station-suggestion']}           /* individual suggestion item */
{[data-testid='station-suggestion']:first-child}  /* first match */
```

> Some airlines use a `data-iata` attribute on suggestion items — verify during web-exploration.
> The attribute name for the IATA code (`data-iata`, `data-code`, `data-value`) is airline-specific.

**Automation rule:**
1. Click origin/destination input
2. Type IATA code (e.g., `${TEST_ORIGIN_IATA}`)
3. Wait for dropdown to appear
4. Click first suggestion or the suggestion matching the IATA code
5. Verify field is populated

```java
@Step("Enter airport: {iataCode}")
public HomePage enterOrigin(String iataCode) {
    originInput.clear();
    originInput.sendKeys(iataCode);
    stationSelector.selectFirstSuggestion(); // waits for dropdown + clicks first
    return this;
}
```

---

### 6. [A] Calendar Date Picker

**What it is:** Custom month-navigation calendar widget for date selection.
The exact selectors must be discovered during web-exploration.

**Selectors — CONFIGURE THIS:**
```css
{[data-testid='calendar']}                         /* container */
{[data-testid='calendar-next']}                    /* next month button */
{[data-testid='calendar-prev']}                    /* previous month button */
{[data-testid='calendar-day-{YYYY-MM-DD}']}        /* specific day cell */
```

> If the calendar uses a different date attribute format, update `CalendarComponent.java` accordingly.

**Automation rule:**
- Always use `LocalDate.now().plusDays(N)` — never hardcode a date
- Navigate months using next/prev buttons until target month is visible
- Use `CalendarComponent.selectDateFromToday(N)` for all date selections

---

### 7. [G] i18n / Localization

**What it is:** Most airline websites are multilingual.

**Automation rule:**
- **Never use text-based selectors** for content that may change by locale
- Always prefer `data-testid`, `data-qa`, `id`, `name` attributes
- If text must be validated, set browser locale explicitly in test config or use a locale URL parameter
- Exception: test cases that explicitly test localization behavior

---

## Selector Priority (Universal Reference)

| Priority | Attribute | Example | Notes |
|----------|-----------|---------|-------|
| 1 | `data-testid` | `[data-testid='search-btn']` | Always preferred |
| 2 | `data-qa` / `data-cy` | `[data-qa='origin']` | QA-specific attrs |
| 3 | Static `id` | `#booking-form` | Only if stable |
| 4 | `name` | `[name='email']` | Form fields |
| 5 | CSS combination | `.flight-card .price-amount` | When above absent |
| 6 | XPath | `//button[contains(@class,'search')]` | Last resort only |

**Never use:**
- Dynamic IDs containing numbers/hashes (change per session)
- Auto-generated class names (contain random characters)
- Visible text content (breaks on locale change)
