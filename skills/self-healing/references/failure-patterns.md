# Reference: failure-patterns
# Failure Classification & Repair Strategies

## Classification Table

| Error Type | Stack Trace Indicator | Self-Healable | Max Attempts |
|------------|----------------------|---------------|--------------|
| Element Not Found | `NoSuchElementException` | YES | 2 |
| Timeout | `TimeoutException`, `WebDriverWait` | YES | 2 |
| Stale Element | `StaleElementReferenceException` | YES | 2 |
| Click Intercepted | `ElementClickInterceptedException` | YES | 2 |
| iFrame Issue | element inside iframe, no context switch | YES | 2 |
| Assertion Failure | `AssertionError` / `AssertionFailedError` | MAYBE | 1 |
| Navigation Error | wrong URL, redirect loop | MAYBE | 1 |
| Environment Error | `Connection refused`, `ERR_NAME_NOT_RESOLVED` | NO | 0 |
| Test Data Error | invalid data causing 4xx/5xx in app | NO | 0 |
| Compilation Error | syntax/type error in source code | NO | 0 |
| Zero Tests Found | `No tests found` / `0 test cases` | NO | 0 |

---

## Repair Strategy A: Element Not Found

**Indicator:** `NoSuchElementException: Unable to locate element: {method:css selector}`

**Root cause:** Selector changed after a UI update.

**Repair steps:**
1. Extract the failing selector from the stack trace
2. Identify the Page Object file and line number from the stack trace
3. Invoke `web-exploration` skill in `revalidation` mode for that element
4. Apply the validated replacement selector to the `@FindBy` annotation

**Java example:**
```java
// BEFORE (failing selector from stack trace)
@FindBy(css = "#old-search-btn")
private WebElement searchButton;

// AFTER (validated replacement from web-exploration)
@FindBy(css = "[data-testid='search-submit']")
private WebElement searchButton;
```

**Python example:**
```python
# BEFORE
SEARCH_BUTTON = (By.ID, "old-search-btn")

# AFTER
SEARCH_BUTTON = (By.CSS_SELECTOR, "[data-testid='search-submit']")
```

**Never apply without web-exploration validation first.**

---

## Repair Strategy B: Timeout

**Indicator:** `TimeoutException: Expected condition failed: waiting for visibility of element`

**Root cause:** Element takes longer than configured timeout, OR selector is correct but the element loads slowly (e.g., after a spinner that wasn't awaited).

**Repair steps — attempt in order:**

**B1 — Add spinner wait before the failing action:**
```java
// Add this line before the action that times out
waitUtils.waitForSpinnerDisappear(LOADING_SPINNER);
// Then proceed with the original action
element.click();
```

**B2 — Increase timeout for this specific wait:**
```java
// BEFORE
new WebDriverWait(driver, Duration.ofSeconds(5))
    .until(ExpectedConditions.visibilityOfElementLocated(locator));

// AFTER
new WebDriverWait(driver, Duration.ofSeconds(15))
    .until(ExpectedConditions.visibilityOfElementLocated(locator));
```

**B3 — Change wait condition (presence → visibility):**
```java
// BEFORE — waiting for visibility directly
wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

// AFTER — presence first, then visibility
wait.until(ExpectedConditions.presenceOfElementLocated(locator));
wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
```

Apply B1 first. If still failing on retry, apply B2. Never apply B3 without understanding why presence differs from visibility for this element.

---

## Repair Strategy C: Stale Element

**Indicator:** `StaleElementReferenceException: element is not attached to page document`

**Root cause:** The element was found, then a DOM-changing action (spinner, AJAX, navigation) refreshed the DOM, making the old reference invalid.

**Repair steps:**

```java
// BEFORE — element found once, used after DOM refresh
WebElement btn = driver.findElement(By.cssSelector("[data-testid='confirm-btn']"));
waitForPageLoad(); // DOM refreshes here
btn.click();       // Stale! btn reference is now invalid

// AFTER — re-locate after DOM-changing action
WebElement btn = driver.findElement(By.cssSelector("[data-testid='confirm-btn']"));
waitForPageLoad();
btn = driver.findElement(By.cssSelector("[data-testid='confirm-btn']")); // re-locate
btn.click();
```

Alternatively, use `waitForClickable()` which implicitly handles stale references:
```java
waitUtils.waitForClickable(By.cssSelector("[data-testid='confirm-btn']")).click();
```

---

## Repair Strategy D: Click Intercepted

**Indicator:** `ElementClickInterceptedException: element click intercepted`

**Root cause:** An overlay (spinner, cookie modal, promo popup, loading overlay) is covering the element.

**Repair steps — apply in order:**

**D1 — Add spinner wait before click:**
```java
waitUtils.waitForSpinnerDisappear(LOADING_SPINNER);
element.click();
```

**D2 — Add popup close before click:**
```java
closePromoPopup();
scrollToElement(element);
element.click();
```

**D3 — Scroll to element first:**
```java
scrollToElement(element);
element.click();
```

**D4 — JavaScript click as last resort:**
```java
try {
    element.click();
} catch (ElementClickInterceptedException e) {
    clickWithJS(element); // JS bypasses overlay
}
```

---

## Repair Strategy E: iFrame Issue

**Indicator:** Element interactions fail on the payment page with `NoSuchElementException` inside an iframe, or `UnexpectedTagNameException`.

**Root cause:** Code interacts with payment form fields without switching to the iframe context first.

**Repair steps:**

```java
// BEFORE — direct interaction (fails — element is inside iframe)
driver.findElement(By.id("cardNumber")).sendKeys("4111111111111111");

// AFTER — switch context first
WebElement iframe = waitUtils.waitForPresence(
    By.cssSelector("#payment-iframe, [data-testid='payment-frame']"));
driver.switchTo().frame(iframe);
driver.findElement(By.id("cardNumber")).sendKeys("4111111111111111");
driver.switchTo().defaultContent(); // ALWAYS return to main document
```

**Key rule:** `driver.switchTo().defaultContent()` MUST be called after every iframe interaction, before any other page action.

---

## Repair Strategy F: Assertion Failure (Maybe-Healable)

**Indicator:** `AssertionError: expected [X] but found [Y]`

**Classify as healable ONLY if:**
- The assertion is on a dynamic value that the test was incorrectly asserting as exact (e.g., exact count in a list that grows over test runs)
- Fix: relax exact assertion to fuzzy/range assertion

**Do NOT attempt repair if:**
- The assertion reveals a genuine functional issue (page shows wrong content)
- The expected value comes from the normalized test case specification

**If healable — relax the assertion:**
```java
// BEFORE — exact count that breaks as data accumulates
Assert.assertEquals(flightCount, 5, "Expected exactly 5 flights");

// AFTER — range assertion (more resilient)
Assert.assertTrue(flightCount >= 1, "Expected at least 1 flight to be available");
```

**If not healable → immediately move to unresolved_issues.**

---

## Unresolved Issue Documentation

When a test cannot be repaired after max retries:

```json
{
  "test": "PaymentTest.testCreditCardPayment",
  "error_type": "iFrameError",
  "attempts": 2,
  "last_error": "Unable to switch to frame: element not found after 15s",
  "recommendation": "Payment iFrame selector may have changed. Inspect payment page with browser DevTools: look for <iframe> tags in the payment section. Check if the iframe URL changed to a new payment provider domain.",
  "needs_human": true
}
```

**Recommendation must be actionable** — "manual investigation needed" is not sufficient. Provide the specific investigation steps.
