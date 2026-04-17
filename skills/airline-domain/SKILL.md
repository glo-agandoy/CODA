# Skill: airline-domain
# Airline — Domain Knowledge Base

## Purpose

Centralized reference for all airline-specific domain knowledge.
This skill is a **read-only reference** — it does not execute tasks.
Other skills reference this skill's content explicitly using `[READ REFERENCE: references/<file>.md]`.

## How to Use This Skill

Load specific references on-demand when you need them:

| Reference | Load When |
|-----------|-----------|
| `references/flights-and-fares.md` | Writing test data, validating IATA codes, fare families, pax types |
| `references/web-flows.md` | Understanding page sequences, flow order validation |
| `references/ui-patterns.md` | Writing selectors, handling spinners/popups/iframes |
| `references/business-rules.md` | Validating business logic, edge cases, booking rules |

---

## AIRLINE CONFIGURATION

> This skill is currently configured as a **Iberia-specific setup**.
> Iberia values are filled in below and in reference files.

| Parameter | Value | Where it's used |
|-----------|-------|-------------------|
| `AIRLINE_NAME` | `Iberia` | This file + all references |
| `AIRLINE_ID` | `iberia` | pom.xml, package.json, run-guide |
| `HUB_IATA` | `MAD` | `references/flights-and-fares.md` |
| `BASE_URL` | `https://www.iberia.com` | `references/web-flows.md` + scaffolding templates |
| `FLIGHT_NUMBER_FORMAT` | `IBnnnn` | `references/flights-and-fares.md` |
| `FARE_FAMILIES` | `Economy, Business, Premium` | `references/flights-and-fares.md` |
| `DEFAULT_CURRENCY` | `EUR` | `references/business-rules.md` |
| `JIRA_URL` | `{https://jira.yourcompany.com}` | `skills/framework-scaffolding/assets/templates/java/allure.properties` |
| `TMS_URL` | `{https://testlink.yourcompany.com}` | `skills/framework-scaffolding/assets/templates/java/allure.properties` |
| `QA_TEAM_EMAIL` | `{qa-team@yourcompany.com}` | `skills/reporting/assets/templates/run-guide.md` |

---

## Critical Rules (Universal — apply to all airlines)

1. **Always use IATA codes** — never city names in test data or selectors
2. **Never hardcode credentials** — always `${TEST_USER_EMAIL}` / `${TEST_USER_PASSWORD}`
3. **Never hardcode dates** — always T+N notation (e.g., T+7, T+14)
4. **Selector language is i18n** — never select elements by visible text; use `data-testid`
5. **Loading indicator is mandatory** — every page transition requires waiting for the loading spinner to disappear before proceeding

---

## Quick Reference

### IATA Hub
MAD — Madrid

### Core Pax Types
ADT · CHD · INF

> See `references/flights-and-fares.md` for full definitions, age ranges, and booking rules.

### Fare Families
Economy · Business · Premium

> See `references/flights-and-fares.md` for inclusions, bag allowances, and change policies.

### Booking Flow (sequential — cannot skip steps)
```
{Step 1} → {Step 2} → {Step 3} → {Step 4} → {Step 5} → {Step 6}
```

> See `references/web-flows.md` for full step-by-step breakdown, URL patterns, and transition rules.

---

## Fine-Tuning Checklist

When adapting this skill for a new airline, complete these steps in order:

- [x] Fill in all `Iberia` and `iberia` placeholders in this file
- [ ] Complete `references/flights-and-fares.md` — IATA network, flight format, fare families, pax types, ancillaries, test data
- [ ] Complete `references/web-flows.md` — BASE_URL, URL paths per page, booking flow steps, page class names
- [ ] Complete `references/ui-patterns.md` — loading spinner selector, cookie consent selectors, airline-specific widgets
- [ ] Complete `references/business-rules.md` — check-in window, max pax, payment rules, edge cases, error selectors
- [ ] Update `skills/framework-scaffolding/assets/templates/java/allure.properties` with real JIRA/TMS URLs
- [ ] Update `skills/reporting/assets/templates/run-guide.md` with QA team email and CI/CD credentials
- [ ] Update `orchestrator.md` with airline name in session metadata and description

> See `AIRLINE_CUSTOMIZATION_GUIDE.md` in the agent root for the complete step-by-step fine-tuning guide with time estimates.

---

Base directory for this skill: skills/airline-domain