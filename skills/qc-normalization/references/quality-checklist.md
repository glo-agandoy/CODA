# Reference: quality-checklist
# Per-Test-Case Quality Gate

Apply this checklist to every test case before writing the normalized output.
A test case is only ready for normalization if ALL mandatory items are satisfied or explicitly handled.

## Analysis Checklist

| # | Aspect | Check | Action if Failed |
|---|--------|-------|-----------------|
| 1 | **Completeness** | All steps present? No gaps in sequence? | Add missing steps based on airline booking flow (see `skills/airline-domain/references/web-flows.md`) |
| 2 | **Clarity** | Every step unambiguous? No "do something" steps? | Rewrite with precise action + target element |
| 3 | **Testability** | All expected results measurable? | Define concrete assertion (element visible, URL match, text value) |
| 4 | **Data Validity** | Test data uses valid airline domain values? | Replace with IATA codes, T+N dates, airline pax types, env vars (see `skills/airline-domain/references/flights-and-fares.md`) |
| 5 | **Flow Logic** | Step sequence matches the airline's booking flow order? | Correct the sequence per `skills/airline-domain/references/web-flows.md` |
| 6 | **Preconditions** | All preconditions listed? Browser state clear? | Add browser state, cookie handling, auth state |
| 7 | **Assertions** | At least 1 measurable assertion per step? | Add explicit `Verify [element] shows [value]` |
| 8 | **Edge Cases** | Known failure modes documented? | Add edge cases section per `skills/airline-domain/references/business-rules.md` |
| 9 | **Dependencies** | Depends on other test cases? Data created by prior test? | Document dependency; make test independent if possible |

## Mandatory Output Requirements

Every normalized test case MUST contain:

- [ ] Unique ID in `TC-XXX` format
- [ ] Clear, descriptive title (action + subject)
- [ ] Category label (Smoke / Booking / Check-in / Payment / Manage / Ancillaries / Edge Case)
- [ ] Priority (P0–P3) and Severity (Critical / Major / Minor / Trivial)
- [ ] All preconditions listed (browser state, user state, cookies)
- [ ] Test data table with Vueling-valid values
- [ ] Numbered steps with explicit expected result per step
- [ ] Assertions section with measurable validations
- [ ] Edge cases & considerations section
- [ ] Dependencies section (even if empty)

## Mandatory Prohibitions

Every normalized test case MUST NOT contain:

- [ ] Ambiguous steps ("verify the page looks correct", "do the thing")
- [ ] City names instead of IATA codes ("fly to London" → `LGW` or whichever IATA the airline uses)
- [ ] Hardcoded dates (use T+N)
- [ ] Hardcoded credentials (use `${TEST_USER_EMAIL}`)
- [ ] Steps that skip the airline's mandatory booking flow order
- [ ] Missing validations (steps with no expected result)
- [ ] Credentials in plain text

## Scenario Density Requirements

Each normalized test case should yield at minimum:

| Scenario Type | Required |
|---------------|----------|
| Happy path (complete flow) | 1 |
| Sad path — invalid input | 1 |
| Sad path — missing required fields | 1 |
| Edge case (boundary, null, empty) | 1 |
| **Total minimum** | **4 scenarios** |

If a test case cannot generate at least 4 scenarios, it may be combined with a related test case or flagged for expansion.

## Classification Guide

### Category → Priority Matrix

| Category | Default Priority | Rationale |
|----------|-----------------|-----------|
| Smoke (critical path validation) | P0 | Blocks all other testing |
| Booking (end-to-end booking flow) | P1 | Core revenue flow |
| Payment (payment methods) | P1 | Core revenue flow |
| Check-in (online check-in) | P1 | Key user journey |
| Manage Booking (changes, cancellations) | P2 | Secondary user journey |
| Ancillaries (bags, seats, extras) | P2 | Revenue upsell |
| Edge Cases (errors, boundaries) | P3 | Defensive coverage |

### Severity Guide

| Severity | When to Use |
|----------|-------------|
| Critical | Failure blocks core business flow (booking, payment) |
| Major | Failure significantly impacts user experience |
| Minor | Failure has a workaround or low user impact |
| Trivial | Cosmetic issue or extremely rare scenario |
