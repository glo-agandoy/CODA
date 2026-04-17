# Reference: result-parsing
# Test Result Parsing — Per Framework Format

## Java (Maven Surefire XML)

### Location
```
target/surefire-reports/
  TEST-tests.BookingTest.xml
  TEST-tests.CheckInTest.xml
  ...
```

### Parsing Counts from XML
```bash
# Total, failures, errors, skipped from surefire XML
grep -h "testsuite" target/surefire-reports/TEST-*.xml | \
  grep -oP '(tests|failures|errors|skipped)="\K[0-9]+'

# Specific: tests run
grep -oP 'tests="\K[0-9]+' target/surefire-reports/TEST-*.xml | \
  awk '{s+=$1} END {print s}'

# Failed test names
grep -l "failure\|error" target/surefire-reports/TEST-*.xml | \
  xargs grep -h "testcase name=" | grep -oP 'name="\K[^"]*'
```

### XML Structure
```xml
<testsuite name="tests.BookingTest" tests="4" failures="1" errors="0" skipped="0" time="45.3">
  <testcase name="testSearchOneWayFlight" classname="tests.BookingTest" time="8.2"/>
  <testcase name="testSearchRoundTrip" classname="tests.BookingTest" time="12.1">
    <failure message="NoSuchElementException" type="org.openqa.selenium.NoSuchElementException">
      org.openqa.selenium.NoSuchElementException: no such element: Unable to locate element...
        at org.openqa.selenium.remote.RemoteWebDriver...
        at pages.HomePage.clickSearch(HomePage.java:52)
        at tests.BookingTest.testSearchRoundTrip(BookingTest.java:87)
    </failure>
  </testcase>
</testsuite>
```

### Extracting Failed Test Details
```bash
# Get all failure messages
grep -A5 "<failure" target/surefire-reports/TEST-*.xml

# Get specific test failure for diagnosis
grep -B2 -A20 "testSearchRoundTrip" target/surefire-reports/TEST-tests.BookingTest.xml
```

---

## Python (Pytest Output)

### Console Output Patterns
```
PASSED tests/test_booking.py::TestBooking::test_search_one_way       [25%]
FAILED tests/test_booking.py::TestBooking::test_search_round_trip     [50%]
ERROR tests/test_checkin.py::TestCheckIn::test_retrieve_booking       [75%]
```

### Summary Line
```
= 2 passed, 1 failed, 1 error in 45.3s =
```

### Extracting Failed Tests from execution.log
```bash
# Failed test names
grep "^FAILED" execution.log | awk '{print $2}'

# Short traceback
grep -A10 "FAILED\|AssertionError\|selenium" execution.log

# Full failure details
sed -n '/FAILURES/,/passed/p' execution.log
```

### Pytest XML Output (optional)
```bash
# Generate XML report
pytest tests/ --junit-xml=test-results.xml

# Parse XML
grep "testcase.*failure" test-results.xml
```

---

## TypeScript (Playwright)

### Console Output Patterns
```
  ✓  tests/booking.spec.ts:15:5 › search one-way flight (8.2s)
  ✗  tests/booking.spec.ts:45:5 › search round-trip (12.1s)
    Error: locator.click: Timeout 15000ms exceeded.
      at HomPage.clickSearch (src/pages/HomePage.ts:52)
      at test (tests/booking.spec.ts:55)
```

### Summary Line
```
2 passed (45.3s)
1 failed (12.1s)
```

### JSON Results (via reporter)
```bash
# test-results/ directory contains per-test JSON
ls test-results/
cat test-results/*.json | python3 -c "import sys,json; [print(r['title'], r['status']) for r in json.load(sys.stdin)['suites'][0]['specs']]"
```

---

## Unified Extraction Pattern

After running any framework, extract this structure:

```json
{
  "total": 4,
  "passed": 3,
  "failed": 1,
  "skipped": 0,
  "duration_seconds": 45.3,
  "failed_tests": [
    {
      "name": "BookingTest.testSearchRoundTrip",
      "error_type": "NoSuchElementException",
      "error_message": "Unable to locate element: {method:css selector, selector:[data-testid='search-return']}",
      "stack_trace": "org.openqa.selenium.NoSuchElementException...\n  at pages.HomePage.clickSearch(HomePage.java:52)\n  at tests.BookingTest.testSearchRoundTrip(BookingTest.java:87)",
      "screenshot": "allure-results/abc123-attachment.png"
    }
  ]
}
```

## Error Type Classification from Stack Traces

| String in Stack Trace | error_type Classification |
|-----------------------|--------------------------|
| `NoSuchElementException` | `NoSuchElementException` |
| `TimeoutException` | `TimeoutException` |
| `StaleElementReferenceException` | `StaleElementReferenceException` |
| `ElementClickInterceptedException` | `ElementClickInterceptedException` |
| `AssertionError` / `AssertionFailedError` | `AssertionError` |
| `WebDriverException` + `not reachable` | `EnvironmentError` |
| `switchTo().frame()` failure | `iFrameError` |
| `InvalidSelectorException` | `InvalidSelectorError` |
| All others | `UnknownError` |

## Screenshot Linkage

Allure stores screenshots as attachments in `allure-results/`.
Link screenshots to failed tests:
```bash
# Find screenshots created in the last test run window
find allure-results/ -name "*.png" -newer execution.log | head -10

# Match by test name in allure JSON result files
grep -l "testSearchRoundTrip" allure-results/*.json | \
  xargs grep -oP '"source":"\K[^"]+\.png'
```
