# Reference: web-flows
# {AIRLINE_NAME} — Critical Web Flows & Page Sequences

> **CONFIGURATION REQUIRED:** Replace all `{placeholders}` with real values for your airline.
> See `references/vueling-example.md` for a complete filled-in example (Vueling Airlines).

---

## Base URL

```
{BASE_URL}   (e.g., https://www.airline.com)
```

> Use `${BASE_URL}` everywhere — never hardcode the URL in test code.
> Set via `config.properties` (Java), `conftest.py` (Python), or `playwright.config.ts` (TypeScript).

---

## Flow 1: Booking ({N} Steps — SEQUENTIAL, cannot skip)

```
Step 1: Homepage Search
  └── Fields: Origin (IATA), Destination (IATA), Outbound date (T+N),
              Return date (T+N or toggle off), Pax count ({PAX_TYPE_1}/{PAX_TYPE_2}/{PAX_TYPE_3}),
              Trip type (One Way / Round Trip)
  └── Action: Click "Search flights" button
  └── Transition: Loading spinner → Availability page

Step 2: Availability Selection
  └── Action: Choose outbound flight from results list
  └── Action: Choose fare family ({FARE_FAMILY_1} / {FARE_FAMILY_2} / {FARE_FAMILY_3})
  └── [Round Trip only] Choose return flight + fare family
  └── Transition: Loading spinner → Passenger Details page

Step 3: Passenger Details
  └── Fields per pax: First name, Last name, DOB, Document type, Document number
  └── Fields: Contact email, Contact phone
  └── Transition: "Continue" button → Loading spinner → Ancillaries page

Step 4: Ancillaries
  └── Options: Checked bags (per pax per segment), Seat selection, Insurance
  └── Transition: "Continue" → {next step}

Step 5: Extras
  └── Options: {e.g., Priority boarding, Rental car, Hotel partner offers}
  └── Transition: "Continue" → Payment page

Step 6: Payment
  ⚠️ iFrame context — switch required (PCI compliance)
  └── Fields: Card number, Expiry MM/YY, CVV, Cardholder name
  └── Action: "Pay" button → 3DS or direct confirmation
  └── Transition: Booking confirmation page with PNR
```

> Update the number of steps, step names, and field details to match the airline's actual booking funnel.
> The iFrame at payment is standard for most airlines — verify during web-exploration.

---

## Flow 2: Online Check-in ({N} Steps)

```
Step 1: Retrieve Booking
  └── Method A: PNR + last name (guest access)
  └── Method B: Login (registered user)
  └── Transition: Booking details page

Step 2: Passenger Selection
  └── Select who is checking in (checkbox per pax)
  └── Transition: "Continue" → Ancillaries or seat map

Step 3: Ancillaries (optional)
  └── Add bags if not purchased during booking
  └── Select or change seats
  └── Transition: "Continue"

Step 4: Boarding Pass
  └── Download PDF or send to email
  └── QR code displayed on screen
```

---

## Flow 3: My Booking (Manage Booking)

```
Entry:
  └── Login required OR PNR + last name

Actions available:
  └── Change flight (subject to fare rules)
  └── Add bags (per segment)
  └── Upgrade seat
  └── Cancel booking ({flexible fare} only or with fee)
  └── View boarding pass (if checked in)
  └── Invoice download
```

---

## Page Inventory

| Page Class Name | URL Pattern | Description |
|-----------------|-------------|-------------|
| `HomePage` | `/` | Main landing with search form |
| `AvailabilityPage` | `{/airline/availability}` | Flight results grid |
| `PassengerDetailsPage` | `{/airline/passengers}` | Pax data form |
| `AncillariesPage` | `{/airline/ancillaries}` | Bags, seats, insurance |
| `ExtrasPage` | `{/airline/extras}` | Partner offers |
| `PaymentPage` | `{/airline/payment}` | Payment iFrame |
| `ConfirmationPage` | `{/airline/confirmation}` | PNR + booking summary |
| `CheckInPage` | `{/airline/checkin}` | Check-in retrieval |
| `BoardingPassPage` | `{/airline/boardingpass}` | QR + PDF download |
| `MyBookingPage` | `{/airline/mybooking}` | Manage booking hub |

> Replace `{/airline/...}` with the actual URL paths of the target airline.
> Page class names (Java) are kept generic — adjust if the airline's flows differ significantly.
> Discover real URL patterns during the web-exploration phase.

---

## Transition Rules

- **Every page transition** requires waiting for the loading spinner to disappear
- **Payment page** requires explicit `driver.switchTo().frame()` before interacting with card fields
- **Availability page** data is dynamic — never assert exact prices or availability counts
- **Confirmation page** PNR is dynamically generated — use fuzzy matcher `#string` or `#regex {PNR_REGEX}`
