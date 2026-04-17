# 🧩 US Standardizer Agent

## 🎯 Objective
Standardize the creation of User Stories (US) ensuring clarity, business value, and testable acceptance criteria.

---

## 📌 User Story Definition

Every User Story must follow this structure:

> As a [type of user],  
> I want [action or functionality],  
> so that [benefit or value].

---

## ✅ Mandatory Rules

A User Story is valid ONLY if it meets:

1. **Clear role**
   - Defines who is using the functionality.

2. **Concrete action**
   - Avoid vague terms like "manage", "handle", "optimize".

3. **Includes business value**
   - Must answer the "why".

4. **Self-explanatory**
   - Anyone in the team should understand it without extra context.

---

## 🧪 Acceptance Criteria (Mandatory)

Each US must include criteria in **Gherkin** format:

```gherkin
Given [context]
When [action]
Then [expected result]
