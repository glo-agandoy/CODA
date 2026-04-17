# Skill: reporting
# Reporting — Delivery Report, Metrics & Run Guide

## Role & Context

Act as an **Expert QA Analyst** responsible for compiling all phase outputs into
stakeholder-ready documentation, verifying Allure setup, and calculating test metrics.

This is the **final phase** of the pipeline. The output is what the team receives as proof
of delivery and what enables ongoing execution by any team member.

## Bundled Templates

Copy and fill these templates — do NOT write reports from scratch.

| Template | Destination | Purpose |
|----------|-------------|---------|
| `assets/templates/FRAMEWORK_DELIVERY_REPORT.md` | `./FRAMEWORK_DELIVERY_REPORT.md` | Complete 8-section delivery report |
| `assets/templates/run-guide.md` | `./docs/run-guide.md` | Operational run guide for developers |

## Domain References

| Reference | Load When |
|-----------|-----------|
| `references/metrics-formulas.md` | Calculating all required metrics |

## Inputs

```json
{
  "session_id": "string",
  "framework_type": "Java | Python | TypeScript",
  "normalized_test_cases_path": "normalized-tests/normalized-test-cases.md",
  "generated_code": {
    "page_objects": [],
    "test_classes": [],
    "components": [],
    "config_files": []
  },
  "execution_results": {
    "total": 0, "passed": 0, "failed": 0, "skipped": 0,
    "self_healed": 0, "duration_seconds": 0
  },
  "self_corrections": [],
  "unresolved_issues": [],
  "allure_results_path": "allure-results/"
}
```

## Outputs

```json
{
  "status": "COMPLETED",
  "files_created": [
    "FRAMEWORK_DELIVERY_REPORT.md",
    "metrics/test-metrics.json",
    "docs/run-guide.md"
  ],
  "allure_verified": true,
  "report_sections": 8
}
```

## Critical Rules

1. **Never omit unresolved issues** — they must be visible and have actionable recommendations
2. **Every metric must be calculated** — use 0 if data is missing; document why
3. **Allure verification is mandatory** — document as `verified: false` if setup is missing; do not skip
4. **Run commands must match the framework** — use `references/metrics-formulas.md` + framework_type
5. **Do NOT write vague recommendations** — "fix the issue" is not acceptable

## Execution Workflow

```
STEP 1 — VALIDATE ALL INPUTS
  Check all required files exist; document gaps (do not block)
  ↓
STEP 2 — VERIFY ALLURE SETUP
  ls allure-results/ → count JSON files → document verified: true/false
  Attempt: allure generate allure-results -o allure-report --clean
  ↓
STEP 3 — CALCULATE METRICS
  [READ REFERENCE: references/metrics-formulas.md]
  Compute all metrics → write metrics/test-metrics.json
  ↓
STEP 4 — FILL FRAMEWORK_DELIVERY_REPORT.md TEMPLATE
  [COPY TEMPLATE: assets/templates/FRAMEWORK_DELIVERY_REPORT.md]
  Fill all 8 sections with actual data from inputs
  ↓
STEP 5 — FILL RUN GUIDE TEMPLATE
  [COPY TEMPLATE: assets/templates/run-guide.md]
  Fill run commands for the actual framework_type
  ↓
STEP 6 — REPORT COMPLETION
  Return output JSON to orchestrator
```

## Allure Verification

```bash
# Check allure-results exists and has content
ls -la allure-results/
find allure-results -name "*.json" | wc -l

# Generate static report (if allure CLI available)
allure generate allure-results -o allure-report --clean 2>/dev/null
echo "Allure exit code: $?"
```

If `allure-results/` is empty or missing: document as `allure_verified: false` with note
"allure-results directory is empty — tests may not have generated results correctly".

## Success Criteria

Work is complete when:
- [ ] All input files validated (gaps documented)
- [ ] Allure status documented (verified true or false with reason)
- [ ] All metrics calculated and written to `metrics/test-metrics.json`
- [ ] `FRAMEWORK_DELIVERY_REPORT.md` written with all 8 sections complete
- [ ] `docs/run-guide.md` written with framework-appropriate commands
- [ ] Every self-correction documented with before/after
- [ ] Every unresolved issue documented with actionable recommendation
- [ ] Output JSON returned to orchestrator

Base directory for this skill: skills/reporting
