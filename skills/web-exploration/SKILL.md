# Skill: web-exploration
# Web Exploration — MCP-Based Selector Discovery & Validation

## Role & Context

Act as an **Expert UI Inspector** using MCP browser tools to live-navigate the airline
web application and discover the real CSS selectors needed for test automation.

This skill produces a **validated selector map** — the single source of truth for all
locators used in code generation and self-healing. No selector enters the codebase
without being validated by this skill first.

## Domain References

| Reference | Load When |
|-----------|-----------|
| `skills/airline-domain/references/ui-patterns.md` | Handling initial page state, known airline-specific selectors |
| `skills/airline-domain/references/web-flows.md` | Understanding page navigation order and URL patterns |
| `references/mcp-tools-reference.md` | Full MCP tool catalog and usage examples |
| `references/selector-strategy.md` | Decision tree for choosing the right selector |

## Inputs

```json
{
  "url": "${application_url}",
  "pages_to_explore": ["HomePage", "AvailabilityPage", "..."],
  "elements_needed": ["originInput", "destinationInput", "searchButton", "..."],
  "mode": "discovery | revalidation",
  "target_element": "optional — specific element for revalidation mode"
}
```

## Outputs

```json
{
  "selectors": {
    "HomePage.originInput": "[data-testid='search-origin']",
    "HomePage.destinationInput": "[data-testid='search-destination']",
    "HomePage.searchButton": "[data-testid='search-submit']",
    "...": "..."
  },
  "validated_count": 0,
  "fallback_count": 0,
  "not_found": ["PageName.elementName"],
  "screenshots": ["exploration-homepage.png"],
  "iframes_detected": ["#payment-iframe"]
}
```

## Critical Rules

1. **Never guess selectors** — every selector must be verified with `element_exists` + `check_visibility`
2. **Selector priority is law** — always prefer `data-testid` > `data-qa` > `id` > `name` > CSS > XPath. Never skip levels without trying higher priority first.
3. **Document every fallback** — when a selector required going past Priority 2, mark it as `fallback` in the output
4. **iFrame detection is mandatory** — if any page contains iframes, record them. Payment pages in most airlines use an iFrame for PCI compliance — verify during exploration.
5. **Screenshot every page** — visual evidence of what the browser sees is required

## Execution Workflow

```
STEP 1 — NAVIGATE & HANDLE INITIAL STATE
  [READ REFERENCE: references/mcp-tools-reference.md]
  Navigate to URL → handle cookie consent → close popups
  ↓
STEP 2 — DISCOVER SELECTORS PER PAGE
  [READ REFERENCE: references/selector-strategy.md]
  For each page: get_test_ids → inspect_dom → query_selector
  ↓
STEP 3 — VALIDATE EACH SELECTOR
  element_exists → check_visibility → mark as validated or fallback
  ↓
STEP 4 — APPLY FALLBACK PROTOCOL IF NEEDED
  inspect_dom parent → find_by_text → screenshot → document as not_found
  ↓
STEP 5 — DETECT iFRAMES & SPECIAL CONTEXTS
  Detect iframes, shadow DOM, dynamic containers
  ↓
STEP 6 — RETURN SELECTOR MAP
  Structure output JSON with all selectors, counts, and screenshots
```

## Revalidation Mode

When called with `mode: revalidation` from `self-healing` skill:

1. Navigate directly to the page containing the failing element
2. Handle initial state (cookie, popup)
3. Run discovery ONLY for the specified `target_element`
4. Apply full fallback protocol
5. Return the replacement selector (or `null` if not found after all levels)

## Initial State Handling (Always Required)

> Load `skills/airline-domain/references/ui-patterns.md` to get the real selectors for
> cookie consent and promo popups for the configured airline.

**Step A — Cookie consent:**
```
[READ REFERENCE: skills/airline-domain/references/ui-patterns.md]
[MCP] element_exists → { "selector": "{cookie-accept-selector from airline-domain}" }
  → if true: click → wait for disappear
  → if false: continue
```

**Step B — Promo popup:**
```
[MCP] element_exists → { "selector": "{promo-close-selector from airline-domain}" }
  → if true: click
  → if false: continue
```

**Step C — Wait for stable state:**
```
[MCP] wait_for_network_idle → {}
```

## Success Criteria

Work is complete when:
- [ ] All requested pages explored
- [ ] All requested elements have a validated selector (or documented as not_found)
- [ ] At least 1 screenshot per page captured
- [ ] iFrame presence documented if detected
- [ ] Selector map returned to orchestrator

Base directory for this skill: skills/web-exploration
