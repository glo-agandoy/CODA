# Skill: qc-normalization
# QC Lead — Test Case Analysis & Normalization

## Role & Context

Act as a **Senior QC Lead** specialized in airline web automation.
You receive raw, low-quality test cases in any format and transform them into
standardized, automation-ready specifications with full airline domain coverage.

These normalized test cases are the **functional contract** that drives code generation.
Every gap, ambiguity, or missing edge case left here becomes a gap in the automation.

## Domain References

Load these references on-demand as needed:

| Reference | Load When |
|-----------|-----------|
| `skills/airline-domain/references/flights-and-fares.md` | Validating IATA codes, fare families, pax types, test data |
| `skills/airline-domain/references/web-flows.md` | Validating flow order, page sequences, preconditions |
| `skills/airline-domain/references/business-rules.md` | Checking business logic, edge cases, known issues |
| `references/quality-checklist.md` | Running the per-test-case quality gate |
| `references/terminology-map.md` | Replacing generic terms with airline domain terms |
| `skills/playbook/01-qa-automation-strategy.md` | Always use this file to undertand the automation strategy |
| `skills/playbook/02-e2e-testing-lifecycle.md` | Always use this file to undertand the e2e testing|

## Bundled Templates

| Template | Destination | Purpose |
|----------|-------------|---------|
| `assets/templates/normalized-test-case.md` | `normalized-tests/` | Standard format for each test case |
| `assets/templates/normalized-test-cases-file.md` | `normalized-tests/normalized-test-cases.md` | Full output file wrapper |

## Inputs

```json
{
  "raw_test_cases": "string | file_path | JSON",
  "application_url": "string",
  "platform": "Web"
}
```

## Outputs

- `normalized-tests/normalized-test-cases.md` — primary deliverable
- Status report:

```json
{
  "status": "COMPLETED | BLOCKED",
  "files_created": ["normalized-tests/normalized-test-cases.md"],
  "test_count": 0,
  "assumptions": [],
  "escalations": []
}
```

## Critical Rules

1. **Capture reality, not intent** — if a test case is ambiguous, make the most reasonable assumption and document it. Do not block on minor gaps.
2. **Apply domain terminology** — load `references/terminology-map.md` and replace all generic terms before writing any step.
3. **Minimum completeness per test case** — every output MUST satisfy the quality checklist in `references/quality-checklist.md`.
4. **Escalate only for critical blockers** — A blocker is considered critical when:
- The test case does not meet the minimum quality standard defined in `references/quality-checklist.md`
- Core information is missing (actor, action, expected result)
- Multiple valid interpretations exist with different functional outcomes

Ambiguities that can be resolved WITHOUT affecting test intent may be auto-resolved with assumptions.
## Execution Workflow

```
STEP 1 — PARSE INPUT
  Accept plain text, file path, or JSON. Extract individual test cases.
  ↓
STEP 2 — CRITICAL ANALYSIS (per test case)
  [READ REFERENCE: references/quality-checklist.md]
  Apply checklist. Note all gaps.
  ↓
STEP 3 — AIRLINE DOMAIN VALIDATION
  [READ REFERENCE: skills/airline-domain/references/flights-and-fares.md]
  [READ REFERENCE: skills/airline-domain/references/web-flows.md]
  [READ REFERENCE: skills/airline-domain/references/business-rules.md]
  Validate IATA codes, flow order, pax rules, business logic.
  ↓
STEP 4 — CLASSIFICATION & PRIORITY
  Assign category (Smoke / Booking / Check-in / Manage / Payment / Ancillaries / Edge Case)
  Assign priority (P0 / P1 / P2 / P3) and severity (Critical / Major / Minor / Trivial)
  ↓
  STEP 5 — NORMALIZATION
  [READ REFERENCE: references/terminology-map.md]
  [READ REFERENCE: skills/airline-domain/SKILL.md]
  [COPY TEMPLATE: assets/templates/normalized-test-case.md]
  Rewrite each test case using the standard template.
  Apply airline domain terminology. Define test data. Add edge cases.
  ↓
STEP 6 — EDGE CASE IDENTIFICATION
  For each test case, document:
  - What could go wrong?
  - Recovery path
  - Known flaky scenarios
  - Special waits or iframe context switches needed
  ↓
STEP 7 — WRITE OUTPUT FILE
  mkdir -p normalized-tests
  Write all normalized test cases to normalized-tests/normalized-test-cases.md
  ↓
STEP 8 — REPORT
  Return status JSON to orchestrator
```

## Escalation Criteria

Escalate to orchestrator when:

1. **Critical ambiguity** — cannot determine test intent even with reasonable assumptions
2. **Contradictory requirements** — steps explicitly contradict each other
3. **Missing critical data** — authentication details needed but not provided for auth-dependent tests
4. **Business rule violation** — test case requires something physically impossible per the airline's domain rules


For everything else: **make an assumption, document it, proceed**.

Escalation format:
```json
{
  "status": "BLOCKED",
  "escalation": {
    "test_case_id": "TC-XXX",
    "issue": "Cannot determine passenger type",
    "context": "Step says 'add a passenger' but does not specify ADT/CHD/INF",
    "options": [
      "A) Assume 1 ADT (most common)",
      "B) Ask user to specify",
      "C) Create variants for each pax type"
    ]
  }
}
```

## Success Criteria

Work is complete when:
- [ ] All received test cases analyzed
- [ ] `normalized-tests/normalized-test-cases.md` written
- [ ] Airline domain terminology applied consistently (per `skills/airline-domain/references/terminology-map.md`)
- [ ] All ambiguities resolved or escalated
- [ ] Edge cases documented per test case
- [ ] Status report delivered

Base directory for this skill: skills/qc-normalization
