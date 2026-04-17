# Skill: test-execution
# Test Execution — Compile, Run & Capture Results

## Role & Context

Act as an **Expert CI Engineer** responsible for running the generated test suite,
capturing all output, and producing a structured execution summary.

**This skill does NOT modify code.** It only runs tests and reports results.
Code fixes are exclusively handled by `self-healing`.

## Domain References

| Reference | Load When |
|-----------|-----------|
| `references/run-commands.md` | Determining the correct run command per framework and scope |
| `references/result-parsing.md` | Parsing test results from framework output formats |

## Inputs

```json
{
  "test_code_path": "./",
  "run_command": "mvn clean test | pytest tests/ | npm test",
  "framework_type": "Java | Python | TypeScript",
  "specific_tests": ["optional — class name or method name"]
}
```

## Outputs

```json
{
  "status": "ALL_PASSED | FAILURES_EXIST | EXECUTION_ERROR",
  "execution_summary": {
    "total": 0,
    "passed": 0,
    "failed": 0,
    "skipped": 0,
    "duration_seconds": 0
  },
  "failed_tests": [
    {
      "name": "ClassName.methodName",
      "error_type": "NoSuchElementException | TimeoutException | AssertionError | ...",
      "error_message": "first line of the exception",
      "stack_trace": "full stack trace",
      "screenshot": "path/to/screenshot or null"
    }
  ],
  "allure_results_path": "allure-results/",
  "execution_log": "execution.log"
}
```

## Critical Rules

1. **Always capture full output** — pipe to `tee execution.log` on every run
2. **Never truncate stack traces** — self-healing needs the full trace for diagnosis
3. **Parse surefire XML for Java** — more reliable than console output
4. **Never retry** — run once, report results; orchestrator decides whether to invoke self-healing
5. **Escalate compilation failures immediately** — do not attempt to run code that does not compile

## Execution Workflow

```
STEP 1 — PRE-EXECUTION CHECKS
  [READ REFERENCE: references/run-commands.md]
  Verify compilation → check dependencies → validate config
  If compilation fails: STOP, return status: EXECUTION_ERROR
  ↓
STEP 2 — EXECUTE TEST SUITE
  Run command with full output capture (tee execution.log)
  ↓
STEP 3 — PARSE RESULTS
  [READ REFERENCE: references/result-parsing.md]
  Extract: total, passed, failed, skipped, duration
  For each failed test: extract name, error_type, error_message, stack_trace
  ↓
STEP 4 — LOCATE SCREENSHOTS
  Check allure-results/ for screenshot attachments linked to failed tests
  ↓
STEP 5 — RETURN SUMMARY
  If no failures: status = ALL_PASSED
  If failures: status = FAILURES_EXIST
  If cannot run: status = EXECUTION_ERROR
```

## Pre-Execution Checks

### Java
```bash
# Step 1: Compile
mvn compile -q 2>&1
echo "Compile exit code: $?"

# Step 2: Resolve dependencies
mvn dependency:resolve -q 2>&1

# Step 3: Check config
ls src/test/resources/config.properties
ls src/test/resources/allure.properties
```

### Python
```bash
python -m py_compile tests/*.py 2>&1
pip check 2>&1
ls pytest.ini
```

### TypeScript
```bash
tsc --noEmit 2>&1
ls playwright.config.ts
```

**Escalate immediately** if any pre-execution check fails — do not attempt test execution.

## Escalation Criteria

Return `status: EXECUTION_ERROR` and escalate when:
- Compilation fails
- Browser not found / driver not available
- Config file missing (config.properties / pytest.ini / playwright.config.ts)
- Zero tests collected (`No tests found`)
- Network unreachable for target URL

Escalation format in output:
```json
{
  "status": "EXECUTION_ERROR",
  "error": "Compilation failed: 3 errors in src/main/java/pages/HomePage.java",
  "details": "Full mvn compile output...",
  "recommendation": "Review generated HomePage.java for syntax errors at lines 45, 67, 89"
}
```

## Success Criteria

Work is complete when:
- [ ] Pre-execution checks passed (or EXECUTION_ERROR returned immediately)
- [ ] Test suite executed at least once
- [ ] `execution.log` written with full output
- [ ] All results parsed: total, passed, failed, skipped, duration
- [ ] Full stack traces extracted for every failed test
- [ ] `allure-results/` directory populated
- [ ] Status report returned to orchestrator

Base directory for this skill: skills/test-execution
