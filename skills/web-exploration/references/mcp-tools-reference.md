# Reference: mcp-tools-reference
# MCP Web Inspector — Full Tool Catalog

MCP tool: `mcp-web-inspector`

## Navigation Tools

### navigate
Go to a URL. Always the first action in any exploration session.
```json
Tool: navigate
Arguments: { "url": "${application_url}" }
```

### wait_for_network_idle
Wait until all pending network requests complete. Use after navigation and after actions that trigger server calls.
```json
Tool: wait_for_network_idle
Arguments: {}
```

### wait_for_element
Wait for an element to appear in the DOM (useful after transitions).
```json
Tool: wait_for_element
Arguments: { "selector": "[data-testid='flight-results']", "timeout": 15000 }
```

---

## DOM Inspection Tools

### inspect_dom
Inspect the DOM structure starting from a CSS selector. Use to map the structure of a container and find child elements.
```json
Tool: inspect_dom
Arguments: { "selector": "main", "maxChildren": 50 }
```
Returns: nested DOM structure with tag names, attributes, and text content.

**Best practice:** Start with `main` for page-level exploration; narrow to specific containers when looking for a specific element.

### get_test_ids
List all elements that have a `data-testid` attribute on the current page. Always run this first — it gives the full map of automation-friendly selectors.
```json
Tool: get_test_ids
Arguments: {}
```
Returns: array of `{ selector, value }` pairs.

### query_selector
Query elements matching a CSS selector and return their attributes.
```json
Tool: query_selector
Arguments: { "selector": "[data-testid='search-origin']" }
```

---

## Element Validation Tools

### element_exists
Check if a CSS selector matches any element in the DOM.
```json
Tool: element_exists
Arguments: { "selector": "[data-testid='search-origin']" }
```
Returns: `true | false`

**Rule:** Always run this before `check_visibility`. If `false`, skip visibility check.

### check_visibility
Check if a matched element is actually visible to the user (not hidden, not display:none, not off-screen).
```json
Tool: check_visibility
Arguments: { "selector": "[data-testid='search-origin']" }
```
Returns: `true | false`

**Validation rule:** A selector is only "validated" if BOTH `element_exists` AND `check_visibility` return `true`.

---

## Fallback Search Tools

### find_by_text
Find elements by their text content. Use as fallback when attribute-based selectors fail.
```json
Tool: find_by_text
Arguments: { "text": "Search flights", "exact": false }
```
Returns: array of matching elements with selectors.

**Warning:** Text-based selectors are fragile for i18n content. Only use to locate an element, then derive an attribute-based selector from the result.

---

## Visual & Debug Tools

### visual_screenshot_for_humans
Capture a screenshot of the current page state. Required after each page exploration.
```json
Tool: visual_screenshot_for_humans
Arguments: { "name": "exploration-homepage", "fullPage": true }
```

### get_console_logs
Retrieve browser console logs. Use when an element is missing to check for JS errors.
```json
Tool: get_console_logs
Arguments: { "type": "error" }
```

---

## Interaction Tools (for Initial State Handling Only)

> Use these ONLY for handling cookie consent and closing popups during exploration setup.
> Do NOT use for test execution — test execution is handled by the Selenium framework.

### click
Click an element by CSS selector.
```json
Tool: click
Arguments: { "selector": "[data-testid='cookie-accept']" }
```

### type
Type text into an input field.
```json
Tool: type
Arguments: { "selector": "[data-testid='search-origin']", "text": "${TEST_ORIGIN_IATA}" }
```

---

## Common Exploration Sequence

```
1. navigate          → go to URL
2. wait_for_network_idle → wait for page load
3. get_test_ids      → map all data-testid attributes
4. inspect_dom       → { "selector": "main", "maxChildren": 50 }
5. [for each element needed]:
   element_exists    → check presence
   check_visibility  → check visibility
   [if not found]:
     inspect_dom     → inspect parent container
     find_by_text    → fallback text search
     visual_screenshot_for_humans → capture evidence
6. visual_screenshot_for_humans → page-level screenshot
```

## iFrame Detection Sequence

```
1. inspect_dom → { "selector": "iframe" }
   [if results found]:
   → record selector for each iframe
   → note: most airline payment pages have an iframe for PCI compliance — verify selector in airline-domain/references/ui-patterns.md
   → document in output as iframes_detected: ["#payment-iframe"]
```
