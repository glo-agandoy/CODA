# Reference: selector-strategy
# Selector Decision Tree & Strategy Guide

## Priority Hierarchy

Always attempt selectors in this order. Only move to the next level if the current level yields no stable result.

```
Level 1: data-testid
  └── [data-testid='element-name']
  └── Most stable, explicitly placed for automation
  └── ALWAYS prefer this

Level 2: data-qa / data-cy
  └── [data-qa='element-name']
  └── [data-cy='element-name']
  └── QA-specific attributes — stable

Level 3: static id
  └── #element-id
  └── ONLY use if the id is clearly static (not auto-generated)
  └── Skip if id contains numbers that may vary per session

Level 4: name attribute
  └── [name='email']
  └── [name='cardNumber']
  └── Stable for form fields

Level 5: CSS combination
  └── .parent-class .child-class
  └── [type='submit'].search-form__btn
  └── Use only when above levels are unavailable

Level 6: XPath (last resort)
  └── //button[contains(@class,'search-btn')]
  └── //div[@role='dialog']//button[1]
  └── Only use when NO attribute-based approach works
  └── Must be documented as a known fragile selector
```

## Decision Rules

### Use `data-testid` when:
- `get_test_ids` returns it for the target element
- The value is descriptive (e.g., `search-origin`, not `abc123`)

### Skip to Level 2 when:
- `get_test_ids` returns no result for the target element
- The `data-testid` value appears auto-generated (random hash)

### Skip to Level 3 when:
- No `data-qa` or `data-cy` is present
- The element has an `id` that is clearly human-authored (e.g., `booking-form`, not `input-3847`)

### Skip to Level 4 when:
- The element has a `name` attribute (typical for form inputs)
- `name` is stable (does not change per session or render)

### Skip to Level 5 when:
- No attribute selectors are available
- The element is identifiable by its position within a stable parent container

### Skip to Level 6 when:
- The element has no stable attributes
- Must be identified by relationship (e.g., "first button inside the promo dialog")
- **Always document Level 6 selectors** as requiring monitoring for breakage

## Anti-Patterns (Never Use)

| Anti-Pattern | Why | Alternative |
|-------------|-----|-------------|
| `#component-123456` | Dynamic IDs change per render | Look for `data-testid` on same element |
| `.css-1a2b3c4d` | CSS-in-JS generated class names | Use `data-testid` or `name` |
| `//button[text()='Search flights']` | Breaks on locale change | Use `data-testid` or `[type='submit']` |
| `div:nth-child(3) > span` | Position-based — breaks on DOM reorder | Use attribute selector on the target |
| `[class='header-logo active selected']` | Full class match — breaks on any class addition | Use `[data-testid]` or partial class match |

## Validation States

Mark each discovered selector with one of these states in the output:

| State | Meaning | Level Used |
|-------|---------|------------|
| `validated` | `element_exists: true` AND `check_visibility: true` | 1–4 |
| `fallback` | Validated but required Level 5 or 6 | 5–6 |
| `hidden` | `element_exists: true` but `check_visibility: false` | Any |
| `not_found` | `element_exists: false` after full fallback protocol | — |

## Special Cases

> The selectors below are generic starting points.
> The configured airline's actual selectors are in `skills/airline-domain/references/ui-patterns.md`.
> Always validate with MCP tools during web-exploration — never assume selectors match.

### Autocomplete Dropdowns (Airport Search)
```css
/* Generic pattern — common in airline booking sites */
[data-testid='station-suggestions']          /* dropdown container */
[data-testid='station-suggestion']           /* individual item */
[data-testid='station-suggestion']:first-child  /* first match */
```
Validation: Type a known IATA code first, then validate dropdown selectors.
Note: Some airlines use a `data-iata` or `data-code` attribute on suggestion items — discover during exploration.

### Calendar Widget
```css
/* Generic pattern — verify actual selectors with get_test_ids */
[data-testid='calendar']                    /* Container */
[data-testid='calendar-next']               /* Next month */
[data-testid='calendar-prev']               /* Prev month */
[data-testid='calendar-day-YYYY-MM-DD']     /* Specific date cell */
```
Validation: Calendar only appears after clicking a date input. Click first, then validate.

### Payment iFrame
```css
/* Generic patterns for PCI-compliant payment pages */
#payment-iframe
[data-testid='payment-frame']
iframe[src*='payment']
```
**Note:** Selectors inside the iframe (card number, expiry, CVV) cannot be inspected from the parent document. Document the iframe selector only; card field selectors are standard PCI iframe patterns documented in `code-generation` templates.

### Loading Spinner
```css
/* Generic pattern — airline-specific fallback in airline-domain/references/ui-patterns.md */
[data-testid='loading-spinner']   /* Primary — data-testid is standard */
{.airline-loading-class}          /* Fallback — airline-specific CSS class */
```
Validation: Spinner appears transiently — screenshot immediately after a transition to capture it.
