# Reference: business-rules
# Vueling Business Rules & Edge Cases

## Booking Rules

| Rule | Description | Test Implication |
|------|-------------|-----------------|
| INF requires ADT | At least 1 ADT per INF. Cannot book INF without an ADT. | Test blocked state when INF > ADT count |
| Future dates only | All flight search dates must be T+1 or later. No same-day or past dates. | Use T+N notation; assert validation error for past dates |
| PNR format | Exactly 6 alphanumeric uppercase characters. | Use regex `[A-Z0-9]{6}` for assertions |
| Booking flow order | Steps 1→6 are sequential. Cannot access Step 3 without completing Step 2. | Assert redirect if URL accessed directly |
| Max pax per booking | 9 passengers total (all types combined). | Test boundary at 9 and error at 10 |
| CHD age | 2–11 years at time of travel. Under 2 is INF. | DOB validation — test boundary ages |

## Fare Rules

| Rule | Description |
|------|-------------|
| Basic — no changes | Basic fare cannot be changed for free. Fees apply. |
| TimeFlex — free changes | TimeFlex allows unlimited free flight changes before departure. |
| Family — child pricing | Family fare applies discounted pricing for CHD passengers. |
| Optima — bag included | Optima includes 1×23kg checked bag. Adding a second bag is paid. |

## Check-in Rules

| Rule | Description |
|------|-------------|
| Window | Online check-in opens 48h before departure, closes 2h before. |
| Seat required | Check-in requires a seat assignment. Selectable or auto-assigned. |
| INF check-in | INF does not require a seat — check-in for INF is handled as part of the accompanying ADT. |
| Already checked in | Re-entering the check-in flow for a checked-in pax shows boarding pass directly. |

## Payment Rules

| Rule | Description |
|------|-------------|
| Card types | Visa, Mastercard, American Express accepted. |
| Currency | Always EUR unless regional override. |
| 3DS | 3D Secure challenge may appear for non-exempt cards. |
| Payment failure | On decline, user stays on payment page with error. Booking is NOT created. |
| Session timeout | Payment page has a 15-minute session. After timeout, user must restart from availability. |

## Known Edge Cases for Automation

| Scenario | Expected Behavior | Tag |
|----------|-------------------|-----|
| Flight sold out | "No availability" message. No seats to select. | `@edge-case` |
| Infant without adult | Validation error before search or pax selection | `@business-rule` |
| Promo code expired | Error message shown; full price applied | `@edge-case` |
| Session timeout (15 min) | Redirected to availability with timeout message | `@edge-case` |
| Payment failure / decline | Error on payment page; booking NOT created | `@edge-case` |
| Dynamic price change mid-flow | Price refresh notification; user must confirm new price | `@edge-case` |
| Spinner timeout (>30s) | Increase wait, add retry; if still failing escalate | `@flaky` |
| iFrame not loading | Wait for iframe presence before switching; retry once | `@flaky` |
| Popup reappears mid-flow | Close again; log occurrence | `@known-issue` |
| Cookie consent after navigation | Handle again; some flows re-trigger consent | `@known-issue` |

## Validation Error Patterns

| Error | Trigger | Selector Pattern |
|-------|---------|-----------------|
| Required field missing | Submit without filling required field | `[data-testid='field-error-{fieldName}']` |
| Invalid IATA code | Typing non-airport in origin/destination | `[data-testid='station-not-found']` |
| Past date selected | Selecting a past date on calendar | `[data-testid='date-error']` |
| INF exceeds ADT | Selecting more INF than ADT | `[data-testid='pax-error-inf']` |
| Payment declined | Failed card transaction | `[data-testid='payment-error']` |

## Test Data — Recommended Configurations

### Smoke Test (Fastest)
```
Origin:      BCN
Destination: MAD
Date:        T+7
Passengers:  1 ADT
Fare:        Basic
Trip:        One Way
```

### Booking Full Flow
```
Origin:      BCN
Destination: LGW
Outbound:    T+14
Return:      T+21
Passengers:  2 ADT, 1 CHD
Fare:        Optima
Trip:        Round Trip
```

### Edge Case — INF Rule
```
Origin:      BCN
Destination: MAD
Date:        T+7
Passengers:  0 ADT, 0 CHD, 1 INF  ← should trigger validation error
```
