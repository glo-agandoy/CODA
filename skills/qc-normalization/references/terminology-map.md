# Reference: terminology-map
# Airline Domain Terminology Replacement Guide

Apply this mapping when normalizing test cases.
Replace all generic terms with their airline-specific equivalents.

---

## Section 1: Universal Aviation Terms
# These terms are standard across the airline industry.
# They apply to most airlines with minimal or no modification.

| Generic Term | Aviation Standard | Notes |
|--------------|------------------|-------|
| city / airport | IATA code (e.g., BCN, MAD, LGW…) | Always 3-letter IATA code |
| passenger | Pax | Universal shorthand in aviation |
| booking reference / confirmation number | PNR | Passenger Name Record — 6-char GDS standard |
| departure date | Outbound date | Use T+N notation (e.g., T+7) |
| return date | Return date | Use T+N notation or N/A for one-way |
| one way / single | One Way | Trip type |
| round trip / return | Round Trip | Trip type |
| check-in | Online Check-in | Specify "online" for web flows |
| boarding card | Boarding Pass | PDF or QR code format |
| confirmation | Booking Confirmation | Shows PNR on success |
| payment page | Payment (iFrame) | Inside iframe — PCI compliance |
| login / sign in | Login | Via email + password |
| my account | My Bookings | Manage booking section |
| "tomorrow" / "next week" / hardcoded dates | T+N notation | T+1, T+7, T+14, T+30 |
| "username" / "user@test.com" | `${TEST_USER_EMAIL}` | Environment variable |
| "password" / "123456" | `${TEST_USER_PASSWORD}` | Environment variable |

---

## Section 2: Airline-Specific Terms
# These terms are specific to the configured airline.
# Update this section when adapting for a different airline.
# See `skills/airline-domain/references/flights-and-fares.md` for the full airline configuration.

| Generic Term | Airline Term | Notes |
|--------------|-------------|-------|
| adult | `{PAX_TYPE_1}` | e.g., ADT — Age {12+} |
| child | `{PAX_TYPE_2}` | e.g., CHD — Age {2–11} |
| baby / infant | `{PAX_TYPE_3}` | e.g., INF — Under {2} years |
| flight class / ticket tier | Fare Family | {FARE_FAMILY_1} / {FARE_FAMILY_2} / {FARE_FAMILY_3} / {FARE_FAMILY_4} |
| extra / add-on | Ancillary | Bags / Seats / Insurance / {airline-specific bundles} |
| bag / luggage | Checked bag | {10kg / 20kg / 32kg} |
| hand luggage / carry-on | Cabin bag | {40x20x30cm} |
| seat upgrade | Seat ancillary | {SEAT_TYPE_1} / {SEAT_TYPE_2} / {SEAT_TYPE_3} |
| travel insurance | Insurance | Per pax |
| flexible ticket | {FARE_FAMILY_3} | Flexible fare |
| flight number | Flight Number | Format {AIRLINE_ICAO_PREFIX}xxxx |
| economy class | {FARE_FAMILY_1} or {FARE_FAMILY_2} | Specify fare family |
| business class | {FARE_FAMILY_3} | Premium fare |
| "2 adults 1 child" | 2 {PAX_TYPE_1}, 1 {PAX_TYPE_2} | Pax type notation |
| "with baby" | + 1 {PAX_TYPE_3} | Must also have {PAX_TYPE_1} |
| "booking number" | PNR | {PNR_FORMAT} |

### City → IATA Code Mapping (configure for your airline's network)

| City / Region | IATA Code | Role |
|---------------|-----------|------|
| {Hub City} | `{HUB_IATA}` | Hub |
| {City 2} | `{IATA_2}` | Key station |
| {City 3} | `{IATA_3}` | Key station |
| {City 4} | `{IATA_4}` | Key station |
| {City 5} | `{IATA_5}` | Secondary |

> Fill in the city-to-IATA mapping from `skills/airline-domain/references/flights-and-fares.md`.
> See `skills/airline-domain/references/vueling-example.md` for a complete example (BCN, MAD, LGW, ORY, AMS, FCO...).

---

## Step-Level Replacements

| Generic Step | Normalized Step |
|--------------|----------------|
| "Go to the website" | Navigate to `${BASE_URL}` (from airline-domain config) |
| "Log in with your account" | Log in using `${TEST_USER_EMAIL}` / `${TEST_USER_PASSWORD}` |
| "Enter where you want to go" | Enter destination IATA code in origin/destination field |
| "Choose a date" | Select outbound date T+N using CalendarComponent |
| "Add a passenger" | Select 1 `{PAX_TYPE_1}` in passenger selector |
| "Pick a seat" | Select seat ancillary: `{SEAT_TYPE_1}` / `{SEAT_TYPE_2}` / `{SEAT_TYPE_3}` |
| "Add luggage" | Select checked bag ancillary: {weight options} |
| "Pay for the booking" | Complete payment form inside payment iFrame |
| "Check the confirmation" | Verify booking confirmation page displays PNR matching `{PNR_REGEX}` |

---

## Assertion Replacements

| Generic Assertion | Normalized Assertion |
|------------------|---------------------|
| "The page should show results" | Flight results element is visible and contains at least 1 flight |
| "The booking should be confirmed" | Booking confirmation page shows PNR matching `{PNR_REGEX}` |
| "Should see an error" | Error element is visible with specific error message |
| "Should be logged in" | User avatar/icon replaces login button in header navigation |
| "The price should be shown" | Price element is visible (use fuzzy matcher — prices are dynamic) |
