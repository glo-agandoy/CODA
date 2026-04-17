# E2E Testing Lifecycle & Governance

## Purpose
This document defines the strictly operational lifecycle of an automated test. It moves beyond theory to define **exact actions** required to create, promote, quarantine, and delete tests within the repository and the CI/CD pipeline.

## Audience
* **QA Engineers:** Must follow the "Quarantine Protocol" to keep pipelines green.
* **Developers:** Must understand why tests are rejected in PRs.

## The Decision Matrix (Do we automate this?)
Before writing a single line of code, apply this filter. We do not automate everything.

| Criteria | Condition | Decision |
| :--- | :--- | :--- |
| **Stability** | Is the UI stable? (No major refactors planned in 2 sprints) | **IF NO:** Do NOT Automate. |
| **Criticality** | Is this a core business flow (Booking, Payment, Login)? | **IF YES:** Automate (E2E). |
| **Logic** | Can this be validated via Unit Test or API Integration? | **IF YES:** Automate in Lower Layers (Not E2E). |
| **Complexity** | Does it require 3rd party tools (e.g., Email OTP, Captcha)? | **IF YES:** Discuss Mocking Strategy first. |

---

## The Operational Lifecycle

### Stage 1: Local Construction (The "5-Run Rule")
When writing a new test (in WebdriverIO/Karate):
1.  **Tagging:** Every test must have a Jira ID tag (e.g., `@IB-1234`).
2.  **Isolation:** The test must create its own data.
3.  **The Validation Rule:** Before opening a PR, you must run the test locally **5 times in a row**.
    * *Success Criteria:* 5/5 Pass.
    * *Failure:* If it fails once locally, it **will** fail in the pipeline. Fix it.

### Stage 2: The PR Gate (Integration)
You push the code to Git. The pipeline triggers.
* **Gate 1:** Linting (Static analysis).
* **Gate 2:** Headless execution of the *new* test file.
* **Review:** A peer (Dev or QA) must review the code. Look for hardcoded waits (`sleep`) or weak selectors.

### Stage 3: Production (Regression Suite)
Once merged, the test enters the `regression` tag.
* **Governance:** From this moment, if this test fails, it **blocks the deployment**.

### Stage 4: The Quarantine Protocol (Dealing with Flakiness)
If a test in the `regression` suite fails randomly (Flaky), follow this **strict protocol**. Do not debate it.

**The Protocol:**
1.  **Identify:** The pipeline failed, but a re-run passed. The test is flaky.
2.  **Tag (Immediate):** Create a PR adding the `@quarantine` tag to the test code.
    ```javascript
    // WebdriverIO Example
    it('should book a flight', { tags: ['@quarantine'] }, async () => { ... })
    ```
    * *Effect:* The CI pipeline is configured to **exclude** `@quarantine` tags. The build turns Green immediately.
3.  **Ticket:** Create a Jira Bug/Task: `[FLAKY] Fix test IB-1234`. Assign it to the Squad.
4.  **Fix & Return:**
    * Analyze root cause (Race condition? Environment?).
    * Apply fix.
    * Run locally x20.
    * Remove `@quarantine` tag.

### Stage 5: Retirement
If a feature is removed or the flow changes entirely:
1.  **Delete** the test file.
2.  **Archive** the test execution in Xray (set status to "Obsolete").
3.  **Do not comment out code.** Commented code is trash.

---

## Technical Standards for Reliability

### Selector Strategy (Priority Order)
To avoid brittle tests, you must strictly use selectors in this order:

1.  `data-testid` / `data-cy` (Best practice: Devs add this attribute).
2.  `id` (Unique identifiers).
3.  `accessibility-id` (For Mobile).
4.  **FORBIDDEN:** Full XPath (`/html/body/div[2]/div/span...`) or CSS classes dependent on style (`.btn-blue`).

### Wait Strategy
* **FORBIDDEN:** `Thread.sleep(5000)` or `.pause(5000)`.
* **MANDATORY:** Explicit waits.
    * `waitForDisplayed()`
    * `waitForClickable()`
    * `waitForResponse()`

---

## Xray Integration (Visibility)
Automation is not an island; it must reflect in Jira.

1.  **Linking:** Every automation script must map to a **Test Issue** in Jira (Xray).
2.  **Synchronization:** The CI Pipeline must push results to Xray.
    * *Green Pipeline:* Xray Test Execution = **PASS**.
    * *Red Pipeline:* Xray Test Execution = **FAIL**.
3.  **Manual vs Auto:** If a test is automated, **change the Xray Test Type** to "Automated". Do not keep it as "Manual".

## Metrics & Health
**QA Champion** checks these stats weekly:

* **Quarantined Tests:** If > 5 tests are in quarantine, the Squad must stop feature work to fix the suite.
* **Execution Time:** If the suite takes > 15 mins, we must implement **Parallelization** (Sharding).