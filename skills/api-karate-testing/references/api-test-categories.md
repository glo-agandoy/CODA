# API Test Categories — Airline Domain

## Purpose

This reference defines how to classify API test scenarios and map them to feature files.
The categorization is generic — the actual endpoints, payload fields, and business rules
come from `skills/airline-domain/references/` for the specific airline being tested.

---

## Category Map

| Category | Feature File | Triggers | Priority |
|----------|-------------|----------|----------|
| `search` | `features/search/flight-search.feature` | Any test involving flight availability, pricing, calendar queries | P0 |
| `booking` | `features/booking/booking-flow.feature` | Create reservation, select seat, add ancillaries, booking state transitions | P0 |
| `payment` | `features/payment/payment.feature` | Payment processing, tokenization, refund, void | P0 |
| `checkin` | `features/checkin/checkin.feature` | Online check-in, boarding pass retrieval, seat assignment at check-in | P1 |
| `manage` | `features/manage/manage-booking.feature` | Cancel, change flight, name change, add bags post-booking | P1 |
| `auth` | `features/auth/auth.feature` | Login, token refresh, session management, unauthorized access | P0 |
| `profile` | `features/profile/profile.feature` | Loyalty account, saved passengers, stored payment methods | P2 |
| `notifications` | `features/notifications/notifications.feature` | Email/SMS triggers, push notification endpoints | P2 |
| `contracts` | `features/contracts/schema-contracts.feature` | Pure schema validation against OpenAPI spec — no business flow | P1 |

---

## How to Classify a Scenario

When parsing normalized test cases or an OpenAPI spec, use this decision tree:

```
Does the scenario involve checking available flights or prices?
  → YES → category: search

Does the scenario create or modify a booking record?
  → YES (create) → category: booking
  → YES (modify/cancel) → category: manage

Does the scenario process a payment or refund?
  → YES → category: payment

Does the scenario issue a boarding pass or seat assignment at check-in time?
  → YES → category: checkin

Does the scenario only validate token acquisition or API security?
  → YES → category: auth

Is the scenario purely validating a response schema (no business assertion)?
  → YES → category: contracts

None of the above → default to the closest domain verb; document assumption
```

---

## Priority Levels

| Level | Meaning | Run in |
|-------|---------|--------|
| P0 | Core booking funnel — must always pass | `@smoke` + `@regression` |
| P1 | Important post-booking flows | `@regression` |
| P2 | Secondary features | `@regression` (optional in smoke) |
| P3 | Edge cases and negative paths | `@regression` |

---

## Feature File Structure (per category)

Every feature file follows this structure regardless of category:

```gherkin
@epic={EpicName} @feature={FeatureName}
Feature: {Description}

  Background:
    * url baseUrl
    * def authToken = authResult.token
    * def origin   = karate.properties['TEST_ORIGIN_IATA'] || '{HUB_IATA}'
    * def dest     = karate.properties['TEST_DEST_IATA']   || '{DEST_IATA}'
    * def depDate  = call read('classpath:helpers/data-factory.js') { offset: 7 }

  @story={Story} @severity=blocker @smoke
  Scenario: {happy path}
    ...

  @story={Story} @severity=normal @regression
  Scenario: {error path}
    ...

  @story={Story} @severity=minor @regression
  Scenario Outline: {data driven}
    ...
    Examples: ...
```

---

## Scenario Classification from Normalized Test Cases

When a normalized test case does not explicitly state it is an API test, infer from these signals:

| Signal in test case | Inferred category |
|---------------------|-------------------|
| "call endpoint", "send request", "HTTP", "API" | whichever domain matches |
| "status 200", "response body", "JSON payload" | contracts or matching category |
| "search for flights", "check availability" | search |
| "create booking", "select fare" | booking |
| "process payment", "enter card" | payment (if API) |
| "cancel booking", "change flight" | manage |
| "check in", "boarding pass" | checkin |
| "login", "token", "session", "unauthorized" | auth |

If no inference is possible, default to `booking` and document the assumption.

---

## Shared Test Data Conventions

These conventions apply across all categories. Concrete values come from `airline-domain`:

| Data Point | Convention | Source |
|------------|-----------|--------|
| Origin IATA | env var `TEST_ORIGIN_IATA`, fallback to hub | `airline-domain/flights-and-fares.md` |
| Destination IATA | env var `TEST_DEST_IATA`, fallback to secondary hub | `airline-domain/flights-and-fares.md` |
| Departure date | T+7 (minimum), never T+0 or T+1 | `helpers/data-factory.js` |
| Return date | T+14 for round trips | `helpers/data-factory.js` |
| Pax type | ADT default; CHD/INF per business rules | `airline-domain/flights-and-fares.md` |
| Fare family | First available fare in search result, or cheapest | `airline-domain/flights-and-fares.md` |
| Test card | Read from env var `TEST_CARD_TOKEN` | `karate-config.js` |
| Test passenger name | Always `Test Automation` — never real names | hardcoded constant |

---

## Cleanup Requirements by Category

| Category | Creates state? | Cleanup required |
|----------|---------------|-----------------|
| search | No | None |
| booking | Yes | Cancel booking via manage API |
| payment | Yes | Void/refund via payment API |
| checkin | Depends | Reset check-in status if API allows |
| manage | Yes (if modifying) | Reverse the change if possible |
| auth | No | None |
| profile | Depends | Remove test data if created |
| contracts | No | None |

Always call `cleanup-helper.feature` when `Creates state? = Yes`.

---

## Allure Epic Mapping

The Allure Epic for each category should reflect the airline's booking funnel:

| Category | Allure Epic | Allure Feature |
|----------|-------------|----------------|
| search | `FlightSearch` | `AvailabilitySearch` / `PriceCalendar` |
| booking | `FlightBooking` | `ReservationCreation` / `SeatSelection` |
| payment | `FlightBooking` | `PaymentProcessing` / `Refund` |
| checkin | `CheckIn` | `OnlineCheckIn` / `BoardingPass` |
| manage | `ManageBooking` | `Cancellation` / `FlightChange` |
| auth | `Security` | `Authentication` / `Authorization` |
| contracts | `APIContracts` | `SchemaValidation` |

These values should be adapted in the feature file tags to match the airline's domain terminology from `airline-domain/flights-and-fares.md`.
