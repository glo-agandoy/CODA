# Test Execution Guide — {AIRLINE_NAME} UI Automation

**Framework:** {{FRAMEWORK_TYPE}}
**Last Updated:** {{DATE}}

---

## Prerequisites

### Required Software

| Tool | Version | Install |
|------|---------|---------|
| Java | 17+ | `brew install openjdk@17` / [adoptium.net](https://adoptium.net) |
| Maven | 3.8+ | `brew install maven` |
| Chrome | Latest | [google.com/chrome](https://www.google.com/chrome) |
| Allure CLI | 2.x | `npm install -g allure-commandline` |

> For Python: Python 3.11+, pip, pytest
> For TypeScript: Node 20+, npm

### Environment Setup

```bash
# 1. Clone repository
git clone <repo-url>
cd {airline-id}-ui-automation

# 2. Set required environment variables
export BASE_URL="{BASE_URL}"                    # Target airline's URL
export TEST_USER_EMAIL="${TEST_USER_EMAIL}"      # Test account email
export TEST_USER_PASSWORD="${TEST_USER_PASSWORD}" # Test account password

# 3. Verify Chrome is installed
google-chrome --version   # or 'chromium --version' on some systems
```

### Configuration

Edit `src/test/resources/config.properties` to override defaults:

```properties
base.url=${BASE_URL}              # Set via env variable, or hardcode for local runs
browser=chrome                    # or 'firefox'
headless=false                    # set 'true' for CI
timeout.explicit=15               # increase for slow environments
```

---

## Running Tests

### Full Test Suite
```bash
mvn clean test
```

### Specific Test Class
```bash
mvn test -Dtest=BookingTest
mvn test -Dtest=CheckInTest
mvn test -Dtest=PaymentTest
```

### Specific Test Method
```bash
mvn test -Dtest=BookingTest#testSearchOneWayFlight
mvn test -Dtest=BookingTest#testSearchRoundTrip
```

### By Priority Tag (TestNG Groups)
```bash
# Smoke only — fastest subset
mvn test -Dgroups=smoke

# P1 critical tests
mvn test -Dgroups="booking,checkin,payment"

# All edge cases
mvn test -Dgroups=edge_case
```

### Headless Mode (for CI)
```bash
mvn test -Dheadless=true
```

### Different Browser
```bash
mvn test -Dbrowser=firefox
```

---

## Viewing Test Results

### Allure Interactive Report (Recommended)
```bash
# Generates report AND opens browser automatically
allure serve allure-results
```

### Allure Static HTML Report
```bash
# Generate
allure generate allure-results -o allure-report --clean

# Open
open allure-report/index.html      # macOS
start allure-report/index.html     # Windows
xdg-open allure-report/index.html  # Linux
```

### Console Summary
```bash
cat target/surefire-reports/*.txt | grep -E "Tests run|FAILED|ERROR"
```

---

## Troubleshooting

### ChromeDriver Version Mismatch
**Symptom:** `SessionNotCreatedException: Chrome version must be between X and Y`
**Solution:** WebDriverManager handles this automatically. If issues persist:
```bash
# Force update Chrome to latest
# macOS: brew upgrade --cask google-chrome
# Then re-run tests
mvn clean test
```

### Element Not Found
**Symptom:** `NoSuchElementException: Unable to locate element: [data-testid='X']`
**Solution:**
1. Check if the airline updated their UI (run a quick manual check of the failing page)
2. Use browser DevTools (F12 → Inspector) to find the new selector
3. Update the `@FindBy` in the corresponding Page Object
4. Or: re-run `web-exploration` skill to auto-discover new selectors

### Timeout Errors
**Symptom:** `TimeoutException: Expected condition failed: waiting for visibility`
**Solution:**
1. Increase `timeout.explicit` in `config.properties` (try 30)
2. Check network latency: `ping {AIRLINE_DOMAIN}`
3. Verify the element selector is correct
4. Add `waitForSpinnerDisappear()` before the failing action if spinner is involved

### Allure Report Not Generating
**Symptom:** `allure-results/` is empty after test run
**Solution:**
```bash
# Verify allure-testng dependency
grep "allure-testng" pom.xml

# Verify maven-surefire-plugin has aspectjweaver arg
grep "aspectjweaver" pom.xml

# Manually check results dir
ls -la allure-results/
```

### Cookie Consent Modal Blocking Tests
**Symptom:** Tests fail early with `ElementClickInterceptedException` or `NoSuchElementException`
**Solution:** Cookie handling is in `BasePage.handleCookieConsent()`. If modal selector changed:
```
Check current selector in skills/airline-domain/references/ui-patterns.md (Section 2: Cookie Consent Modal)
Update COOKIE_ACCEPT constant in BasePage.java
```

### Payment iFrame Failures
**Symptom:** Test fails in payment step with `NoSuchElementException` for card fields
**Solution:**
```
Payment page uses an iframe for PCI compliance — verify context switch.
Check PaymentPage.java: enterPaymentIframe() must be called before card field interaction.
Verify iframe selector from skills/airline-domain/references/ui-patterns.md (Section 4: Payment iFrame).
```

---

## CI/CD Integration

### Jenkins Pipeline
```groovy
pipeline {
    agent any
    environment {
        BASE_URL           = '{BASE_URL}'
        TEST_USER_EMAIL    = credentials('{airline-id}-test-email')
        TEST_USER_PASSWORD = credentials('{airline-id}-test-password')
    }
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test -Dheadless=true'
            }
            post {
                always {
                    sh 'mvn allure:report'
                    allure([
                        includeProperties: false,
                        jdk: '',
                        results: [[path: 'allure-results']]
                    ])
                }
            }
        }
    }
    post {
        failure {
            emailext(
                subject: '{AIRLINE_NAME} UI Tests FAILED',
                body: '${BUILD_URL}',
                to: '{QA_TEAM_EMAIL}'
            )
        }
    }
}
```

### GitHub Actions
```yaml
name: {AIRLINE_NAME} UI Tests

on:
  schedule:
    - cron: '0 8 * * 1-5'   # Monday–Friday at 8am
  push:
    branches: [main, develop]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Run Tests
        env:
          BASE_URL: ${{ secrets.BASE_URL }}
          TEST_USER_EMAIL: ${{ secrets.TEST_USER_EMAIL }}
          TEST_USER_PASSWORD: ${{ secrets.TEST_USER_PASSWORD }}
        run: mvn clean test -Dheadless=true

      - name: Generate Allure Report
        if: always()
        uses: simple-elf/allure-report-action@master
        with:
          allure_results: allure-results

      - name: Publish Allure Report
        if: always()
        uses: peaceiris/actions-gh-pages@v3
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: allure-report
```

---

## File Reference

| File | Purpose |
|------|---------|
| `pom.xml` | Build + dependency management |
| `src/test/resources/config.properties` | All configuration (URL, browser, timeouts) |
| `src/test/resources/allure.properties` | Allure + JIRA/TestLink deep links |
| `allure-results/` | Raw test results (JSON format) |
| `allure-report/` | Generated HTML report (after `allure:report`) |
| `execution.log` | Full console output from last test run |
| `FRAMEWORK_DELIVERY_REPORT.md` | Delivery documentation |
| `normalized-tests/` | QC-normalized test case specifications |
