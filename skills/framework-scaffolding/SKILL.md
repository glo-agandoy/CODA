# Skill: framework-scaffolding
# Framework Scaffolding — Project Structure & Configuration

## Role & Context

Act as an **Expert Build Engineer** responsible for creating the complete automation
project skeleton: directory structure, dependency management files, and all configuration.

This skill runs **before** code generation. Code generation assumes the scaffold is already in place.

## Bundled Templates

Copy these templates to the project root and replace placeholders.

### Java Templates
| Template | Destination | Purpose |
|----------|-------------|---------|
| `assets/templates/java/pom.xml` | `./pom.xml` | Maven build with all dependencies pinned |
| `assets/templates/java/config.properties` | `src/test/resources/config.properties` | Externalized test config |
| `assets/templates/java/allure.properties` | `src/test/resources/allure.properties` | Allure report config |

### Python Templates
| Template | Destination | Purpose |
|----------|-------------|---------|
| `assets/templates/python/requirements.txt` | `./requirements.txt` | Pinned dependencies |
| `assets/templates/python/pytest.ini` | `./pytest.ini` | Pytest config with Allure |
| `assets/templates/python/conftest.py` | `./tests/conftest.py` | Base WebDriver fixtures |

### TypeScript Templates
| Template | Destination | Purpose |
|----------|-------------|---------|
| `assets/templates/typescript/package.json` | `./package.json` | npm with Playwright + Allure |
| `assets/templates/typescript/playwright.config.ts` | `./playwright.config.ts` | Playwright config |
| `assets/templates/typescript/tsconfig.json` | `./tsconfig.json` | TypeScript compiler config |

## Inputs

```json
{
  "framework_type": "Java | Python | TypeScript",
  "project_mode": "new | extend_existing",
  "existing_framework_path": "optional",
  "application_url": "${BASE_URL}"
}
```

## Outputs

```json
{
  "status": "COMPLETED | FAILED",
  "project_root": "./",
  "directories_created": [],
  "files_created": [],
  "existing_patterns": {
    "naming_convention": "CamelCase | snake_case",
    "wait_strategy": "WebDriverWait | FluentWait | explicit",
    "allure_style": "method-level | class-level"
  },
  "ready_for_code_generation": true
}
```

## Critical Rules

1. **Create all directories before writing files** — never assume a directory exists
2. **Use exact version pins** — no version ranges (`^`, `~`) in dependency files
3. **Externalize ALL configuration** — no URLs, timeouts, or credentials in source code
4. **If `extend_existing`** — read existing code patterns first; never overwrite existing files

## Execution Workflow

```
STEP 1 — DETECT OR ANALYZE EXISTING FRAMEWORK
  If project_mode == "extend_existing":
    Run bash to find existing Page Objects, test classes, configs
    Extract naming conventions and patterns
    Report as existing_patterns in output
  ↓
STEP 2 — CREATE DIRECTORY STRUCTURE
  [See directory trees per framework type below]
  Run mkdir -p for all required directories
  ↓
STEP 3 — COPY & CONFIGURE TEMPLATES
  Copy relevant templates from assets/templates/{framework_type}/
  Replace placeholders:
    {BASE_URL} → application_url
    {FRAMEWORK_TYPE} → Java | Python | TypeScript
  ↓
STEP 4 — VERIFY SCAFFOLD
  Run compilation check (mvn compile -q | tsc --noEmit | python -m py_compile)
  Confirm all directories exist
  ↓
STEP 5 — REPORT
  Return output JSON with all created files and existing_patterns
```

## Directory Structures

### Java
```bash
mkdir -p \
  src/main/java/pages \
  src/main/java/components \
  src/main/java/utils \
  src/test/java/tests \
  src/test/resources/testdata \
  normalized-tests \
  allure-results \
  docs \
  metrics
```

### Python
```bash
mkdir -p \
  pages components utils tests resources/testdata \
  normalized-tests allure-results docs metrics
```

### TypeScript
```bash
mkdir -p \
  src/pages src/components src/utils \
  tests fixtures \
  normalized-tests allure-results docs metrics
```

## Existing Framework Detection

```bash
# Find Page Objects
find . -name "*Page.java" -o -name "*_page.py" -o -name "*.page.ts" | head -10

# Find test classes
find . -name "*Test.java" -o -name "test_*.py" -o -name "*.spec.ts" | head -10

# Find config files
find . -name "pom.xml" -o -name "package.json" -o -name "requirements.txt" | head -5

# Read a Page Object to detect naming style
cat $(find . -name "*Page.java" | head -1)
```

Extract:
- **Naming convention** — `CamelCase` (Java) or `snake_case` (Python)
- **Wait strategy** — which `WebDriverWait` pattern is used
- **Allure style** — annotations at method level vs class level
  - **Base package** — `com.{airline_id}` or equivalent
- **Existing utilities** — `WaitUtils`, `ConfigReader`, etc. that should not be recreated

## Success Criteria

Work is complete when:
- [ ] All directories created
- [ ] Dependency file written (pom.xml / requirements.txt / package.json)
- [ ] Configuration files written (config.properties / pytest.ini / playwright.config.ts)
- [ ] Allure properties configured
- [ ] Compilation/type-check passes
- [ ] `existing_patterns` extracted if `extend_existing`

Base directory for this skill: skills/framework-scaffolding
