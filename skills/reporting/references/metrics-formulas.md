# Reference: metrics-formulas
# Test Metrics Calculation Formulas

## Execution Metrics

```
pass_rate (%) = (passed / total) × 100
fail_rate (%) = (failed / total) × 100
skip_rate (%) = (skipped / total) × 100
```

## Self-Healing Metrics

```
# Tests that were initially failing and got fixed autonomously
self_healing_success_rate (%) = (self_healed / (self_healed + len(unresolved_issues))) × 100

# If no failures: self_healing_success_rate = 100 (or N/A)
# If all failures unresolved: self_healing_success_rate = 0
```

## Coverage Metrics

```
automation_coverage (%) = (test_cases_automated / original_test_cases_received) × 100

# test_cases_automated = number of test methods in all test classes
# original_test_cases_received = number of raw test cases before normalization
```

## Timing Metrics

```
average_test_duration_seconds = total_duration_seconds / total
fastest_test = min(individual_test_durations)   [from execution log if available]
slowest_test = max(individual_test_durations)   [from execution log if available]
```

## Quality Metrics (from QC phase)

```
normalization_rate (%) = (normalized_count / received_count) × 100
assumption_count = len(assumptions_made)
escalation_count = len(escalations)
```

## Selector Metrics (from web-exploration phase)

```
selector_validation_rate (%) = (validated_count / (validated_count + fallback_count + not_found_count)) × 100
fallback_rate (%) = (fallback_count / (validated_count + fallback_count + not_found_count)) × 100
```

## metrics/test-metrics.json Schema

```json
{
  "metadata": {
    "session_id": "string",
    "timestamp": "ISO-8601 datetime",
    "framework_type": "Java | Python | TypeScript",
    "application_url": "string"
  },
  "execution": {
    "total": 0,
    "passed": 0,
    "failed": 0,
    "skipped": 0,
    "self_healed": 0,
    "unresolved": 0,
    "duration_seconds": 0
  },
  "rates": {
    "pass_rate": 0.0,
    "fail_rate": 0.0,
    "self_healing_success_rate": 0.0,
    "automation_coverage": 0.0,
    "selector_validation_rate": 0.0
  },
  "timing": {
    "total_seconds": 0,
    "average_seconds": 0.0,
    "fastest_test": "TestName (Xs)",
    "slowest_test": "TestName (Xs)"
  },
  "quality": {
    "original_test_cases": 0,
    "normalized_test_cases": 0,
    "assumptions_made": 0,
    "escalations": 0
  },
  "selectors": {
    "total_discovered": 0,
    "validated": 0,
    "fallback": 0,
    "not_found": 0
  },
  "generated_artifacts": {
    "page_objects": 0,
    "test_classes": 0,
    "components": 0,
    "config_files": 0
  }
}
```

## Rounding Rules

- All percentages: round to 1 decimal place (e.g., 83.3%)
- All durations: round to 1 decimal place in seconds
- All rates in JSON: store as float 0.0–1.0 (e.g., 0.833 for 83.3%)

## Edge Cases

| Situation | Handling |
|-----------|---------|
| total = 0 | pass_rate = 0, note: "No tests executed" |
| No failures at all | self_healing_success_rate = null (N/A — not applicable) |
| No web-exploration data | selector metrics = null (N/A) |
| No self_corrections | self_healed = 0, self_healing_success_rate = null |
