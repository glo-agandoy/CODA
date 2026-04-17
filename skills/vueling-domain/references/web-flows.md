# Reference: web-flows
# Vueling Critical Web Flows & Page Sequences

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

## Flow 2: Online Check-in (4 Steps)

```
Step 1: Retrieve Booking
  └── Method A: PNR + last name (guest)
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

## Flow 3: My Booking (Manage Booking)

```
Entry:
  └── Login required OR PNR + last name

Actions available:
  └── Change flight (subject to fare rules)
  └── Add bags (per segment)
  └── Upgrade seat
  └── Cancel booking (TimeFlex only or with fee)
  └── View boarding pass (if checked in)
  └── Invoice download
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

## Transition Rules

- **Every page transition** requires waiting for the airplane spinner to disappear
- **Payment page** requires explicit `driver.switchTo().frame()` before interacting
- **Availability page** data is dynamic — never assert exact prices or availability counts
- **Confirmation page** PNR is dynamically generated — use fuzzy matcher `#string` or `#regex [A-Z0-9]{6}`
