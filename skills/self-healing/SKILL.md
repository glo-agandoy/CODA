# Skill: self-healing
# Self-Healing — Failure Diagnosis & Autonomous Code Repair

## Role & Context

Act as an **Expert Test Automation Debugger** that autonomously diagnoses failing tests,
applies targeted code repairs, and re-executes to verify the fix.

**This skill owns the repair loop.** It reads execution results from `test-execution`,
modifies source files, and re-executes — up to a maximum of 2 retry attempts per test.

**Every selector fix must be validated via `web-exploration` (revalidation mode) first.**
Never guess a new selector.

## Domain References

| Reference | Load When |
|-----------|-----------|
| `references/failure-patterns.md` | Classifying failures and choosing the right repair strategy |
| `skills/web-exploration/SKILL.md` | Re-invoking web exploration in revalidation mode for selector fixes |
| `skills/airline-domain/references/ui-patterns.md` | Understanding airline-specific UI behavior causing the failure |

## Inputs

```json
{
  "failed_tests": [
    {
      "name": "ClassName.methodName",
      "error_type": "NoSuchElementException | TimeoutException | ...",
      "error_message": "string",
      "stack_trace": "full stack trace",
      "screenshot": "path | null"
    }
  ],
  "framework_type": "Java | Python | TypeScript",
  "run_command": "mvn test | pytest | npm test",
  "max_retries": 2
}
```

## Outputs

```json
{
  "status": "COMPLETED",
  "self_corrections": [
    {
      "test": "ClassName.methodName",
      "original_error": "string",
      "diagnosis": "string",
      "fix_applied": {
        "file": "path/to/modified/file",
        "line": 45,
        "before": "old code snippet",
        "after": "new code snippet"
      },
      "retry_attempt": 1,
      "retry_result": "PASSED | FAILED"
    }
  ],
  "unresolved_issues": [
    {
      "test": "ClassName.methodName",
      "error_type": "string",
      "attempts": 2,
      "last_error": "string",
      "recommendation": "string",
      "needs_human": true
    }
  ],
  "files_modified": ["src/main/java/pages/HomePage.java"]
}
```

## Critical Rules

1. **Max 2 retries per test** — hard cap, no exceptions
2. **Only fix selectors and waits** — never modify test logic or business assertions
3. **Always validate new selector via `web-exploration` before applying** — never guess
4. **Document every change** — file path, line number, before, after
5. **Not-healable failures go straight to `unresolved_issues`** — no retry
6. **Re-run only the repaired test** — not the full suite

## Execution Workflow

```
FOR EACH failed_test:
  ↓
  STEP 1 — CLASSIFY FAILURE
  [READ REFERENCE: references/failure-patterns.md]
  Determine: self-healable | maybe-healable | not-healable
  ↓
  If NOT HEALABLE → add to unresolved_issues immediately, skip to next test
  ↓
  STEP 2 — DIAGNOSE ROOT CAUSE
  Analyze stack trace → identify file + line + element
  If error_type == NoSuchElementException:
    [INVOKE SKILL: web-exploration mode:revalidation]
    Get replacement selector
  ↓
  STEP 3 — APPLY FIX
  Modify the source file at identified location
  Record: file, line, before, after
  ↓
  STEP 4 — RE-EXECUTE (targeted)
  Run only this specific test
  ↓
  If PASSED → record as self_corrections, move to next test
  If FAILED (retry_count < max_retries) → go to STEP 1 with new error
  If FAILED (retry_count >= max_retries) → add to unresolved_issues
```

## Repair Rules

### ONLY modify:
- CSS selectors in `@FindBy` annotations / locator constants
- Timeout duration values in `WebDriverWait` instantiation
- Wait conditions (`visibilityOf` → `presenceOf`, etc.)
- Re-location of stale elements (add fresh `driver.findElement()` call)
- iframe context switch (add `driver.switchTo().frame()`)
- Overlay handling (add `waitForSpinnerDisappear()` or `closePromoPopup()`)

### NEVER modify:
- Test method logic or flow
- `Assert` / `assert` statements
- Test data values
- Business flow order
- Allure annotations

## Targeted Re-Execution Commands

### Java — single test method
```bash
mvn test -Dtest=ClassName#methodName 2>&1 | tee retry-execution.log
```

### Python — single test method
```bash
pytest tests/test_file.py::ClassName::method_name -v 2>&1 | tee retry-execution.log
```

### TypeScript — single test by name
```bash
npx playwright test --grep "test name pattern" 2>&1 | tee retry-execution.log
```

Parse result from `retry-execution.log` to determine PASSED or FAILED.

## Success Criteria

Work is complete when:
- [ ] All failed tests processed (either fixed or moved to unresolved)
- [ ] Every fix documented with file, line, before, after
- [ ] Max 2 retries respected per test
- [ ] All unresolved issues have actionable recommendations
- [ ] `files_modified` list populated
- [ ] Output JSON delivered to orchestrator

Base directory for this skill: skills/self-healing
