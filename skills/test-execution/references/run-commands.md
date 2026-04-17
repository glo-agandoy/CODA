# Reference: run-commands
# Test Execution Commands by Framework & Scope

## Java (Maven + TestNG)

### Full Suite
```bash
mvn clean test 2>&1 | tee execution.log
```

### Specific Test Class
```bash
mvn test -Dtest=BookingTest 2>&1 | tee execution.log
```

### Specific Test Method
```bash
mvn test -Dtest=BookingTest#testSearchOneWayFlight 2>&1 | tee execution.log
```

### By TestNG Group/Tag
```bash
mvn test -Dgroups=smoke 2>&1 | tee execution.log
mvn test -Dgroups="booking,checkin" 2>&1 | tee execution.log
```

### Headless Mode Override
```bash
mvn test -Dheadless=true 2>&1 | tee execution.log
```

### Different Browser
```bash
mvn test -Dbrowser=firefox 2>&1 | tee execution.log
```

### Allure Report Generation (after test run)
```bash
mvn allure:report          # Generates static HTML
mvn allure:serve           # Generates + opens browser
```

---

## Python (Pytest + Allure)

### Full Suite
```bash
pytest tests/ --alluredir=allure-results 2>&1 | tee execution.log
```

### Specific File
```bash
pytest tests/test_booking.py --alluredir=allure-results -v 2>&1 | tee execution.log
```

### Specific Test Method
```bash
pytest tests/test_booking.py::TestBooking::test_search_one_way_flight \
  --alluredir=allure-results -v 2>&1 | tee execution.log
```

### By Marker
```bash
pytest tests/ -m smoke --alluredir=allure-results 2>&1 | tee execution.log
pytest tests/ -m "booking or checkin" --alluredir=allure-results 2>&1 | tee execution.log
```

### With HTML Report
```bash
pytest tests/ --alluredir=allure-results \
  --html=test-report.html --self-contained-html 2>&1 | tee execution.log
```

### Allure Report Generation
```bash
allure serve allure-results       # Live server
allure generate allure-results -o allure-report --clean   # Static HTML
```

---

## TypeScript (Playwright + Allure)

### Full Suite
```bash
npx playwright test 2>&1 | tee execution.log
```

### Specific File
```bash
npx playwright test tests/booking.spec.ts 2>&1 | tee execution.log
```

### By Test Name Pattern
```bash
npx playwright test --grep "one-way flight" 2>&1 | tee execution.log
```

### Headed (with visible browser)
```bash
npx playwright test --headed 2>&1 | tee execution.log
```

### Debug Mode (step-by-step)
```bash
npx playwright test --debug 2>&1 | tee execution.log
```

### Allure Report Generation
```bash
allure generate allure-results -o allure-report --clean
allure open allure-report
```

---

## Pre-Execution Compilation Checks

### Java
```bash
mvn compile -q 2>&1
echo "Exit code: $?"
# Exit 0 = OK, non-zero = compilation error
```

### Python
```bash
python -m py_compile tests/*.py 2>&1
pytest --collect-only -q 2>&1
```

### TypeScript
```bash
npx tsc --noEmit 2>&1
echo "Exit code: $?"
```

---

## Dependency Checks

### Java
```bash
mvn dependency:resolve -q 2>&1
echo "Exit code: $?"
```

### Python
```bash
pip check 2>&1
```

### Node
```bash
npm ls --depth=0 2>&1
```

---

## Output Files

| Framework | Results Location | Log |
|-----------|-----------------|-----|
| Java | `target/surefire-reports/*.xml` + `allure-results/` | `execution.log` |
| Python | `allure-results/` + pytest terminal output | `execution.log` |
| TypeScript | `allure-results/` + `test-results/` | `execution.log` |
