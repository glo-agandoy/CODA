# Normalized Test Case: [TC-XXX] - [Clear, Action-Oriented Title]

## Metadata
- **Category:** [Smoke | Booking | Check-in | Manage | Payment | Ancillaries | Edge Case]
- **Priority:** [P0 | P1 | P2 | P3]
- **Severity:** [Critical | Major | Minor | Trivial]
- **Estimated Duration:** [X minutes]
- **Automation Complexity:** [Low | Medium | High]

## Pre-conditions
1. Browser is open at [application_url]
2. User state: [Guest (not logged in) | Logged In as registered user]
3. Cookie consent modal handled (accepted or dismissed)
4. No active booking in session
5. [Any other specific preconditions]

## Test Data

> Use IATA codes, T+N dates, and pax type codes per `skills/airline-domain/references/flights-and-fares.md`.

| Field | Value | Notes |
|-------|-------|-------|
| Origin | [IATA Code — see airline-domain hub] | Use hub for primary tests |
| Destination | [IATA Code — see airline-domain network] | |
| Outbound Date | [T+N, e.g., T+7] | Always future date |
| Return Date | [T+N, e.g., T+14 \| N/A] | N/A for one-way |
| Trip Type | [One Way \| Round Trip] | |
| Passengers | [e.g., 1 {PAX_TYPE_1} \| 2 {PAX_TYPE_1}, 1 {PAX_TYPE_2}] | Use airline pax type codes |
| Fare Family | [See airline-domain fare families] | |
| Ancillaries | [e.g., 1×{weight} checked bag, {seat type} \| None] | |
| Email | ${TEST_USER_EMAIL} | Environment variable — never hardcode |
| Password | ${TEST_USER_PASSWORD} | Environment variable — never hardcode |
| PNR | [{PNR example} \| N/A] | Only for retrieve booking flows |

## Steps

| # | Action | Expected Result |
|---|--------|-----------------|
| 1 | Navigate to [URL] | Homepage loads, search form is visible |
| 2 | [Precise action on specific element] | [Measurable, observable result] |
| 3 | [Precise action] | [Result] |
| … | … | … |

> **Spinner rule:** After any server-side transition (search, continue, submit), wait for the loading spinner to disappear before the next action (see `skills/airline-domain/references/ui-patterns.md` for the spinner selector).

> **Cookie rule:** Handle cookie consent modal on first interaction with the page.

## Assertions (Explicit Validations)

1. Verify `[data-testid='element-name']` is visible and displays `[expected value]`
2. Verify URL contains `[expected path fragment]`
3. Verify `[element]` state is `[enabled | disabled | selected]`
4. Verify `[count]` items are present in `[list element]`
5. [At minimum 1 assertion per major step]

## Edge Cases & Considerations

- **Spinner Handling:** Wait for loading spinner to disappear after each server transition. Selector from `skills/airline-domain/references/ui-patterns.md`.
- **Popup Handling:** Close promo/newsletter popup if it appears mid-flow. Selector from `skills/airline-domain/references/ui-patterns.md`.
- **iFrame (if payment):** Switch to payment iFrame context before card field interaction; return to default content after.
- **i18n:** Do not assert visible text that may change by locale — use `data-testid` selectors.
- **Dynamic Data:** Use fuzzy matchers for prices, PNRs, and flight numbers (values change per run).
- **Known Issues:** [Document any known flaky behavior or airline-specific quirk]

## Dependencies

- **Depends on:** [TC-XXX for [reason]] | None
- **Blocks:** [TC-YYY] | None
- **Shared test data:** [Describes if this test creates data used by other tests]
