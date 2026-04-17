# Example: Vueling Airlines — Complete Domain Configuration
#
# This file shows how a fully configured airline-domain looks.
# Use it as a reference when adapting the generic templates for a new airline.
# DO NOT modify this file — it is a read-only reference.
#
# To configure for a new airline:
#   1. Edit flights-and-fares.md, web-flows.md, ui-patterns.md, business-rules.md
#   2. Replace the {placeholders} with real values
#   3. This file remains unchanged as the Vueling reference

---

## AIRLINE_NAME = Vueling
## AIRLINE_ID = vueling
## BASE_URL = https://www.vueling.com
## JIRA_URL = https://jira.vueling.com
## TMS_URL = https://testlink.vueling.com
## QA_TEAM_EMAIL = qa-team@vueling.com

---

# flights-and-fares (Vueling)

## IATA Airport Codes

| Code | City | Role |
|------|------|------|
| BCN | Barcelona | **Hub** |
| MAD | Madrid | Key station |
| LGW | London Gatwick | Key station |
| ORY | Paris Orly | Key station |
| AMS | Amsterdam | Key station |
| FCO | Rome Fiumicino | Key station |
| VLC | Valencia | Secondary |
| SVQ | Seville | Secondary |
| PMI | Palma de Mallorca | Secondary |

## Flight Number Format
```
VYxxxx   (e.g., VY1001, VY2342, VY8412)
```

## PNR Format
```
6-character alphanumeric, uppercase   (e.g., ABC123, XY9Z01)
```

## Passenger Types

| Code | Name | Age Range | Rules |
|------|------|-----------|-------|
| ADT | Adult | 12+ years | No restrictions |
| CHD | Child | 2–11 years | Must travel with ADT |
| INF | Infant | Under 2 years | Requires at least 1 ADT per INF. No seat assigned. |

**Validation Rules:**
- At least 1 ADT required per booking
- INF count cannot exceed ADT count
- Maximum 9 passengers per booking (all types combined)

## Fare Families

| Fare | Tier | Includes | Changes | Refund |
|------|------|----------|---------|--------|
| Basic | Entry | 1 cabin bag (40x20x30cm) | Paid | No |
| Optima | Mid | Cabin + 1 checked bag (23kg) + seat | Paid | No |
| TimeFlex | Premium | Cabin + free changes + refund | Free | Yes |
| Family | Family | Family seating + child discounts | Paid | No |

## Ancillaries

### Baggage
| Ancillary | Weight | Notes |
|-----------|--------|-------|
| Cabin bag | 40x20x30cm | Included in all fares |
| Checked bag S | 10 kg | Add-on |
| Checked bag M | 20 kg | Add-on |
| Checked bag L | 32 kg | Add-on |

### Seat Types
| Code | Name | Location | Notes |
|------|------|----------|-------|
| SPACE_ONE | Space One | Row 1-2 | Extra legroom, premium |
| SPACE_PLUS | Space Plus | Emergency exits | Extra legroom |
| SPACE_FRONT | Space Front | Rows 3-10 | Forward cabin |
| STANDARD | Standard | General | Assigned seat |

### Other Ancillaries
| Ancillary | Description |
|-----------|-------------|
| Insurance | Travel insurance (per pax) |
| Flex Pack | Bundled: change fee waiver + cancellation |
| Priority Boarding | Boards in first group |
| Rental Car | Partner car rental |

## Test Data Standards

| Field | Standard | Example Value |
|-------|----------|---------------|
| Origin | IATA code | `BCN` |
| Destination | IATA code | `MAD` |
| Outbound date | T+N | `T+7` |
| Return date | T+N or N/A | `T+14` |
| Trip type | Enum | `One Way` or `Round Trip` |
| Passengers | Type+count | `1 ADT` / `2 ADT, 1 CHD` |
| Fare family | Enum | `Basic` |
| Email | Env var | `${TEST_USER_EMAIL}` |
| Password | Env var | `${TEST_USER_PASSWORD}` |
| PNR | 6-char alphanumeric | `ABC123` |

---

# web-flows (Vueling)

## Base URL
```
https://www.vueling.com
```

## Flow 1: Booking (6 Steps — SEQUENTIAL, cannot skip)

```
Step 1: Homepage Search
  └── Fields: Origin (IATA), Destination (IATA), Outbound date (T+N),
              Return date (T+N or toggle off), Pax count (ADT/CHD/INF),
              Trip type (One Way / Round Trip)
  └── Action: Click "Search flights"
  └── Transition: Airplane spinner → Availability page

Step 2: Availability Selection
  └── Action: Choose outbound flight from results list
  └── Action: Choose fare family (Basic / Optima / TimeFlex / Family)
  └── [Round Trip only] Choose return flight + fare family
  └── Transition: Airplane spinner → Passenger Details page

Step 3: Passenger Details
  └── Fields per pax: First name, Last name, DOB, Document type, Document number
  └── Fields: Contact email, Contact phone
  └── Transition: "Continue" button → Airplane spinner → Ancillaries page

Step 4: Ancillaries
  └── Options: Checked bags (per pax per segment), Seat selection, Insurance
  └── Transition: "Continue" → Extras page

Step 5: Extras
  └── Options: Priority boarding, Rental car, Hotel (partner offers)
  └── Transition: "Continue" → Payment page

Step 6: Payment
  ⚠️ iFrame context — switch required
  └── Fields: Card number, Expiry MM/YY, CVV, Cardholder name
  └── Action: "Pay" button → 3DS or direct confirmation
  └── Transition: Booking confirmation page with PNR
```

## Page Inventory

| Page Class Name | URL Pattern | Description |
|-----------------|-------------|-------------|
| `HomePage` | `/` | Main landing with search form |
| `AvailabilityPage` | `/vueling/availability` | Flight results grid |
| `PassengerDetailsPage` | `/vueling/passengers` | Pax data form |
| `AncillariesPage` | `/vueling/ancillaries` | Bags, seats, insurance |
| `ExtrasPage` | `/vueling/extras` | Partner offers |
| `PaymentPage` | `/vueling/payment` | Payment iFrame |
| `ConfirmationPage` | `/vueling/confirmation` | PNR + booking summary |
| `CheckInPage` | `/vueling/checkin` | Check-in retrieval |
| `BoardingPassPage` | `/vueling/boardingpass` | QR + PDF download |
| `MyBookingPage` | `/vueling/mybooking` | Manage booking hub |

---

# ui-patterns (Vueling)

## Loading Spinner
```css
[data-testid='loading-spinner']    /* primary */
.loading-plane                     /* fallback — Vueling-specific CSS class */
```
Max wait: 30s

## Cookie Consent Modal
```css
[data-testid='cookie-banner']      /* container */
[data-testid='cookie-accept']      /* accept all */
[data-testid='cookie-reject']      /* reject */
[data-testid='cookie-settings']    /* preferences */
```

## Promotional Popup
```css
[data-testid='promo-close']
.modal-close
.newsletter-close
```

## Payment iFrame
```css
#payment-iframe
[data-testid='payment-frame']
iframe[src*='payment']
```

## Station Autocomplete
```css
[data-testid='station-suggestions']           /* dropdown container */
[data-testid='station-suggestion']            /* item */
[data-testid='station-suggestion']:first-child /* first match */
[data-testid='station-suggestion'][data-iata='BCN']  /* by IATA — Vueling-specific attribute */
```

## Calendar Date Picker
```css
[data-testid='calendar']
[data-testid='calendar-next']
[data-testid='calendar-prev']
[data-testid='calendar-day-{YYYY-MM-DD}']
```

## Validation Error Selectors
```css
[data-testid='field-error-{fieldName}']
[data-testid='station-not-found']
[data-testid='date-error']
[data-testid='pax-error-inf']
[data-testid='payment-error']
```

---

# business-rules (Vueling)

## Configuration Constants

| Rule | Vueling Value |
|------|--------------|
| Max passengers per booking | 9 |
| Check-in opens | 48h before departure |
| Check-in closes | 2h before departure |
| Payment session timeout | 15 minutes |
| Default currency | EUR |
| 3D Secure | Yes (may appear for non-exempt cards) |
| Accepted cards | Visa, Mastercard, American Express |

## Booking Rules

- INF requires ADT (1 ADT minimum per INF; INF count cannot exceed ADT count)
- Future dates only (T+1 or later — no same-day booking)
- PNR format: exactly 6 alphanumeric uppercase characters `[A-Z0-9]{6}`
- Booking flow: steps 1→6 are sequential; direct URL access to later steps redirects
- Max 9 passengers total per booking
- CHD age: 2–11 years; Under 2 = INF

## Fare Rules

- Basic: no free changes; fees apply
- TimeFlex: unlimited free flight changes before departure
- Family: discounted pricing for CHD passengers
- Optima: includes 1×23kg checked bag; second bag is paid

## Check-in Rules

- Window: opens 48h before departure, closes 2h before
- Seat required (selectable or auto-assigned)
- INF: no separate seat; handled with accompanying ADT
- Already checked in: re-entering flow shows boarding pass directly

## Test Data — Recommended Configurations

### Smoke Test
```
Origin: BCN | Destination: MAD | Date: T+7 | Pax: 1 ADT | Fare: Basic | Trip: One Way
```

### Full Booking Flow
```
Origin: BCN | Destination: LGW | Outbound: T+14 | Return: T+21 | Pax: 2 ADT, 1 CHD | Fare: Optima | Trip: Round Trip
```

### Edge Case — INF Validation
```
Origin: BCN | Destination: MAD | Date: T+7 | Pax: 0 ADT, 0 CHD, 1 INF  ← triggers validation error
```

---

# allure.properties (Vueling)

```properties
allure.link.issue.pattern=https://jira.vueling.com/browse/{}
allure.link.tms.pattern=https://testlink.vueling.com/case/{}
allure.report.name=Vueling UI Automation Report
```

# CI/CD (Vueling)

```
Jenkins credentials: credentials('vueling-test-email') / credentials('vueling-test-password')
QA email: qa-team@vueling.com
GitHub Actions secrets: TEST_USER_EMAIL, TEST_USER_PASSWORD
```
