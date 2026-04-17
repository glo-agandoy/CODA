# Customization Guide Summary
# Quick Reference for airline-setup Skill

Full guide: `AIRLINE_CUSTOMIZATION_GUIDE.md` (agent root)

---

## Files to Modify — Ordered Checklist

| # | File | Placeholders to replace | Step |
|---|------|------------------------|------|
| 1 | `skills/airline-domain/SKILL.md` | `{AIRLINE_NAME}`, `{airline-id}`, `{HUB_IATA}`, `{Hub City}`, `{BASE_URL}`, `{XXnnnn}`, `{FARE_FAMILY_1..4}`, `{PAX_TYPE_1..3}`, `{JIRA_URL}`, `{TMS_URL}`, `{QA_TEAM_EMAIL}`, `{DEST_IATA}` | 3 |
| 2 | `skills/airline-domain/references/flights-and-fares.md` | `{HUB_IATA}`, `{IATA_N}`, `{AIRLINE_ICAO_PREFIX}`, `{FARE_FAMILY_N}`, `{PAX_TYPE_N}` | 4 |
| 3 | `skills/airline-domain/references/web-flows.md` | `{BASE_URL}`, `{FARE_FAMILY_N}`, `{PAX_TYPE_N}` *(URL paths left for web-exploration)* | 5 |
| 4 | `skills/airline-domain/references/ui-patterns.md` | `{AIRLINE_NAME}` *(selectors left for web-exploration)* | 6 |
| 5 | `skills/airline-domain/references/business-rules.md` | `{AIRLINE_NAME}`, `{EUR/USD/GBP}`, `{FARE_FAMILY_N}`, `{PAX_TYPE_N}` *(numeric limits left if unknown)* | 7 |
| 6 | `skills/framework-scaffolding/assets/templates/java/allure.properties` | `{JIRA_URL}`, `{TMS_URL}`, `{AIRLINE_NAME}` | 8 |
| 7 | `skills/framework-scaffolding/assets/templates/java/pom.xml` | `{airline-id}`, `{AIRLINE_NAME}` | 8 |
| 8 | `skills/reporting/assets/templates/run-guide.md` | `{airline-id}`, `{AIRLINE_NAME}`, `{QA_TEAM_EMAIL}` | 8 |
| 9 | `skills/api-karate-testing/assets/templates/karate-config.js` | `{airline-id}` | 8 |
| 10 | `skills/api-karate-testing/assets/templates/pom.xml` | `{airline-id}`, `{AIRLINE_NAME}` | 8 |
| 11 | `skills/api-karate-testing/assets/templates/booking-flow.feature` | `{airline-id}` | 8 |

---

## Placeholder → Value Mapping

| Placeholder | Input field | Example |
|-------------|------------|---------|
| `{AIRLINE_NAME}` | `airline_name` | `Iberia` |
| `{airline-id}` / `{AIRLINE_ID}` | `airline_id` | `iberia` |
| `{HUB_IATA}` | `hub_iata` | `MAD` |
| `{Hub City}` | derived from `hub_iata` | `Madrid` |
| `{IATA_2}` ... `{IATA_N}` | `secondary_iatas[0]` ... | `LHR`, `BCN` |
| `{BASE_URL}` | `base_url` | `https://www.iberia.com` |
| `{AIRLINE_DOMAIN}` | derived from `base_url` | `iberia.com` |
| `{AIRLINE_ICAO_PREFIX}` | `carrier_code` | `IB` |
| `{XXnnnn}` (flight format) | `flight_number_fmt` | `IBnnnn` |
| `{FARE_FAMILY_1}` | `fare_families[0]` | `Economy` |
| `{FARE_FAMILY_2}` | `fare_families[1]` | `Economy Plus` |
| `{FARE_FAMILY_3}` | `fare_families[2]` | `Business` |
| `{FARE_FAMILY_4}` | `fare_families[3]` (if exists) | — |
| `{PAX_TYPE_1}` | `pax_types` adult key | `ADT` |
| `{PAX_TYPE_2}` | `pax_types` child key | `CHD` |
| `{PAX_TYPE_3}` | `pax_types` infant key | `INF` |
| `{EUR/USD/GBP/...}` | `default_currency` | `EUR` |
| `{JIRA_URL}` | `jira_url` | `https://jira.company.com` |
| `{TMS_URL}` | `tms_url` | `https://testlink.company.com` |
| `{QA_TEAM_EMAIL}` | `qa_team_email` | `qa@company.com` |
| `{DEST_IATA}` | `secondary_iatas[0]` | `LHR` |

---

## Placeholders Expected to Remain (do NOT fill these — web-exploration handles them)

These are airline-specific UI selectors and URL paths that can only be correctly
determined by navigating the real airline website:

| Placeholder pattern | File | Discoverer |
|--------------------|------|-----------|
| CSS/XPath selectors for spinner | `ui-patterns.md` | `web-exploration` |
| CSS/XPath for cookie consent | `ui-patterns.md` | `web-exploration` |
| iFrame selectors for payment | `ui-patterns.md` | `web-exploration` |
| Station autocomplete selectors | `ui-patterns.md` | `web-exploration` |
| Calendar widget selectors | `ui-patterns.md` | `web-exploration` |
| URL paths per booking step | `web-flows.md` | Manual or `web-exploration` |
| Error message selectors | `business-rules.md` | `self-healing` |
| `{N}` numeric limits (timeouts, max pax) | `business-rules.md` | User provides |

---

## Summary Output Format

After completing all steps, deliver this summary:

```
AIRLINE SETUP COMPLETE
══════════════════════════════════════════════════

Airline      : {AIRLINE_NAME}
ID           : {airline-id}
Hub          : {HUB_IATA}
Currency     : {DEFAULT_CURRENCY}
Website      : {BASE_URL}

FILES CONFIGURED ({N} files):
  ✓ skills/airline-domain/SKILL.md
  ✓ skills/airline-domain/references/flights-and-fares.md
  ✓ skills/airline-domain/references/web-flows.md
  ✓ skills/airline-domain/references/ui-patterns.md
  ✓ skills/airline-domain/references/business-rules.md
  ✓ skills/framework-scaffolding/assets/templates/java/allure.properties
  ✓ skills/framework-scaffolding/assets/templates/java/pom.xml
  ✓ skills/reporting/assets/templates/run-guide.md
  ✓ skills/api-karate-testing/assets/templates/karate-config.js
  ✓ skills/api-karate-testing/assets/templates/pom.xml
  ✓ skills/api-karate-testing/assets/templates/booking-flow.feature

REMAINING PLACEHOLDERS:
  EXPECTED (web-exploration will fill automatically):
    - UI selectors (spinner, cookie, payment iFrame)
    - URL paths per booking step
  NEEDS INPUT (provide before running if known):
    - {list any values user didn't provide}

NEXT STEP:
  The agent is ready to run. Invoke the orchestrator with your test cases:
    → UI tests:  provide test cases + application URL
    → API tests: provide test cases or OpenAPI spec + API base URL
    → Both:      provide all of the above

══════════════════════════════════════════════════
```
