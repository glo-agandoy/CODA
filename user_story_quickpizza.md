# 🧩 User Story - Token Login API (QuickPizza)

## 📌 User Story

As an API consumer or automated test system,\
I want to authenticate a user using the token login endpoint,\
so that I can obtain a valid session token and access protected
resources.

------------------------------------------------------------------------

## 🎯 Description

This functionality allows a user to log in via the
`/api/users/token/login` endpoint using username, password, and CSRF
token, returning an authenticated session (optionally via cookies when
`set_cookie=true` is enabled).

The request requires valid credentials and a valid CSRF token to
successfully generate an authentication session.

------------------------------------------------------------------------

## 📥 Request Specification

-   **Method:** POST\
-   **Endpoint:** `/api/users/token/login?set_cookie=true`\
-   **Content-Type:** `application/json`

### Body

``` json
{
  "username": "default",
  "password": "12345678",
  "csrf": "FlH15edHxMC110gNrEXzGC2TDZHtYmKw"
}
```

### Headers

-   Content-Type: application/json\
-   Cookie: session and CSRF-related cookies required by backend (OptanonConsent=isGpcEnabled=0&datestamp=Wed+Apr+08+2026+17%3A15%3A08+GMT%2B0200+(hora+de+verano+de+Europa+central)&version=202505.2.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=0af7cc07-2ba4-41c3-98f5-38559c9d9809&interactionCount=1&isAnonUser=1&landingPath=NotLandingPage&groups=C0003%3A0%2CC0004%3A0%2CC0002%3A0%2CC0001%3A1&AwaitingReconsent=false; rl_page_init_referrer=RudderEncrypt%3AU2FsdGVkX1%2B0c8ZLq7ImR9bLAxUdS%2Bcj7KzlsxT60El7im5pNnH97Nz7%2FWaQnVhI; rl_page_init_referring_domain=RudderEncrypt%3AU2FsdGVkX1%2FSsW1oMxFxkKUjYJtEd%2BLNQOWzAIi5UbJkLSLF8%2FfsM4L683tsYe0h; _ga=GA1.1.1138488477.1775661320; rl_user_id=RudderEncrypt%3AU2FsdGVkX1%2FDHDWgaI3CC9vb9RE9TQcFHQX6Tacwjys%3D; rl_anonymous_id=RudderEncrypt%3AU2FsdGVkX1%2BZZ0kUM%2B6W8Bx97zGZJOV%2Fx%2BDVj6BrV%2FJvhyzG8fwNucVtddBDaTaQRuiYAOub%2FaXAlu%2FuaW17Lg%3D%3D; rl_group_id=RudderEncrypt%3AU2FsdGVkX1%2B%2BjCdRW1i6MU97dWK%2BesN0urtx%2FTlQfww%3D; rl_trait=RudderEncrypt%3AU2FsdGVkX1%2FSpWljwJu3Ztwkdwe7lRI8uLElevdVY8Y%3D; rl_group_trait=RudderEncrypt%3AU2FsdGVkX1%2FCsJbkX5z6zDBn5ZEspUHCcIxcRMNNkD4%3D; rl_session=RudderEncrypt%3AU2FsdGVkX19FDSKwVmVuRhWT8rEzKmDJ3i%2BF1cEa%2BGiJnDvtx%2Fdipb2fy2%2B7268BbPj%2BXkILRJJK3D8q1d%2BrCEEy%2FZqNkSxyaoDFpTbqBVlIPayomIxDZ9uzSVejUJar27uTxqSIcMqsRreFkIGQ%2BQ%3D%3D; _ga_DMHSW7B1V7=GS2.1.s1776089440$o2$g1$t1776089448$j52$l0$h0; csrf_token=FlH15edHxMC110gNrEXzGC2TDZHtYmKw; AWSALB=rHnTFJTFSRLFdFW+k7XcuWR/9KWHl+w4Y/wqym4yYaeXXPUr4K6fgCLRJJ85ArAMsp92jYjCUU95ZEbsyFIhxXQti9tvYh4/Aj7WcKUD4iqVl8trwuqqOE16bqVR; AWSALBCORS=rHnTFJTFSRLFdFW+k7XcuWR/9KWHl+w4Y/wqym4yYaeXXPUr4K6fgCLRJJ85ArAMsp92jYjCUU95ZEbsyFIhxXQti9tvYh4/Aj7WcKUD4iqVl8trwuqqOE16bqVR' \
  --cookie 'OptanonConsent=isGpcEnabled=0&datestamp=Wed+Apr+08+2026+17%3A15%3A08+GMT%2B0200+(hora+de+verano+de+Europa+central)&version=202505.2.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=0af7cc07-2ba4-41c3-98f5-38559c9d9809&interactionCount=1&isAnonUser=1&landingPath=NotLandingPage&groups=C0003%3A0%2CC0004%3A0%2CC0002%3A0%2CC0001%3A1&AwaitingReconsent=false; rl_page_init_referrer=RudderEncrypt%3AU2FsdGVkX1%2B0c8ZLq7ImR9bLAxUdS%2Bcj7KzlsxT60El7im5pNnH97Nz7%2FWaQnVhI; rl_page_init_referring_domain=RudderEncrypt%3AU2FsdGVkX1%2FSsW1oMxFxkKUjYJtEd%2BLNQOWzAIi5UbJkLSLF8%2FfsM4L683tsYe0h; _ga=GA1.1.1138488477.1775661320; rl_user_id=RudderEncrypt%3AU2FsdGVkX1%2FDHDWgaI3CC9vb9RE9TQcFHQX6Tacwjys%3D; rl_anonymous_id=RudderEncrypt%3AU2FsdGVkX1%2BZZ0kUM%2B6W8Bx97zGZJOV%2Fx%2BDVj6BrV%2FJvhyzG8fwNucVtddBDaTaQRuiYAOub%2FaXAlu%2FuaW17Lg%3D%3D; rl_group_id=RudderEncrypt%3AU2FsdGVkX1%2B%2BjCdRW1i6MU97dWK%2BesN0urtx%2FTlQfww%3D; rl_trait=RudderEncrypt%3AU2FsdGVkX1%2FSpWljwJu3Ztwkdwe7lRI8uLElevdVY8Y%3D; rl_group_trait=RudderEncrypt%3AU2FsdGVkX1%2FCsJbkX5z6zDBn5ZEspUHCcIxcRMNNkD4%3D; rl_session=RudderEncrypt%3AU2FsdGVkX19FDSKwVmVuRhWT8rEzKmDJ3i%2BF1cEa%2BGiJnDvtx%2Fdipb2fy2%2B7268BbPj%2BXkILRJJK3D8q1d%2BrCEEy%2FZqNkSxyaoDFpTbqBVlIPayomIxDZ9uzSVejUJar27uTxqSIcMqsRreFkIGQ%2BQ%3D%3D; _ga_DMHSW7B1V7=GS2.1.s1776089440$o2$g1$t1776089448$j52$l0$h0; csrf_token=FlH15edHxMC110gNrEXzGC2TDZHtYmKw; AWSALB=rHnTFJTFSRLFdFW+k7XcuWR/9KWHl+w4Y/wqym4yYaeXXPUr4K6fgCLRJJ85ArAMsp92jYjCUU95ZEbsyFIhxXQti9tvYh4/Aj7WcKUD4iqVl8trwuqqOE16bqVR; AWSALBCORS=rHnTFJTFSRLFdFW+k7XcuWR/9KWHl+w4Y/wqym4yYaeXXPUr4K6fgCLRJJ85ArAMsp92jYjCUU95ZEbsyFIhxXQti9tvYh4/Aj7WcKUD4iqVl8trwuqqOE16bqVR)

------------------------------------------------------------------------

## 🎯 Business Value

Ensures that users can securely authenticate and obtain session tokens,
enabling access to protected API resources and supporting end-to-end
authenticated workflows.

------------------------------------------------------------------------

## 🧪 Acceptance Criteria

``` gherkin
Feature: User token login

  Scenario: Successful login with valid credentials and CSRF token
    Given a valid username "default"
    And a valid password "12345678"
    And a valid CSRF token "FlH15edHxMC110gNrEXzGC2TDZHtYmKw"
    And the login API endpoint "/api/users/token/login?set_cookie=true"
    When the user sends a POST request
    Then the response status should be 200
    And a session token or authentication cookie should be returned
```

------------------------------------------------------------------------

## ⚠️ Notes

-   CSRF token must match backend expectation
-   Cookies may be required depending on environment configuration
-   Suitable for API automation testing
