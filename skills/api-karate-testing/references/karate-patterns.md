# Karate DSL Patterns Reference

## Table of Contents
1. [Core Language Patterns](#1-core-language-patterns)
2. [Request Construction](#2-request-construction)
3. [Response Assertion Patterns](#3-response-assertion-patterns)
4. [Schema Validation](#4-schema-validation)
5. [Chained / Multi-step Flows](#5-chained--multi-step-flows)
6. [Reusable Helpers (karate.call)](#6-reusable-helpers-karatecall)
7. [Data-Driven Testing](#7-data-driven-testing)
8. [Authentication Patterns](#8-authentication-patterns)
9. [Dynamic Data Generation](#9-dynamic-data-generation)
10. [Error and Negative Testing](#10-error-and-negative-testing)
11. [Allure Tags in Karate](#11-allure-tags-in-karate)
12. [Anti-Patterns to Avoid](#12-anti-patterns-to-avoid)

---

## 1. Core Language Patterns

### Variable assignment
```gherkin
* def bookingRef = response.bookingReference
* def flightId = response.flights[0].id
* def count = response.flights.length
```

### Embedded expressions (in strings)
```gherkin
* def url = baseUrl + '/v1/bookings/' + bookingRef
* def message = 'Booking ' + bookingRef + ' created'
```

### JavaScript inline
```gherkin
* def today = new Date().toISOString().substring(0, 10)
* def futureDate = function(days){ var d = new Date(); d.setDate(d.getDate()+days); return d.toISOString().substring(0,10) }
* def departDate = call futureDate 7
```

### Conditional logic
```gherkin
* def hasPromo = response.promotion != null
* if (hasPromo) karate.set('promoCode', response.promotion.code)
```

---

## 2. Request Construction

### Simple GET with path params
```gherkin
Given url baseUrl
And path '/v1/flights/' + flightId
When method GET
Then status 200
```

### POST with JSON body
```gherkin
Given url baseUrl
And path '/v1/bookings'
And request
  """
  {
    "flightId": "#(flightId)",
    "passengers": [
      { "type": "#(paxType)", "firstName": "Test", "lastName": "User" }
    ],
    "fareCode": "#(fareCode)"
  }
  """
When method POST
Then status 201
```

### Headers
```gherkin
Given header Authorization = 'Bearer ' + authToken
And header Content-Type = 'application/json'
And header X-Correlation-ID = java.util.UUID.randomUUID() + ''
```

### Query parameters
```gherkin
Given param origin = origin
And param destination = destination
And param departureDate = departureDate
And param adults = 1
```

### Multipart / form data
```gherkin
Given multipart file document = { read: 'test-doc.pdf', filename: 'boarding-pass.pdf', contentType: 'application/pdf' }
When method POST
Then status 200
```

---

## 3. Response Assertion Patterns

### Status and basic field
```gherkin
Then status 200
And match response.status == 'CONFIRMED'
And match response.bookingReference == '#notnull'
```

### Presence checks
```gherkin
And match response.price == '#present'
And match response.legs == '#[] #present'   # non-empty array
And match response.discount == '#? _ >= 0'  # predicate
```

### Fuzzy matchers
```gherkin
And match response.totalAmount == '#number'
And match response.currency == '#string'
And match response.createdAt == '#? _.length == 10'   # ISO date
And match response.passengers == '#[1]'               # exactly 1 element
And match response.passengers == '#[_ > 0]'           # at least 1
```

### Partial match (ignore extra fields)
```gherkin
And match response contains { status: 'CONFIRMED', bookingReference: '#string' }
```

### Header assertions
```gherkin
And match responseHeaders['Content-Type'][0] contains 'application/json'
And match responseHeaders['X-Request-Id'][0] == '#notnull'
```

---

## 4. Schema Validation

Define schemas as `def` blocks and reuse across scenarios:

```gherkin
* def passengerSchema =
  """
  {
    type: '#string',
    firstName: '#string',
    lastName: '#string',
    dateOfBirth: '##string'
  }
  """

* def flightSchema =
  """
  {
    id: '#string',
    origin: '#string',
    destination: '#string',
    departureDate: '#string',
    departureTime: '#string',
    arrivalTime: '#string',
    fares: '#[] #present',
    availableSeats: '#number'
  }
  """

* def bookingSchema =
  """
  {
    bookingReference: '#string',
    status: '#string',
    passengers: '#[] #present',
    totalAmount: '#number',
    currency: '#string',
    createdAt: '#string'
  }
  """

And match response == bookingSchema
And match response.passengers[0] == passengerSchema
```

Store common schemas in `helpers/schemas.js` and load with:
```gherkin
* def schemas = read('classpath:helpers/schemas.js')
And match response == schemas.booking
```

---

## 5. Chained / Multi-step Flows

The pattern for booking flows: each step captures output for the next.

```gherkin
Scenario: Complete booking and check-in flow
  # --- Search ---
  Given url baseUrl
  And path '/v1/flights/search'
  And params { origin: '#(origin)', destination: '#(destination)', date: '#(departureDate)' }
  When method GET
  Then status 200
  * def flight = response.flights[0]
  * def selectedFare = flight.fares[0]

  # --- Create booking ---
  Given path '/v1/bookings'
  And request { flightId: '#(flight.id)', fareCode: '#(selectedFare.code)', passengers: '#(passengers)' }
  When method POST
  Then status 201
  * def bookingRef = response.bookingReference
  * def bookingId = response.id

  # --- Confirm payment ---
  Given path '/v1/bookings/' + bookingId + '/payment'
  And request { method: 'CARD', token: '#(testCardToken)' }
  When method POST
  Then status 200
  And match response.paymentStatus == 'APPROVED'

  # --- Cleanup ---
  * call read('classpath:helpers/cleanup-helper.feature') { bookingId: '#(bookingId)' }
```

**Rule:** Always `def` any value you need in a later step. Never rely on `response` across `Given/When/Then` blocks.

---

## 6. Reusable Helpers (karate.call)

### Calling a helper feature
```gherkin
# Inline call (result goes to a variable)
* def authResult = call read('classpath:helpers/auth-helper.feature')
* def authToken = authResult.token

# Call with arguments
* def cleanupResult = call read('classpath:helpers/cleanup-helper.feature') { bookingId: '#(bookingId)' }
```

### Auth helper structure (`auth-helper.feature`)
```gherkin
Feature: Auth Helper
  Scenario:
    Given url authUrl
    And path '/oauth/token'
    And request { grant_type: 'client_credentials', client_id: '#(clientId)', client_secret: '#(clientSecret)' }
    When method POST
    Then status 200
    * def token = response.access_token
```

### Shared Background across files
Put common setup in `karate-config.js`, not per-feature. Feature-level `Background` should only contain scenario-specific variables.

---

## 7. Data-Driven Testing

### Scenario Outline with Examples table
```gherkin
Scenario Outline: Search with different pax combinations
  Given url baseUrl
  And path '/v1/flights/search'
  And params { origin: '<origin>', destination: '<destination>', adults: <adults>, children: <children> }
  When method GET
  Then status 200
  And match response.flights == '#[_ > 0]'

  Examples:
    | origin | destination | adults | children |
    | BCN    | MAD         | 1      | 0        |
    | BCN    | MAD         | 2      | 1        |
    | MAD    | LHR         | 1      | 0        |
```

### Dynamic data from JSON table
```gherkin
* def testData = read('classpath:helpers/test-data.json')

Scenario Outline: Validate fare pricing
  * def scenario = <row>
  Given url baseUrl
  And path '/v1/fares/' + scenario.fareCode
  When method GET
  Then status 200
  And match response.name == scenario.expectedName

  Examples:
    | row!             |
    | testData.fares[0] |
    | testData.fares[1] |
```

---

## 8. Authentication Patterns

### Bearer token (OAuth2 client credentials)
```javascript
// karate-config.js
var authResult = karate.callSingle('classpath:helpers/auth-helper.feature', config);
config.authToken = authResult.token;
```
Then in features:
```gherkin
* header Authorization = 'Bearer ' + authToken
```

### API Key
```gherkin
* header X-API-Key = apiKey
```

### Basic Auth
```gherkin
* configure headers = { Authorization: 'Basic ' + btoa(username + ':' + password) }
```

### Token refresh (long-running tests)
Use `karate.callSingle` in `karate-config.js` — token is acquired once per test run and shared across all features.

---

## 9. Dynamic Data Generation

### Date helpers (always use T+N offsets, never hardcoded dates)
```javascript
// helpers/data-factory.js
function fn(args) {
  var days = args && args.offset ? args.offset : 7;
  var d = new Date();
  d.setDate(d.getDate() + days);
  return d.toISOString().substring(0, 10);
}
```
Usage:
```gherkin
* def departDate = call read('classpath:helpers/data-factory.js') { offset: 7 }
* def returnDate = call read('classpath:helpers/data-factory.js') { offset: 14 }
```

### Unique identifiers
```gherkin
* def correlationId = java.util.UUID.randomUUID() + ''
* def testRef = 'AUTO-' + java.lang.System.currentTimeMillis()
```

### Passenger object builder
```javascript
// helpers/data-factory.js — extended
function fn(args) {
  var type = args.paxType || 'ADT';
  return {
    type: type,
    firstName: 'Test',
    lastName: 'Automation',
    dateOfBirth: type === 'ADT' ? '1985-06-15' : '2018-03-20',
    documentType: 'PASSPORT',
    documentNumber: 'TEST' + java.lang.System.currentTimeMillis()
  };
}
```

---

## 10. Error and Negative Testing

### Expected error responses
```gherkin
Scenario: Search with invalid IATA code returns 400
  Given url baseUrl
  And path '/v1/flights/search'
  And params { origin: 'INVALID', destination: 'MAD', date: '#(departureDate)' }
  When method GET
  Then status 400
  And match response.error.code == 'INVALID_IATA_CODE'
  And match response.error.message == '#string'
```

### Boundary conditions
```gherkin
Scenario: Max passengers exceeded returns 422
  Given url baseUrl
  And path '/v1/bookings'
  And request { flightId: '#(flightId)', passengers: '#(tooManyPassengers)' }
  When method POST
  Then status 422
  And match response.error.code == 'MAX_PASSENGERS_EXCEEDED'
```

### Unauthorized access
```gherkin
Scenario: Request without token returns 401
  Given url baseUrl
  And path '/v1/bookings'
  # Intentionally no Authorization header
  When method GET
  Then status 401
  And match response.error.code == 'UNAUTHORIZED'
```

---

## 11. Allure Tags in Karate

Apply tags at Feature and Scenario level using the `@` prefix. Allure picks them up automatically when the Allure-Karate adapter is in the pom.xml.

```gherkin
@epic=FlightBooking @feature=SearchFlights
Feature: Flight Search API

  @story=OneWaySearch @severity=blocker @smoke
  Scenario: Search one-way flight returns available options

  @story=NoResults @severity=normal @regression
  Scenario: Search with no availability returns empty list
```

Standard tag taxonomy:
| Tag type | Pattern | Example |
|----------|---------|---------|
| Epic | `@epic=<name>` | `@epic=FlightBooking` |
| Feature | `@feature=<name>` | `@feature=SearchFlights` |
| Story | `@story=<name>` | `@story=OneWaySearch` |
| Severity | `@severity=blocker|critical|normal|minor|trivial` | `@severity=blocker` |
| Suite | `@smoke` / `@regression` / `@e2e` | `@smoke` |

---

## 12. Anti-Patterns to Avoid

| Anti-pattern | Problem | Correct approach |
|---|---|---|
| `* def date = '2025-12-25'` | Hardcoded date breaks over time | Use `data-factory.js` with T+N offset |
| `Then status 200` only | No content validation | Always add at least one `match` assertion |
| `* def token = 'abc123'` | Hardcoded credential | Read from `karate-config.js` env var |
| `And path '/v1/BCN/MAD'` | Hardcoded IATA | Use `'#(origin)'` variables |
| Giant single `.feature` file | Hard to maintain | One feature file per logical flow |
| No cleanup after state-creating tests | Test pollution | Always call `cleanup-helper.feature` |
| `* def x = response` in step N then use in step N+3 | `response` is overwritten by each call | `def` the specific fields you need |
| Schema defined inline per scenario | Duplication | Define in `Background` or `helpers/schemas.js` |
