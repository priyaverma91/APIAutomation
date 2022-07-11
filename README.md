# payconiq  Assignment

In the assignment following points are covered

**Automated the API endpoint /booking for below scenarios**
  Create a Booking and verify response code and response Body for all fields
  Create Multiple Bookings and verify GetAll api response code
  Verify existing booking gets deleted and not present in GET all response
  Verify GetBy bookingId response for existing booking
  Create booking for customer and verify in GetBy BookingId
  Create new booking without optional field-additionalneeds
  Create new booking fails without mandatory fields
  Update existing booking  
  Partial Update existing booking

## Project Gitlab

PROJECT_CHECKOUT_FOLDER - 

Move to the base of the  i.e, `${PROJECT_CHECKOUT_FOLDER}\ `

git clone 

cd ${PROJECT_CHECKOUT_FOLDER}\

git pull

## Structure
```Gherkin
src
  + main
    + java
      + env                      Application Properties and Resource path reader class
      + utilities                Support File operation and constant classes
  + test
    + java                        
      + jsonRequestType           API request POJO classes
      + runner                    Test runners
      + step                      Step Definition
      + utils                     Support Classes
    + resources
      + features                  Feature files
      + properties                Application Config file
  
   
```
Instructions to run test locally :-

## Build

Run below command to build the project in IDE terminal
```sh
$ mvn clean install
```

## Run Cucumber Tests

To run all tests in local and get Serenity test report
        
     -mvn clean verify

    - target/site/serenity -Serenity report path

    - target/site/serenity/index.html - index.html will be genareted with all test cases

## Integrated with Gitlab CI

To open Serenity Report in Gitlab CI job
- Click browse on the job to access the Serenity report
- Go to target/site/serenity/index.html


**.gitlab-ci.yml**
image: circleci/openjdk:8-jdk-stretch

cache:
paths:
- ./m2/repository/
- ./target/failsafe-reports/
- ./target/site/serenity/

build:
stage: build
script:
- mvn dependency:go-offline

test:
stage: test
script:
- mvn clean verify




