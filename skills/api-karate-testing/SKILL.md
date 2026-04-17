# Skill: api-karate-testing
# API Test Engineer — Karate DSL Feature Generation

## Role & Context

Act as a **Senior API Test Automation Engineer** specialized in Karate DSL for airline REST APIs.
You receive normalized test cases (from `qc-normalization`) and/or an OpenAPI spec, and produce
a complete, runnable Karate Maven project with feature files, runner, config, and helpers.

This skill runs **independently** of the UI pipeline — it can be invoked in parallel with
`web-exploration` after Phase 1 completes, or standalone at any point.

**Never generate feature files before reading the airline domain context.**
Every endpoint, payload field, and test data value must align with the airline's domain configuration.

---

## Domain References

| Reference | Load When |
|-----------|-----------|
| `skills/airline-domain/references/flights-and-fares.md` | Writing request payloads — IATA codes, fare families, pax types, dates |
| `skills/airline-domain/references/business-rules.md` | Validating response assertions — booking rules, fare conditions, error codes |
| `references/karate-patterns.md` | Choosing the right Karate DSL pattern for each scenario type |
| `references/api-test-categories.md` | Classifying scenarios and mapping them to Karate feature files |

---

## Bundled Templates

Copy and adapt — do NOT write from scratch.

| Template | Target Path | Purpose |
|----------|-------------|---------|
| `assets/templates/pom.xml` | `api-tests/pom.xml` | Maven project with Karate + Allure |
| `assets/templates/KarateRunner.java` | `api-tests/src/test/java/runner/KarateRunner.java` | JUnit5 parallel runner |
| `assets/templates/karate-config.js` | `api-tests/src/test/resources/karate-config.js` | Environment-aware base config |
| `assets/templates/booking-flow.feature` | `api-tests/src/test/resources/features/booking/booking-flow.feature` | Reference chained-call feature |

---

## Inputs

```json
{
  "normalized_test_cases_path": "normalized-tests/normalized-test-cases.md | null",
  "openapi_spec_path": "path/to/openapi.yaml | null",
  "api_base_url": "https://api.{airline}.com | ${API_BASE_URL}",
  "airline_domain": "skills/airline-domain/SKILL.md",
  "environments": ["dev", "staging", "prod"],
  "auth_type": "bearer | apikey | basic | none",
  "parallel_features": 5
}
```

At least one of `normalized_test_cases_path` or `openapi_spec_path` must be provided.
When both are available, normalized test cases take precedence for scenario intent; the spec is used for payload schema validation.

---

## Outputs

```json
{
  "status": "COMPLETED | BLOCKED",
  "project_path": "api-tests/",
  "files_created": {
    "features": [],
    "helpers": [],
    "config": [],
    "runner": [],
    "pom": "api-tests/pom.xml"
  },
  "scenarios_generated": 0,
  "endpoints_covered": [],
  "assumptions": [],
  "escalations": []
}
```

---

## Critical Rules

1. **No hardcoded dates** — always use Karate's `#(futureDate)` helper or JS `new Date()` + offset
2. **No hardcoded credentials** — always read from `karate-config.js` via environment variables
3. **No hardcoded IATA codes in scenarios** — define them as variables in `Background` or `karate-config.js`
4. **Chain calls correctly** — booking flows require extracting IDs/tokens from prior responses; always use `def` to capture and pass them
5. **Schema validation is mandatory** — every response must be validated with `match` against a schema, not just status code
6. **Allure tags on every Scenario** — `@epic`, `@feature`, `@story`, `@severity` as Karate tags
7. **Idempotency** — tests that create bookings must clean up after themselves (cancel/void) or use test-specific namespaced data
8. **Environment isolation** — `karate-config.js` must support at minimum `dev` and `staging` profiles

---

## Execution Workflow

```
STEP 1 — LOAD DOMAIN CONTEXT
  [READ REFERENCE: skills/airline-domain/references/flights-and-fares.md]
  [READ REFERENCE: skills/airline-domain/references/business-rules.md]
  Extract: base URLs, IATA codes, fare families, pax types, auth patterns, error codes
  ↓
STEP 2 — PARSE INPUTS
  If normalized_test_cases_path provided:
    Read file → extract scenarios tagged or typed as "API" or "backend"
    If none explicitly tagged, infer from step descriptions (HTTP methods, endpoints, status codes)
  If openapi_spec_path provided:
    Read spec → extract endpoints, methods, request/response schemas
    Cross-reference with normalized test cases (match by operation or flow step)
  ↓
STEP 3 — CLASSIFY SCENARIOS
  [READ REFERENCE: references/api-test-categories.md]
  Assign each scenario to a category → determines feature file grouping
  ↓
STEP 4 — SCAFFOLD MAVEN PROJECT
  [COPY TEMPLATE: assets/templates/pom.xml]
  [COPY TEMPLATE: assets/templates/KarateRunner.java]
  [COPY TEMPLATE: assets/templates/karate-config.js]
  Adapt: group ID, artifact ID, base URL, auth type, environment profiles
  Create directory structure:
    api-tests/
    ├── karate-config.js
    ├── logback-test.xml
    ├── pom.xml
    └── src/test/
        ├── java/runner/KarateRunner.java
        └── resources/
            ├── karate-config.js
            ├── helpers/
            │   ├── auth-helper.feature
            │   └── cleanup-helper.feature
            └── features/
                ├── search/
                ├── booking/
                ├── payment/
                ├── checkin/
                └── manage/
  ↓
STEP 5 — GENERATE HELPERS
  auth-helper.feature — handles token acquisition (reusable via karate.call)
  cleanup-helper.feature — cancels/voids bookings created during tests
  data-factory.js — generates dynamic test data (T+N dates, passenger objects)
  ↓
STEP 6 — GENERATE FEATURE FILES
  [READ REFERENCE: references/karate-patterns.md]
  [COPY TEMPLATE: assets/templates/booking-flow.feature] (for chained flows)
  For each category group:
    - Create one .feature file per logical flow
    - Write Background with shared variables (base URL, auth token, IATA codes)
    - Write Scenario or Scenario Outline per test case
    - Apply schema validation on every response
    - Add Allure tags
    - Add cleanup call in @AfterScenario if test creates state
  ↓
STEP 7 — REPORT
  Return output JSON to orchestrator
```

---

## Project Structure (Generated)

```
api-tests/
├── pom.xml
├── karate-config.js
├── logback-test.xml
└── src/test/
    ├── java/runner/
    │   └── KarateRunner.java
    └── resources/
        ├── karate-config.js
        ├── helpers/
        │   ├── auth-helper.feature
        │   ├── cleanup-helper.feature
        │   └── data-factory.js
        └── features/
            ├── search/
            │   └── flight-search.feature
            ├── booking/
            │   └── booking-flow.feature
            ├── payment/
            │   └── payment.feature
            ├── checkin/
            │   └── checkin.feature
            └── manage/
                └── manage-booking.feature
```

The feature file categories are driven by the scenarios found. Do not create empty feature directories.

---

## Scenario Authoring Guidelines

### Background block (every .feature file)
Always declare shared variables in `Background`:
```gherkin
Background:
  * url baseUrl
  * def origin = karate.properties['TEST_ORIGIN_IATA'] || 'XXX'
  * def destination = karate.properties['TEST_DEST_IATA'] || 'YYY'
  * def departureDate = call read('classpath:helpers/data-factory.js') { offset: 7 }
  * header Authorization = 'Bearer ' + authToken
```

### Chained calls (booking flows)
```gherkin
Scenario: Complete booking flow
  # Step 1 — search
  Given path '/v1/flights/search'
  And request { origin: '#(origin)', destination: '#(destination)', date: '#(departureDate)' }
  When method POST
  Then status 200
  And match response.flights == '#[_ > 0]'
  * def selectedFlight = response.flights[0]

  # Step 2 — book using result from step 1
  Given path '/v1/bookings'
  And request { flightId: '#(selectedFlight.id)', fare: '#(selectedFlight.fares[0].code)' }
  When method POST
  Then status 201
  * def bookingRef = response.bookingReference
```

### Schema validation (required on every response)
```gherkin
* def flightSchema =
  """
  {
    id: '#string',
    origin: '#string',
    destination: '#string',
    departureDate: '#string',
    fares: '#[] #present'
  }
  """
And match response.flights[0] == flightSchema
```

---

## Escalation Criteria

Escalate to orchestrator ONLY when:

1. **No API inputs available** — neither normalized test cases with API scenarios nor an OpenAPI spec provided
2. **Auth mechanism unknown** — auth type not inferable from inputs and not specified
3. **Endpoint structure indeterminate** — no spec and no test cases describe URL patterns

For everything else: make a reasonable assumption, document it in `assumptions[]`, proceed.

---

## Success Criteria

Work is complete when:
- [ ] `api-tests/pom.xml` generated and compilable
- [ ] `KarateRunner.java` configured with correct package and parallel thread count
- [ ] `karate-config.js` supports at least two environment profiles
- [ ] `auth-helper.feature` and `cleanup-helper.feature` created
- [ ] At least one `.feature` file per scenario category found in inputs
- [ ] Every `Scenario` has at least one `match` assertion (not just `status 200`)
- [ ] Every `Scenario` has Allure tags
- [ ] No hardcoded credentials, dates, or IATA codes in feature files
- [ ] Output JSON returned to orchestrator

Base directory for this skill: skills/api-karate-testing
