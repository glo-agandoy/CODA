# Reference: flights-and-fares
# Vueling Flights, Fares, Ancillaries & Passenger Types

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

**Rule:** Always use IATA codes in test data. Never use city names.

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
| INF | Infant | Under 2 years | **Requires at least 1 ADT per INF. No seat assigned.** |

**Validation Rules:**
- At least 1 ADT required per booking
- INF count cannot exceed ADT count
- Maximum 9 passengers per booking (all types combined)

## Fare Families

| Fare | Tier | Includes | Changes | Refund |
|------|------|----------|---------|--------|
| Basic | Entry | 1 cabin bag (40x20x30cm) | Paid | No |
| Optima | Mid | Cabin + 1 checked bag (23kg) + seat | Paid | No |
| TimeFlex | Premium | Cabin + changes + refund | Free | Yes |
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
| Outbound date | T+N days from today | `T+7` |
| Return date | T+N days from today (or N/A) | `T+14` |
| Trip type | Enum | `One Way` or `Round Trip` |
| Passengers | Type+count | `1 ADT` / `2 ADT, 1 CHD` |
| Fare family | Enum | `Basic` |
| Email | Env variable | `${TEST_USER_EMAIL}` |
| Password | Env variable | `${TEST_USER_PASSWORD}` |
| PNR | 6-char alphanumeric | `ABC123` |

**Never hardcode:** credentials, past dates, city names, dynamic prices.
