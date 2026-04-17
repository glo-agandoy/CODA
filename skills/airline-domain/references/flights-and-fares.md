# Reference: flights-and-fares
# {AIRLINE_NAME} — Flights, Fares, Ancillaries & Passenger Types

> **CONFIGURATION REQUIRED:** Replace all `{placeholders}` with real values for your airline.
> See `references/vueling-example.md` for a complete filled-in example (Vueling Airlines).

---

## IATA Airport Codes

| Code | City | Role |
|------|------|------|
| `{HUB_IATA}` | {Hub City} | **Hub** |
| `{IATA_2}` | {City 2} | Key station |
| `{IATA_3}` | {City 3} | Key station |
| `{IATA_4}` | {City 4} | Key station |
| `{IATA_5}` | {City 5} | Secondary |
| `{IATA_6}` | {City 6} | Secondary |

**Rule:** Always use IATA codes in test data. Never use city names.

> Add or remove rows as needed. List all airports relevant to your test coverage.

---

## Flight Number Format

```
{AIRLINE_ICAO_PREFIX}xxxx   (e.g., {AIRLINE_ICAO_PREFIX}1001)
```

> The IATA carrier code is typically 2 characters (e.g., VY for Vueling, IB for Iberia, BA for British Airways).
> Verify with the airline's operations team.

---

## PNR Format

```
{PNR_FORMAT}   (e.g., 6-character alphanumeric uppercase — ABC123)
```

> Typical GDS standard: 6-character alphanumeric uppercase.
> Some airlines use longer or proprietary formats — verify with the reservation system.

---

## Passenger Types

| Code | Name | Age Range | Rules |
|------|------|-----------|-------|
| `{PAX_TYPE_1}` | {e.g., Adult} | {e.g., 12+ years} | {e.g., No restrictions} |
| `{PAX_TYPE_2}` | {e.g., Child} | {e.g., 2–11 years} | {e.g., Must travel with an adult} |
| `{PAX_TYPE_3}` | {e.g., Infant} | {e.g., Under 2 years} | {e.g., No seat assigned. Requires 1 adult per infant.} |

**Validation Rules:**
- {e.g., At least 1 adult required per booking}
- {e.g., Infant count cannot exceed adult count}
- {e.g., Maximum {N} passengers per booking (all types combined)}

> Some airlines include additional types: YOUNG_ADULT, SENIOR, STUDENT.
> Add rows as needed.

---

## Fare Families

| Fare | Tier | Includes | Changes | Refund |
|------|------|----------|---------|--------|
| `{FARE_FAMILY_1}` | {e.g., Entry} | {e.g., 1 cabin bag only} | {e.g., Paid} | {e.g., No} |
| `{FARE_FAMILY_2}` | {e.g., Mid} | {e.g., Cabin + 1 checked bag + seat} | {e.g., Paid} | {e.g., No} |
| `{FARE_FAMILY_3}` | {e.g., Flex} | {e.g., Cabin + free changes + refund} | {e.g., Free} | {e.g., Yes} |
| `{FARE_FAMILY_4}` | {e.g., Family} | {e.g., Family pricing + child discounts} | {e.g., Paid} | {e.g., No} |

> Add or remove rows. Some airlines use 2 fare tiers, others use 5 or more.

---

## Ancillaries

### Baggage

| Ancillary | Weight / Dimensions | Notes |
|-----------|-------------------|-------|
| Cabin bag | {e.g., 40x20x30cm, max 10kg} | {e.g., Included in all fares} |
| Checked bag S | {e.g., 10 kg} | {e.g., Add-on} |
| Checked bag M | {e.g., 20 kg} | {e.g., Add-on} |
| Checked bag L | {e.g., 32 kg} | {e.g., Add-on} |

### Seat Types

| Code | Name | Location | Notes |
|------|------|----------|-------|
| `{SEAT_TYPE_1}` | {e.g., Extra Legroom} | {e.g., Emergency exit rows} | {e.g., Premium price} |
| `{SEAT_TYPE_2}` | {e.g., Front} | {e.g., Rows 3–10} | {e.g., Fast disembark} |
| `{SEAT_TYPE_3}` | {e.g., Standard} | {e.g., General cabin} | {e.g., Base price} |

### Other Ancillaries

| Ancillary | Description |
|-----------|-------------|
| {e.g., Travel Insurance} | {e.g., Per pax, optional, offered at ancillaries step} |
| {e.g., Priority Boarding} | {e.g., First boarding group access} |
| {e.g., Rental Car} | {e.g., Partner add-on at extras step} |
| {e.g., Flex / Change Pack} | {e.g., Bundled change fee waiver + cancellation credit} |

---

## Test Data Standards

| Field | Standard | Example Value |
|-------|----------|---------------|
| Origin | IATA code | `{HUB_IATA}` |
| Destination | IATA code | `{IATA_2}` |
| Outbound date | T+N days from today | `T+7` |
| Return date | T+N days from today (or N/A) | `T+14` |
| Trip type | Enum | `One Way` or `Round Trip` |
| Passengers | Type+count | `1 {PAX_TYPE_1}` / `2 {PAX_TYPE_1}, 1 {PAX_TYPE_2}` |
| Fare family | Enum | `{FARE_FAMILY_1}` |
| Email | Env variable | `${TEST_USER_EMAIL}` |
| Password | Env variable | `${TEST_USER_PASSWORD}` |
| PNR | {PNR_FORMAT} | `{e.g., ABC123}` |

**Never hardcode:** credentials, past dates, city names, dynamic prices.
