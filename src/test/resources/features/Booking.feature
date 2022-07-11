Feature: Booking API

  Background:
    Given I verify Booking API "ping" response 201
    And I set the BASE_URI with path "booking"

  Scenario: Create new booking with all fields
    When I create 1 booking
      | firstname       | ALPHANUM  |
      | lastname        | ALPHANUM  |
      | totalprice      | NUMBERS   |
      | depositpaid     | true      |
      | checkin         | DATE      |
      | checkout        | DATE      |
      | additionalneeds | ALPHABETS |
    Then I verify response code is 200
    And I verify response body for "created" booking


  Scenario: Create multiple booking for customer and verify in GetAll without filter
    When I create 10 booking
      | firstname       | ALPHANUM  |
      | lastname        | ALPHANUM  |
      | totalprice      | NUMBERS   |
      | depositpaid     | true      |
      | checkin         | DATE      |
      | checkout        | DATE      |
      | additionalneeds | ALPHABETS |
    Then I verify response code is 200
    When I send GetAll Booking API
    Then I verify response code is 200

  Scenario: Delete a booking and verify in GetAll booking
    When I create 1 booking
      | firstname       | ALPHANUM  |
      | lastname        | ALPHANUM  |
      | totalprice      | NUMBERS   |
      | depositpaid     | true      |
      | checkin         | DATE      |
      | checkout        | DATE      |
      | additionalneeds | ALPHABETS |
    Then I verify response code is 200
    Given I set the "Cookie" header
    When I send Delete Booking API request
    Then I verify response code is 201
    When I send GetAll Booking API
    Then I verify response code is 200
    And I verify deleted bookingId not present in getAll

  Scenario: Create booking for customer and verify in GetBy BookingId
    When I create 1 booking
      | firstname       | ALPHANUM  |
      | lastname        | ALPHANUM  |
      | totalprice      | NUMBERS   |
      | depositpaid     | true      |
      | checkin         | DATE      |
      | checkout        | DATE      |
      | additionalneeds | ALPHABETS |
    Then I verify response code is 200
    Given I set the "Cookie" header
    When I send GetBy BookingId API
    Then I verify response code is 200
    And I verify response body for "getById" booking


  Scenario: Create new booking without optional field-additionalneeds
    When I create 1 booking
      | firstname   | ALPHANUM |
      | lastname    | ALPHANUM |
      | totalprice  | NUMBERS  |
      | depositpaid | true     |
      | checkin     | DATE     |
      | checkout    | DATE     |
    Then I verify response code is 200
    And I verify response body for "created" booking


  Scenario Outline: Create new booking fails without mandatory fields
    When I create booking
      | firstname   | <firstname>   |
      | lastname    | <lastname>    |
      | totalprice  | <totalprice>  |
      | depositpaid | <depositpaid> |
      | checkin     | <checkin>     |
      | checkout    | <checkout>    |
    Then I verify response code is 500

    Examples:
      | firstname | lastname | totalprice | depositpaid | checkin | checkout |
      | NULL      | ALPHANUM | NUMBERS    | true        | DATE    | DATE     |
      | ALPHANUM  | NULL     | NUMBERS    | true        | DATE    | DATE     |
      | ALPHANUM  | ALPHANUM | NULL       | true        | DATE    | DATE     |
      | ALPHANUM  | ALPHANUM | NUMBERS    | NULL        | DATE    | DATE     |
      | ALPHANUM  | ALPHANUM | NUMBERS    | true        | NULL    | DATE     |
      | ALPHANUM  | ALPHANUM | NUMBERS    | true        | DATE    | NULL     |


  Scenario: Update existing booking
    When I create 1 booking
      | firstname       | ALPHANUM  |
      | lastname        | ALPHANUM  |
      | totalprice      | NUMBERS   |
      | depositpaid     | true      |
      | checkin         | DATE      |
      | checkout        | DATE      |
      | additionalneeds | ALPHABETS |
    Then I verify response code is 200
    Given I set the "Cookie" header
    When I send update booking request
      | firstname       | ALPHANUM  |
      | lastname        | ALPHANUM  |
      | totalprice      | NUMBERS   |
      | depositpaid     | true      |
      | checkin         | DATE      |
      | checkout        | DATE      |
      | additionalneeds | ALPHABETS |
    Then I verify response code is 200
    And I verify response body for "update" booking


  Scenario: Partial Update existing booking
    When I create 1 booking
      | firstname       | ALPHANUM  |
      | lastname        | ALPHANUM  |
      | totalprice      | NUMBERS   |
      | depositpaid     | true      |
      | checkin         | DATE      |
      | checkout        | DATE      |
      | additionalneeds | ALPHABETS |
    Then I verify response code is 200
    Given I set the "Cookie" header
    When I send partial update booking request
      | firstname | ALPHANUM |
    Then I verify response code is 200
    And I verify response body for "partialUpdate" booking
