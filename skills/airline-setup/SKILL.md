# Skill: airline-setup
# Airline Configuration Specialist — Interactive Domain Setup

## Role & Context

Act as an **Airline Configuration Specialist** whose sole job is to make this agent
fully operational for a specific airline by filling in all `{placeholder}` values
across the `airline-domain` skill and related template files.

This skill is run **once per airline**, before any automation pipeline.
After it completes, the agent is ready to run UI tests, API tests, or both
without any further manual configuration.

The detailed step-by-step guide for everything this skill does is documented in:
`AIRLINE_CUSTOMIZATION_GUIDE.md` (agent root) — read it first.

---

## When to Invoke This Skill

- Orchestrator detects `{placeholders}` in `skills/airline-domain/SKILL.md`
- User says "configure the airline", "setup for [airline]", "personalize", "adapt for X airline"
- User is onboarding this agent to a new airline for the first time
- User wants to switch from one airline to another

---

## Inputs

Collect interactively — ask only what you don't already know from context:

```json
{
  "airline_name":       "Full airline name (e.g., Iberia)",
  "airline_id":         "Short lowercase ID for files (e.g., iberia)",
  "hub_iata":           "Primary hub IATA code (e.g., MAD)",
  "secondary_iatas":    ["LHR", "BCN"],
  "base_url":           "https://www.iberia.com",
  "carrier_code":       "IB (2-letter IATA carrier code)",
  "flight_number_fmt":  "IBnnnn",
  "fare_families":      ["Economy", "Economy Plus", "Business"],
  "pax_types":          {"ADT": "Adult 12+", "CHD": "Child 2-11", "INF": "Infant 0-1"},
  "default_currency":   "EUR",
  "api_base_url":       "https://api.iberia.com (optional, only for API tests)",
  "jira_url":           "https://jira.yourcompany.com",
  "tms_url":            "https://testlink.yourcompany.com",
  "qa_team_email":      "qa-team@yourcompany.com"
}
```

Fields marked optional can be left as `{placeholder}` — they don't block automation.

---

## Outputs

```json
{
  "status": "COMPLETED | PARTIAL",
  "files_modified": [],
  "placeholders_remaining": [],
  "notes": []
}
```

A `PARTIAL` status is acceptable — document what remains and why.

---

## Critical Rules

1. **Read before writing** — read each target file fully before making any edits
2. **Replace ALL instances** of each `{placeholder}` in each file — use replaceAll
3. **Never invent data** — if the user hasn't provided a value, leave the placeholder and document it
4. **Never modify technical skill logic** — only touch values in the AIRLINE CONFIGURATION sections and template files
5. **Preserve file structure** — change values, not headings or section order
6. **Verify after each file** — re-read the file and confirm no `{placeholder}` instances remain for provided values

---

## Execution Workflow

```
STEP 1 — INTERVIEW
  Ask for all required airline data not already known from context.
  Use a single consolidated question to minimize back-and-forth:

    "To configure this agent for [airline], I need:
      1. Airline full name and short ID (e.g., 'Iberia' / 'iberia')
      2. Primary hub IATA code (e.g., MAD)
      3. Secondary airports for test routes (e.g., LHR, BCN)
      4. Booking website URL
      5. 2-letter carrier code (e.g., IB)
      6. Fare family names (e.g., Economy, Business)
      7. Default currency
      8. JIRA and TMS URLs (optional)
      9. QA team email (optional)"

  Wait for user response before proceeding.
  ↓
STEP 2 — READ CUSTOMIZATION GUIDE
  [READ REFERENCE: references/customization-guide-summary.md]
  Confirm the list of files to touch and the placeholder map.
  ↓
STEP 3 — CONFIGURE AIRLINE-DOMAIN SKILL.MD
  Read:  skills/airline-domain/SKILL.md
  Apply: AIRLINE_NAME, AIRLINE_ID, HUB_IATA, BASE_URL, FLIGHT_NUMBER_FORMAT,
         FARE_FAMILIES, PAX_TYPES, DEFAULT_CURRENCY, JIRA_URL, TMS_URL, QA_TEAM_EMAIL
  Write the file. Verify: no provided values remain as {placeholders}.
  ↓
STEP 4 — CONFIGURE FLIGHTS-AND-FARES.MD
  Read:  skills/airline-domain/references/flights-and-fares.md
  Apply: HUB_IATA, secondary IATAs, carrier code, flight number format,
         fare family names + inclusions, pax types + age ranges
  Write the file. Verify.
  ↓
STEP 5 — CONFIGURE WEB-FLOWS.MD
  Read:  skills/airline-domain/references/web-flows.md
  Apply: BASE_URL, fare family names in flow steps, pax type codes
  Note: URL paths per page are LEFT as {placeholder} — they require manual
        navigation of the real website (or will be discovered by web-exploration).
        Document this as a known remaining item.
  Write the file. Verify.
  ↓
STEP 6 — CONFIGURE UI-PATTERNS.MD
  Read:  skills/airline-domain/references/ui-patterns.md
  Apply: AIRLINE_NAME where it appears
  Note: Actual CSS selectors for spinner, cookie consent, iFrame, etc. are
        LEFT as {placeholder} — web-exploration discovers them automatically.
        Document this as expected.
  Write the file. Verify.
  ↓
STEP 7 — CONFIGURE BUSINESS-RULES.MD
  Read:  skills/airline-domain/references/business-rules.md
  Apply: AIRLINE_NAME, DEFAULT_CURRENCY, fare family names, pax type codes
  Note: Specific timeouts and limits that are unknown are LEFT as {N} — document them.
  Write the file. Verify.
  ↓
STEP 8 — CONFIGURE FRAMEWORK TEMPLATES
  Read:  skills/framework-scaffolding/assets/templates/java/allure.properties
  Apply: JIRA_URL, TMS_URL, AIRLINE_NAME
  Write the file.

  Read:  skills/framework-scaffolding/assets/templates/java/pom.xml
  Apply: airline-id, AIRLINE_NAME
  Write the file.

  Read:  skills/reporting/assets/templates/run-guide.md
  Apply: airline-id, AIRLINE_NAME, QA_TEAM_EMAIL
  Write the file.

  Read:  skills/api-karate-testing/assets/templates/karate-config.js
  Apply: airline-id (in URL fallbacks)
  Write the file.

  Read:  skills/api-karate-testing/assets/templates/pom.xml
  Apply: airline-id, AIRLINE_NAME
  Write the file.

  Read:  skills/api-karate-testing/assets/templates/booking-flow.feature
  Apply: airline-id (in contactEmail placeholder)
  Write the file.
  ↓
STEP 9 — VERIFICATION SCAN
  Scan all modified files for remaining {placeholder} patterns.
  Classify each as:
    - EXPECTED (selectors, URL paths — will be filled by web-exploration)
    - MISSING_INPUT (user didn't provide the value — request it or document it)
  ↓
STEP 10 — DELIVER SUMMARY
  Return output JSON and print a human-readable summary.
```

---

## Interview Template

Use this consolidated question format to minimize round trips:

```
To configure this agent for [AIRLINE], I need some information.
Please provide what you know — anything optional can be skipped:

REQUIRED:
  1. Full airline name:          e.g., "Iberia"
  2. Short ID (for files):       e.g., "iberia"  (lowercase, no spaces)
  3. Primary hub IATA:           e.g., "MAD"
  4. Secondary IATA codes:       e.g., "LHR, BCN, JFK"
  5. Booking website URL:        e.g., "https://www.iberia.com"
  6. Carrier code (2 letters):   e.g., "IB"
  7. Fare families:              e.g., "Economy, Economy Plus, Business"
  8. Default currency:           e.g., "EUR"

OPTIONAL:
  9.  API base URL (if API tests needed):  e.g., "https://api.iberia.com"
  10. JIRA URL:     e.g., "https://jira.yourcompany.com"
  11. TMS URL:      e.g., "https://testlink.yourcompany.com"
  12. QA team email: e.g., "qa-team@company.com"
```

---

## What Remains After This Skill Completes

These items are intentionally left for later — document them clearly in the summary:

| Item | Why left | Who fills it |
|------|----------|-------------|
| UI selectors (spinner, cookie, iFrame) | Must be discovered on the real website | `web-exploration` skill (automatic) |
| URL paths per booking step | Vary per airline; must be navigated manually or discovered | Manual or `web-exploration` |
| Error message selectors | Only visible during test failures | `self-healing` skill (automatic) |
| Pax-type edge cases | Need business confirmation | QA team |
| API endpoint paths | Depend on the airline's API design | User provides OpenAPI spec or manual input |

---

## Success Criteria

Work is complete when:
- [ ] All user-provided values written to all target files
- [ ] No provided value remains as `{placeholder}` anywhere
- [ ] Remaining `{placeholder}` values are classified as EXPECTED or MISSING_INPUT
- [ ] Summary delivered with:
  - list of files modified
  - list of remaining placeholders and their classification
  - clear next step: "Run the orchestrator — it will invoke web-exploration to discover selectors automatically"

Base directory for this skill: skills/airline-setup
