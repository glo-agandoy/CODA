# booking-flow.feature
# Template: Complete chained booking flow for {AIRLINE_NAME}
# This file demonstrates the canonical pattern for multi-step API flows.
# Adapt: endpoint paths, payload fields, schema definitions, and IATA placeholders.

@epic=FlightBooking @feature=BookingFlow
Feature: Booking Flow API

  Background:
    * url baseUrl
    # Auth token — injected from karate-config.js (already acquired via callSingle)
    * header Authorization = 'Bearer ' + authToken
    # Test data — read from environment; concrete values from airline-domain config
    * def origin      = testOriginIata
    * def destination = testDestIata
    # T+7 departure, T+14 return — never use hardcoded dates
    * def departDate  = call read('classpath:helpers/data-factory.js') { offset: 7 }
    * def returnDate  = call read('classpath:helpers/data-factory.js') { offset: 14 }
    # Passenger template — adapt type codes to match airline-domain pax types
    * def adultPax =
      """
      {
        "type": "ADT",
        "firstName": "Test",
        "lastName": "Automation",
        "dateOfBirth": "1985-06-15",
        "documentType": "PASSPORT",
        "documentNumber": "AUTO001"
      }
      """
    # Schemas — define once in Background, reuse in assertions
    * def flightSchema =
      """
      {
        "id": "#string",
        "origin": "#string",
        "destination": "#string",
        "departureDate": "#string",
        "departureTime": "#string",
        "arrivalTime": "#string",
        "fares": "#[] #present",
        "availableSeats": "#number"
      }
      """
    * def bookingSchema =
      """
      {
        "id": "#string",
        "bookingReference": "#string",
        "status": "#string",
        "passengers": "#[] #present",
        "totalAmount": "#number",
        "currency": "#string",
        "createdAt": "#string"
      }
      """

  # ---------------------------------------------------------------------------
  # HAPPY PATH — complete booking funnel
  # ---------------------------------------------------------------------------

  @story=OneWayBooking @severity=blocker @smoke
  Scenario: Create a one-way booking and confirm payment

    # --- Step 1: Search for available flights ---
    Given path '/v1/flights/search'
    And params
      """
      {
        "origin": "#(origin)",
        "destination": "#(destination)",
        "departureDate": "#(departDate)",
        "adults": 1,
        "children": 0,
        "infants": 0
      }
      """
    When method GET
    Then status 200
    And match response.flights == '#[_ > 0]'
    And match response.flights[0] == flightSchema
    # Capture first available flight and cheapest fare
    * def flight     = response.flights[0]
    * def selectedFare = flight.fares[0]

    # --- Step 2: Create booking ---
    Given path '/v1/bookings'
    And request
      """
      {
        "outbound": {
          "flightId": "#(flight.id)",
          "fareCode": "#(selectedFare.code)"
        },
        "passengers": ["#(adultPax)"],
        "contactEmail": "test.automation@{airline-id}.com"
      }
      """
    When method POST
    Then status 201
    And match response == bookingSchema
    And match response.status == 'PENDING_PAYMENT'
    * def bookingId  = response.id
    * def bookingRef = response.bookingReference

    # --- Step 3: Process payment ---
    Given path '/v1/bookings/' + bookingId + '/payment'
    And request
      """
      {
        "method": "TOKENIZED_CARD",
        "token": "#(testCardToken)",
        "amount": "#(response.totalAmount)",
        "currency": "#(response.currency)"
      }
      """
    When method POST
    Then status 200
    And match response.paymentStatus == 'APPROVED'
    And match response.bookingStatus == 'CONFIRMED'

    # --- Step 4: Verify booking is confirmed ---
    Given path '/v1/bookings/' + bookingId
    When method GET
    Then status 200
    And match response.status == 'CONFIRMED'
    And match response.bookingReference == bookingRef

    # --- Cleanup: cancel the test booking ---
    * call read('classpath:helpers/cleanup-helper.feature') { bookingId: '#(bookingId)' }

  # ---------------------------------------------------------------------------
  # NEGATIVE — unavailable route
  # ---------------------------------------------------------------------------

  @story=NoAvailability @severity=normal @regression
  Scenario: Search with no availability returns 200 with empty list or 404

    Given path '/v1/flights/search'
    And params
      """
      {
        "origin": "#(origin)",
        "destination": "#(origin)",
        "departureDate": "#(departDate)",
        "adults": 1
      }
      """
    When method GET
    # Airlines differ: some return 200+empty, others return 404
    * def validStatuses = [200, 404]
    And assert validStatuses.contains(responseStatus)
    And if (responseStatus == 200) karate.match(response.flights, '[]')

  # ---------------------------------------------------------------------------
  # NEGATIVE — invalid IATA code
  # ---------------------------------------------------------------------------

  @story=InvalidIATA @severity=normal @regression
  Scenario: Search with invalid IATA code returns 400

    Given path '/v1/flights/search'
    And params { origin: 'INVALID', destination: '#(destination)', departureDate: '#(departDate)', adults: 1 }
    When method GET
    Then status 400
    And match response.error == '#present'
    And match response.error.code == '#string'

  # ---------------------------------------------------------------------------
  # DATA-DRIVEN — multiple pax combinations
  # ---------------------------------------------------------------------------

  @story=PaxCombinations @severity=normal @regression
  Scenario Outline: Search with different passenger combinations returns results

    Given path '/v1/flights/search'
    And params
      """
      {
        "origin": "#(origin)",
        "destination": "#(destination)",
        "departureDate": "#(departDate)",
        "adults": <adults>,
        "children": <children>,
        "infants": <infants>
      }
      """
    When method GET
    Then status 200
    And match response.flights == '#[_ > 0]'

    Examples:
      | adults | children | infants |
      | 1      | 0        | 0       |
      | 2      | 1        | 0       |
      | 1      | 0        | 1       |
