# Skill: vueling-domain
# Vueling Airlines — Domain Knowledge Base

## Purpose

Centralized reference for all Vueling Airlines domain-specific knowledge.
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

## Critical Rules

1. **Always use IATA codes** — never city names in test data or selectors
2. **Never hardcode credentials** — always `${TEST_USER_EMAIL}` / `${TEST_USER_PASSWORD}`
3. **Never hardcode dates** — always T+N notation (e.g., T+7, T+14)
4. **Selector language is i18n** — never select elements by visible text; use `data-testid`
5. **Spinner is mandatory** — every page transition requires waiting for the airplane spinner to disappear

## Quick Reference

### IATA Hub
`BCN` — Barcelona (Hub)

### Core Pax Types
`ADT` (Adult) · `CHD` (Child, 2–11 y) · `INF` (Infant, <2 y — must have ADT)

### Fare Families
`Basic` · `Optima` · `TimeFlex` · `Family`

### Booking Flow (6 steps — sequential, cannot skip)
```
Homepage Search → Availability → Passenger Details → Ancillaries → Extras → Payment
```

Base directory for this skill: skills/vueling-domain
