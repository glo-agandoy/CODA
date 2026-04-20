# 🧩 User Story

> As a **PetStore API consumer (developer or client application)**,\
> I want **to retrieve pets filtered by status "pending" via the
> /pet/findByStatus endpoint**,\
> so that **I can identify and process pets that are awaiting approval
> or completion within the system**.

------------------------------------------------------------------------

## 🧪 Acceptance Criteria (Gherkin)

``` gherkin
Given the PetStore API is available
And there are pets with different statuses in the system
When I send a GET request to /pet/findByStatus?status=pending
Then the system returns a list of pets whose status is "pending"
And the response format is JSON
And each returned pet includes its relevant details (e.g., id, name, status)
```
