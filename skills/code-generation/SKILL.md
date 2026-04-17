# Skill: code-generation
# Code Generation — Page Objects, Test Classes & Components

## Role & Context

Act as an **Expert Test Automation Engineer** generating production-ready automation code.
You receive normalized test cases and a validated selector map, and produce a complete,
compilable automation suite following Page Object Model (POM) best practices.

**Never write code before having the selector map from `web-exploration`.**
Every `@FindBy` selector must come from the validated map — no guessing.

## Domain References

| Reference | Load When |
|-----------|-----------|
| `skills/vueling-domain/references/ui-patterns.md` | Writing BasePage — cookie, popup, spinner handling |
| `skills/vueling-domain/references/web-flows.md` | Mapping test steps to page object methods |
| `references/allure-annotations.md` | Adding Allure metadata to all test methods |
| `references/page-object-patterns.md` | Page Object design patterns, fluent API, components |

## Bundled Templates

Copy and adapt these templates — do NOT rewrite from scratch.

### Java Templates
| Template | Target Path | Purpose |
|----------|-------------|---------|
| `assets/templates/java/BasePage.java` | `src/main/java/pages/BasePage.java` | Foundation with spinner/popup/cookie handling |
| `assets/templates/java/BaseTest.java` | `src/test/java/tests/BaseTest.java` | WebDriver lifecycle (setup/teardown/screenshot) |
| `assets/templates/java/WaitUtils.java` | `src/main/java/utils/WaitUtils.java` | Centralized explicit wait strategies |
| `assets/templates/java/ConfigReader.java` | `src/main/java/utils/ConfigReader.java` | Singleton config from properties file |
| `assets/templates/java/CalendarComponent.java` | `src/main/java/components/CalendarComponent.java` | Date picker — T+N calculation |
| `assets/templates/java/StationSelector.java` | `src/main/java/components/StationSelector.java` | Airport autocomplete |

### Python Templates
| Template | Target Path | Purpose |
|----------|-------------|---------|
| `assets/templates/python/base_page.py` | `pages/base_page.py` | Foundation class |
| `assets/templates/python/base_test.py` | `tests/base_test.py` | Pytest base with Allure |

### TypeScript Templates
| Template | Target Path | Purpose |
|----------|-------------|---------|
| `assets/templates/typescript/BasePage.ts` | `src/pages/BasePage.ts` | Foundation Playwright class |

## Inputs

```json
{
  "normalized_test_cases_path": "normalized-tests/normalized-test-cases.md",
  "selector_map": { "PageName.elementName": "[data-testid='selector']" },
  "framework_type": "Java | Python | TypeScript",
  "project_mode": "new | extend_existing",
  "existing_patterns": {
    "naming_convention": "CamelCase | snake_case",
    "wait_strategy": "WebDriverWait | FluentWait"
  }
}
```

## Outputs

```json
{
  "status": "COMPLETED | BLOCKED",
  "files_created": {
    "base_classes": [],
    "page_objects": [],
    "components": [],
    "utilities": [],
    "test_classes": [],
    "documentation": ["README.md"]
  },
  "selectors_used": 0,
  "tests_generated": 0,
  "issues": []
}
```

## Critical Rules

1. **Every `@FindBy` selector must come from the selector_map** — no invented selectors
2. **No `Thread.sleep()`** — always use `WebDriverWait` or `WaitUtils`
3. **No hardcoded dates** — always `LocalDate.now().plusDays(N)`
4. **No hardcoded config values** — always read from `ConfigReader`
5. **Every test method gets full Allure annotations** — see `references/allure-annotations.md`
6. **`@Step` on all Page Object action methods** — no exceptions
7. **Handle spinner + popups** in every page constructor via `BasePage` methods
8. **If `extend_existing`** — match existing naming conventions; never create duplicate utilities

## Execution Workflow

```
STEP 1 — ANALYZE NORMALIZED TEST CASES
  Read normalized-test-cases.md
  Extract: pages needed, elements per page, actions, test data, Allure metadata
  If extend_existing: read existing code patterns
  ↓
STEP 2 — COPY & ADAPT BASE TEMPLATES
  [READ REFERENCE: references/page-object-patterns.md]
  Copy templates from assets/templates/{framework_type}/
  Adapt BasePage and BaseTest (no logic changes, only package names)
  ↓
STEP 3 — GENERATE PAGE OBJECTS
  [READ REFERENCE: skills/vueling-domain/references/ui-patterns.md]
  For each page identified in test cases:
    - Create {PageName}Page.java/py/ts
    - Add @FindBy locators from selector_map
    - Add @Step action methods
    - Add page-specific popup/iframe handling
  ↓
STEP 4 — GENERATE REUSABLE COMPONENTS
  CalendarComponent — date selection (T+N calculation)
  StationSelector — airport autocomplete
  [Additional components if test cases require them]
  ↓
STEP 5 — GENERATE TEST CLASSES
  [READ REFERENCE: references/allure-annotations.md]
  For each test case in normalized-test-cases.md:
    - Create test method following Arrange-Act-Assert
    - Map normalized steps to Page Object method calls
    - Add full Allure annotations (@Epic, @Feature, @Story, @Severity, @TmsLink)
  Group tests by category into one class per category
  ↓
STEP 6 — GENERATE README
  Document: prerequisites, project structure, run commands, Allure, config
  Include selector reference table from selector_map
  ↓
STEP 7 — REPORT
  Return output JSON with all created files
```

## File Naming Conventions

### Java
- Page Objects: `{PageName}Page.java` (e.g., `HomePage.java`, `AvailabilityPage.java`)
- Test Classes: `{Category}Test.java` (e.g., `BookingTest.java`, `CheckInTest.java`)
- Components: `{Name}Component.java` (e.g., `CalendarComponent.java`)
- Utilities: `{Name}Utils.java` or `{Name}Reader.java`

### Python
- Page Objects: `{page_name}_page.py` (e.g., `home_page.py`)
- Test Classes: `test_{category}.py` (e.g., `test_booking.py`)
- Components: `{name}_component.py`
- Utilities: `{name}_utils.py`

### TypeScript
- Page Objects: `{PageName}Page.ts`
- Test Classes: `{category}.spec.ts`
- Components: `{Name}Component.ts`

## Success Criteria

Work is complete when:
- [ ] All normalized test cases have corresponding test code
- [ ] All `@FindBy` selectors sourced from selector_map
- [ ] Page Objects follow POM patterns (see `references/page-object-patterns.md`)
- [ ] All Allure annotations applied (see `references/allure-annotations.md`)
- [ ] No hardcoded values, dates, or credentials
- [ ] No `Thread.sleep()` usage
- [ ] README.md generated with run instructions and selector table
- [ ] Code compiles / type-checks without errors

Base directory for this skill: skills/code-generation
