# Reference: business-rules
# {AIRLINE_NAME} — Business Rules & Edge Cases

> **CONFIGURATION REQUIRED:** Replace all `{placeholders}` with real values for your airline.
> The table below shows key configurable parameters — fill them in before running tests.
> See `references/vueling-example.md` for a complete filled-in example (Vueling Airlines).

---

## Configuration Constants

| Rule | Value | Notes |
|------|-------|-------|
| Max passengers per booking | `{N}` | e.g., 9 — verify with airline |
| Check-in opens | `{Nh before departure}` | e.g., 48h — varies by airline |
| Check-in closes | `{Nh before departure}` | e.g., 2h — varies by airline |
| Payment session timeout | `{N minutes}` | e.g., 15 min — varies by gateway |
| Default currency | `{EUR/USD/GBP/...}` | Set to airline's primary currency |
| 3D Secure | `{Yes/No/May appear}` | Depends on payment provider |
| Accepted card types | `{Visa, Mastercard, Amex, ...}` | Verify with payment provider |

---

## Booking Rules

| Rule | Description | Test Implication |
|------|-------------|-----------------|
| {PAX_TYPE_3} requires {PAX_TYPE_1} | {e.g., At least 1 adult per infant. Cannot book infant without adult.} | Test blocked state when infant count exceeds adult count |
| Future dates only | All flight search dates must be T+1 or later. No same-day or past dates. | Use T+N notation; assert validation error for past dates |
| PNR format | {e.g., Exactly 6 alphanumeric uppercase characters.} | Use regex `{PNR_REGEX}` for assertions |
| Booking flow order | Steps are sequential. Cannot access a later step without completing the prior one. | Assert redirect if URL accessed directly out of order |
| Max pax per booking | `{N}` passengers total (all types combined). | Test boundary at N and error at N+1 |
| {PAX_TYPE_2} age | {e.g., 2–11 years at time of travel.} | DOB validation — test boundary ages |

---

## Fare Rules

| Rule | Description |
|------|-------------|
| {FARE_FAMILY_1} — restrictions | {e.g., Entry fare — no free changes. Fees apply.} |
| {FARE_FAMILY_2} — inclusions | {e.g., Mid fare — includes 1 checked bag.} |
| {FARE_FAMILY_3} — flexibility | {e.g., Flex fare — free flight changes before departure.} |
| {FARE_FAMILY_4} — special pricing | {e.g., Family fare — discounted pricing for child passengers.} |

---

## Check-in Rules

| Rule | Description |
|------|-------------|
| Window | Online check-in opens `{N}h` before departure, closes `{N}h` before. |
| Seat required | Check-in requires a seat assignment. Selectable or auto-assigned. |
| {PAX_TYPE_3} check-in | {e.g., Infants do not require a seat — check-in handled as part of accompanying adult.} |
| Already checked in | Re-entering the check-in flow for a checked-in pax shows boarding pass directly. |

---

## Payment Rules

| Rule | Description |
|------|-------------|
| Card types | {Visa, Mastercard, American Express, ...} |
| Currency | Always `{DEFAULT_CURRENCY}` unless regional override. |
| 3DS | {3D Secure challenge may appear for non-exempt cards.} |
| Payment failure | On decline, user stays on payment page with error. Booking is NOT created. |
| Session timeout | Payment page has a `{N}`-minute session. After timeout, user must restart from availability. |

---

## Known Edge Cases for Automation

| Scenario | Expected Behavior | Tag |
|----------|-------------------|-----|
| Flight sold out | "No availability" message. No seats to select. | `@edge-case` |
| {PAX_TYPE_3} without {PAX_TYPE_1} | Validation error before search or pax selection | `@business-rule` |
| {e.g., Promo code expired} | {e.g., Error message shown; full price applied} | `@edge-case` |
| Session timeout ({N} min) | Redirected to availability with timeout message | `@edge-case` |
| Payment failure / decline | Error on payment page; booking NOT created | `@edge-case` |
| Dynamic price change mid-flow | Price refresh notification; user must confirm new price | `@edge-case` |
| Spinner timeout (>{N}s) | Increase wait, add retry; if still failing escalate | `@flaky` |
| iFrame not loading | Wait for iframe presence before switching; retry once | `@flaky` |
| Popup reappears mid-flow | Close again; log occurrence | `@known-issue` |
| Cookie consent after navigation | Handle again; some flows re-trigger consent | `@known-issue` |

> Add airline-specific edge cases here as they are discovered during exploratory testing.

---

## Validation Error Patterns

| Error | Trigger | Selector Pattern |
|-------|---------|-----------------|
| Required field missing | Submit without filling required field | `{[data-testid='field-error-{fieldName}']}` |
| Invalid IATA code | Typing non-airport in origin/destination | `{[data-testid='station-not-found']}` |
| Past date selected | Selecting a past date on calendar | `{[data-testid='date-error']}` |
| {PAX_TYPE_3} exceeds {PAX_TYPE_1} | Selecting more infants than adults | `{[data-testid='pax-error-inf']}` |
| Payment declined | Failed card transaction | `{[data-testid='payment-error']}` |

> Discover real error selectors during web-exploration. Update the patterns above with actual `data-testid` values.

---

## Test Data — Recommended Configurations

### Smoke Test (Fastest)
```
Origin:      {HUB_IATA}
Destination: {IATA_2}
Date:        T+7
Passengers:  1 {PAX_TYPE_1}
Fare:        {FARE_FAMILY_1}
Trip:        One Way
```

### Booking Full Flow
```
Origin:      {HUB_IATA}
Destination: {IATA_3}
Outbound:    T+14
Return:      T+21
Passengers:  2 {PAX_TYPE_1}, 1 {PAX_TYPE_2}
Fare:        {FARE_FAMILY_2}
Trip:        Round Trip
```

### Edge Case — Pax Validation
```
Origin:      {HUB_IATA}
Destination: {IATA_2}
Date:        T+7
Passengers:  0 {PAX_TYPE_1}, 0 {PAX_TYPE_2}, 1 {PAX_TYPE_3}  ← should trigger validation error
```
