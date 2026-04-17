# Framework Delivery Report — {AIRLINE_NAME} UI Automation

**Project:** {AIRLINE_NAME} UI Test Automation
**Date:** {{DATE}}
**Session ID:** {{SESSION_ID}}
**Framework:** {{FRAMEWORK_TYPE}} (Selenium + TestNG + Allure)
**Status:** {{STATUS}}  ← COMPLETED | PARTIAL | FAILED

---

## 1. Executive Summary

### Overview
This report documents the automated test framework delivery for {AIRLINE_NAME} web application.
The project transformed {{ORIGINAL_COUNT}} manual test cases into {{AUTOMATED_COUNT}} automated tests
with full Allure reporting integration and autonomous self-healing capability.

### Key Achievements
- {{AUTOMATED_COUNT}} test cases automated ({{COVERAGE_RATE}}% coverage)
- {{PAGE_OBJECTS_COUNT}} Page Objects with validated selectors ({{SELECTOR_VALIDATION_RATE}}% validated)
- Allure Reports integrated with JIRA and TestLink deep links
- {{PASS_RATE}}% pass rate on initial execution
- {{SELF_HEALED_COUNT}} issues autonomously self-healed

### Quick Stats

| Metric | Value |
|--------|-------|
| Total Test Cases | {{TOTAL_COUNT}} |
| Pass Rate | {{PASS_RATE}}% |
| Self-Healing Success Rate | {{SELF_HEAL_RATE}}% |
| Total Execution Duration | {{DURATION}} |
| Selectors Validated | {{SELECTORS_VALIDATED}} / {{SELECTORS_TOTAL}} |

---

## 2. QC & Normalization Report

### Input Analysis
- **Test Cases Received:** {{ORIGINAL_COUNT}}
- **Successfully Normalized:** {{NORMALIZED_COUNT}}
- **Required Clarification:** {{ESCALATED_COUNT}}
- **Assumptions Made:** {{ASSUMPTIONS_COUNT}}

### Normalization Table

| Original ID | Normalized Title | Category | Priority | Changes Made |
|-------------|-----------------|----------|----------|-------------|
| [original] | [normalized] | Booking | P1 | Added IATA codes, T+N dates, pax types |
<!-- Fill one row per test case -->

### Assumptions Made
<!-- List each assumption with TC ID, original ambiguity, and resolution -->
1. **TC-XXX** — [original ambiguity]: Assumed [decision] because [reasoning]

### QC Recommendations
- [Recommendation 1 — e.g., "Future test cases should specify pax type explicitly"]
- [Recommendation 2]

---

## 3. Technical Implementation

### Framework Architecture

```
project/
├── pom.xml                          Selenium {{SELENIUM_VERSION}}, TestNG {{TESTNG_VERSION}}, Allure {{ALLURE_VERSION}}
├── src/main/java/
│   ├── pages/                       Page Objects ({{PAGE_OBJECTS_COUNT}} files)
│   ├── components/                  Reusable UI components
│   └── utils/                       WaitUtils, ConfigReader
├── src/test/java/tests/             Test Classes ({{TEST_CLASSES_COUNT}} files)
├── src/test/resources/
│   ├── config.properties            Externalized config
│   └── allure.properties            Allure + JIRA/TestLink links
└── allure-results/                  Test results ({{ALLURE_RESULTS_COUNT}} files)
```

### Page Objects Created

| Page Object | Elements | Methods | Selectors Validated |
|-------------|----------|---------|---------------------|
<!-- Fill one row per page object -->
| BasePage.java | — | 6 shared | N/A (abstract) |
| HomePage.java | {{N}} | {{N}} | {{X}}/{{X}} |

### Selector Discovery Summary

| Status | Count | Percentage |
|--------|-------|------------|
| Validated (Levels 1–4) | {{VALIDATED}} | {{VALIDATED_PCT}}% |
| Fallback (Levels 5–6) | {{FALLBACK}} | {{FALLBACK_PCT}}% |
| Not Found | {{NOT_FOUND}} | {{NOT_FOUND_PCT}}% |

### Components Generated

| Component | Purpose | Used By |
|-----------|---------|---------|
| CalendarComponent | T+N date selection on calendar widget | {{N}} page objects |
| StationSelector | Airport IATA autocomplete | {{N}} page objects |
| WaitUtils | Centralized explicit waits + spinner | All test classes |
| ConfigReader | Singleton config loader | All page objects + base test |

---

## 4. Execution Results

### Summary

| Status | Count | Percentage |
|--------|-------|------------|
| Passed | {{PASSED}} | {{PASS_RATE}}% |
| Failed | {{FAILED}} | {{FAIL_RATE}}% |
| Skipped | {{SKIPPED}} | {{SKIP_RATE}}% |
| Self-Healed | {{SELF_HEALED}} | {{SELF_HEAL_PCT}}% |
| **Total** | **{{TOTAL}}** | |

**Total Execution Duration:** {{DURATION}}
**Average Test Duration:** {{AVG_DURATION}}

### Self-Correction Log
<!-- Fill one block per self_correction received from self-healing skill -->

#### Fix #{{N}}: {{TEST_NAME}}

| Field | Value |
|-------|-------|
| Issue | {{ORIGINAL_ERROR}} |
| Root Cause | {{DIAGNOSIS}} |
| Fix Applied | {{FIX_DESCRIPTION}} |
| File Modified | {{FILE}}:{{LINE}} |
| Before | `{{BEFORE}}` |
| After | `{{AFTER}}` |
| Retry Attempt | {{ATTEMPT}} of 2 |
| Result | {{RETRY_RESULT}} |

### Unresolved Issues
<!-- Fill if any. If none, write "No unresolved issues — all tests passed or self-healed." -->

| Test | Error Type | Attempts | Recommendation |
|------|------------|----------|----------------|
| {{TEST}} | {{ERROR_TYPE}} | {{ATTEMPTS}} | {{RECOMMENDATION}} |

---

## 5. Allure Reports

### Configuration
```properties
allure.results.directory=allure-results
allure.link.issue.pattern={JIRA_URL}/browse/{}
allure.link.tms.pattern={TMS_URL}/case/{}
```

### Status
- **Results directory:** `allure-results/` — {{ALLURE_STATUS}} ({{ALLURE_FILES_COUNT}} result files)
- **Allure verified:** {{ALLURE_VERIFIED}}

### Viewing Reports
```bash
# Option 1: Live interactive server (opens browser automatically)
allure serve allure-results

# Option 2: Static HTML report
allure generate allure-results -o allure-report --clean
open allure-report/index.html      # macOS
start allure-report/index.html     # Windows
```

### Report Features
- Test steps with Allure `@Step` annotations (visible in report)
- Screenshots captured on test completion and failure
- Severity categorization (Blocker / Critical / Normal / Minor)
- JIRA issue deep links via `@Issue`
- TestLink case deep links via `@TmsLink`

---

## 6. Deliverables Checklist

### Configuration Files
- [x] `pom.xml` — Maven build with pinned dependencies
- [x] `src/test/resources/allure.properties` — Allure + JIRA/TestLink configuration
- [x] `src/test/resources/config.properties` — Externalized test config

### Page Objects
<!-- Check each file -->
- [x] `BasePage.java`
- [x] `{{EACH_PAGE_OBJECT}}.java`

### Components & Utilities
- [x] `CalendarComponent.java`
- [x] `StationSelector.java`
- [x] `WaitUtils.java`
- [x] `ConfigReader.java`

### Test Classes
<!-- Check each file -->
- [x] `BaseTest.java`
- [x] `{{EACH_TEST_CLASS}}.java`

### Documentation
- [x] `README.md` — Setup, run commands, selector reference
- [x] `FRAMEWORK_DELIVERY_REPORT.md` (this document)
- [x] `normalized-tests/normalized-test-cases.md`
- [x] `docs/run-guide.md`

---

## 7. Next Steps

### Immediate (Post-Delivery)
1. **Review normalized test cases** — `normalized-tests/normalized-test-cases.md`
2. **Run the full suite** — `{{RUN_COMMAND}}`
3. **View Allure report** — `allure serve allure-results`
4. **Address unresolved issues** — see Section 4 above

### Recommended Enhancements
1. Configure CI/CD pipeline — see `docs/run-guide.md` for Jenkins/GitHub Actions examples
2. Add test data management layer for booking tests (API-based setup/teardown)
3. Expand edge case coverage for payment flows (3DS, card decline scenarios)
4. Add retry logic for known flaky selectors (`@flaky` tagged tests)

### Maintenance Guide
- **Selector updates:** When a UI change causes `NoSuchElementException`, re-run `web-exploration` in revalidation mode to find the new selector
- **New test cases:** Follow the existing pattern in `BaseTest` + corresponding `{Category}Test` class
- **Environment config:** Update `src/test/resources/config.properties` for staging/production URLs

---

## 8. Appendix

### A. Test Data Reference
| Data Type | Standard | Example |
|-----------|----------|---------|
| IATA Origin/Destination | 3-letter airport code | From `skills/airline-domain/references/flights-and-fares.md` |
| Credentials | Environment variables | `${TEST_USER_EMAIL}` |
| Flight dates | T+N days from today | `T+7`, `T+14` |
| PNR | Airline-specific format | From `skills/airline-domain/references/flights-and-fares.md` |
| Passengers | Airline pax types | From `skills/airline-domain/references/flights-and-fares.md` |

### B. Dependency Versions
| Library | Version |
|---------|---------|
| Selenium Java | 4.18.0 |
| TestNG | 7.9.0 |
| Allure TestNG | 2.25.0 |
| WebDriverManager | 5.7.0 |
| AspectJ Weaver | 1.9.21 |

### C. Selector Strategy Applied
| Priority | Type | Count Used |
|----------|------|------------|
| 1 — data-testid | `[data-testid='X']` | {{COUNT_1}} |
| 2 — data-qa | `[data-qa='X']` | {{COUNT_2}} |
| 3 — static id | `#X` | {{COUNT_3}} |
| 4 — name | `[name='X']` | {{COUNT_4}} |
| 5–6 — CSS/XPath | fallback | {{COUNT_56}} |

---

*Generated by Airline UI Test Automation — Reporting Skill*
*Session: {{SESSION_ID}}*
