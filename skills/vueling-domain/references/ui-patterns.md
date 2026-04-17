# Reference: ui-patterns
# Vueling UI Patterns — Automation Handling Guide

## Critical UI Patterns

### 1. Airplane Loading Spinner

**What it is:** A CSS animation of an airplane silhouette shown during all server-side transitions.

**When it appears:**
- After clicking "Search flights"
- After selecting a flight or fare
- After clicking "Continue" on any step
- After payment submission

**Selector:**
```css
[data-testid='loading-spinner']
/* fallback */
.loading-plane
```

**Automation rule:**
```java
// ALWAYS wait for spinner disappearance before any next action
waitUtils.waitForSpinnerDisappear(By.cssSelector("[data-testid='loading-spinner'], .loading-plane"));
```

**Never proceed** to the next step without confirming the spinner has disappeared (max wait: 30s).

---

### 2. Cookie Consent Modal

**What it is:** GDPR cookie consent dialog. Appears on first load and after cookie clear.

**Selectors:**
```css
[data-testid='cookie-banner']        /* modal container */
[data-testid='cookie-accept']        /* accept all button */
[data-testid='cookie-reject']        /* reject non-essential */
[data-testid='cookie-settings']      /* manage preferences */
```

**Automation rule:**
- Handle at the **beginning of every test** that starts from the homepage
- Click accept, then wait for modal to disappear
- If modal is not present, continue silently (do not fail)

```java
protected void handleCookieConsent() {
    try {
        WebElement btn = driver.findElement(By.cssSelector("[data-testid='cookie-accept']"));
        if (btn.isDisplayed()) {
            btn.click();
            waitUtils.waitForElementDisappear(By.cssSelector("[data-testid='cookie-banner']"));
        }
    } catch (NoSuchElementException | TimeoutException ignored) { }
}
```

---

### 3. Promotional / Newsletter Popup

**What it is:** Marketing overlay that may appear after login or during flow.

**Selectors:**
```css
[data-testid='promo-close']
.modal-close
.newsletter-close
```

**Automation rule:**
- Close silently if present — do not fail if absent
- May reappear mid-flow — close again if needed
- Use `closePromoPopup()` in `BasePage` before any critical action

---

### 4. Payment iFrame

**What it is:** Credit card form rendered inside a sandboxed iframe for PCI compliance.

**Iframe selector:**
```css
#payment-iframe
[data-testid='payment-frame']
iframe[src*='payment']
```

**Automation rule:**
```java
// REQUIRED before interacting with any payment field
WebElement iframe = wait.until(
    ExpectedConditions.presenceOfElementLocated(By.cssSelector("#payment-iframe")));
driver.switchTo().frame(iframe);

// ... interact with card fields ...

driver.switchTo().defaultContent(); // return to main document
```

**Never** attempt to interact with card fields without switching iframe context first.

---

### 5. Station Autocomplete Dropdown

**What it is:** Airport search dropdown that appears when typing in origin/destination fields.

**Selectors:**
```css
[data-testid='station-suggestions']          /* dropdown container */
[data-testid='station-suggestion']           /* individual suggestion item */
[data-testid='station-suggestion']:first-child  /* first match */
```

**Automation rule:**
1. Click origin/destination input
2. Type IATA code (e.g., `BCN`)
3. Wait for dropdown to appear
4. Click first suggestion
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

### 6. Calendar Date Picker

**What it is:** Custom month-navigation calendar widget.

**Selectors:**
```css
[data-testid='calendar']                         /* container */
[data-testid='calendar-next']                    /* next month button */
[data-testid='calendar-prev']                    /* previous month button */
[data-testid='calendar-day-{YYYY-MM-DD}']        /* specific day cell */
```

**Automation rule:**
- Always use `LocalDate.now().plusDays(N)` — never hardcode a date
- Navigate months using next/prev buttons until target month is visible
- Use `CalendarComponent.selectDateFromToday(N)` for all date selections

---

### 7. i18n / Localization

**What it is:** Vueling web is multilingual (ES, EN, FR, DE, IT, PT…)

**Automation rule:**
- **Never use text-based selectors** for content that may change by locale
- Always prefer `data-testid`, `data-qa`, `id`, `name` attributes
- If text must be validated, use the `lang=en` URL parameter or set browser locale explicitly in test config
- Exception: language-specific test cases that explicitly test localization

---

## Selector Priority (Reference)

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
