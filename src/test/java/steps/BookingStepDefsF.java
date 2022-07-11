package steps;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.FilterableRequestSpecification;
import utils.RequestProcessor;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jsonRequestType.BookingRequestType;
import utils.Constants;
import utils.DataMapperForPOJO;
import utils.DataTableUtils;

import java.util.*;

import static io.restassured.RestAssured.requestSpecification;
import static org.junit.Assert.*;
import static utils.DataTableUtils.updateValues;

public class BookingStepDefsF {
    private static FilterableRequestSpecification filterReq;
    RequestSpecification reqSpec;
    RequestProcessor requestProcessor = new RequestProcessor();
    BookingRequestType reqType = new BookingRequestType();
    Response response;
    Map<String, String> expectedTestData;
    String bookingID, authToken = "";
    List<String> expectedBookingIds = new ArrayList<>();

    @Given("I verify Booking API {string} response {int}")
    public void i_verify_booking_api_is_active(String path, int statusCode) {
        RequestSpecification reqSpec = requestProcessor.getCommonSpec(path);
        response = requestProcessor.sendRequest(reqSpec, Constants.RequestType.GET_REQUEST, null);
        assertEquals("PING Health Check Failed.\nRESPONSE: \n" + response.getBody().prettyPrint(), statusCode, response.getStatusCode());
    }

    @And("I set the BASE_URI with path {string}")
    public void i_set_the_baseuri_with_path_something(String path) {
        reqSpec = requestProcessor.getCommonSpec(path);
        authToken = "token=" + requestProcessor.getAuthorizationToken();
    }

    @When("I create {int} booking")
    public void i_create_booking(int count, DataTable table) {
        for (int ctr = 0; ctr < count; ctr++) {
            String bookingIDs = createBooking(table);
            if (Objects.nonNull(bookingIDs)) {
                expectedBookingIds.add(bookingIDs);
            }
        }
    }

    @And("I verify deleted bookingId not present in getAll")
    public void i_verify_deleted_bookingid_not_present_in_getall() {
        assertFalse("BookingID value [" + bookingID + "] is FOUND in actual response list!", response.getBody().jsonPath().getList("bookingid").contains(bookingID));
    }

    @When("I create booking")
    public void i_create_booking(DataTable table) {
        setPOJODataValue(table);
        response = requestProcessor.sendRequest(reqSpec, Constants.RequestType.POST_REQUEST, reqType);
    }

    @When("I send update booking request")
    public void i_update_booking(DataTable table) {
        setPOJODataValue(table);
        response = requestProcessor.sendRequest(reqSpec, Constants.RequestType.PUT_REQUEST, reqType);
    }

    @When("I send GetAll Booking API")
    public void i_send_getall_booking_api() {
        resetRequest();
        response = requestProcessor.sendRequest(reqSpec, Constants.RequestType.GET_REQUEST, reqType);
    }

    private void resetRequest() {
        reqSpec.basePath("");
    }

    @When("I send GetBy BookingId API")
    public void i_send_getById_booking_api() {
        response = requestProcessor.sendRequest(reqSpec, Constants.RequestType.GET_REQUEST, reqType);
    }

    @When("I send Delete Booking API request")
    public void i_send_delete_booking_api() {
        response = requestProcessor.sendRequest(reqSpec, Constants.RequestType.DELETE_REQUEST, reqType);
    }

    @Given("I set the {string} header")
    public void i_set_the_something_header(String header) {
        reqSpec = reqSpec.headers(header, authToken).basePath(bookingID);
    }

    @When("I send partial update booking request")
    public void i_partial_update_booking(DataTable table) {
        setPOJODataValue(table);
        response = requestProcessor.sendRequest(reqSpec, Constants.RequestType.PATCH_REQUEST, reqType);
    }

    @Then("I verify response code is {int}")
    public void i_verify_response_code_is_something(int statusCode) {
        assertEquals("Booking Creation Failed.\nRESPONSE: \n" + response.getBody().prettyPrint(), statusCode, response.getStatusCode());
    }

    void setPOJODataValue(DataTable table) {
        expectedTestData = new HashMap<>();
        expectedTestData = DataTableUtils.getValueMap(table);
        updateValues(expectedTestData, "VALID", null);
        DataMapperForPOJO.createMapping(expectedTestData, reqType);
    }

    String createBooking(DataTable table) {
        setPOJODataValue(table);
        response = requestProcessor.sendRequest(reqSpec, Constants.RequestType.POST_REQUEST, reqType);
        bookingID = response.getBody().jsonPath().get("bookingid").toString();
        return bookingID;
    }

    @And("I verify response body for {string} booking")
    public void i_verify_response_body_for_created_booking(String reqType) {
        switch (reqType) {
            case "created":
                expectedTestData.keySet().forEach(key -> {
                    if (key.contains("check")) {
                        assertEquals("[[FAILED]] : !\n" + "ACTUAL: " + response.jsonPath().get("booking.bookingdates." + key).toString() + "\nEXPECTED: " + expectedTestData.get(key), expectedTestData.get(key), response.jsonPath().get("booking.bookingdates." + key).toString());
                    } else {
                        assertEquals("[[FAILED]] : !\n" + "ACTUAL: " + response.jsonPath().get("booking." + key).toString() + "\nEXPECTED: " + expectedTestData.get(key), expectedTestData.get(key), response.jsonPath().get("booking." + key).toString());
                    }
                });

                break;
            case "partialUpdate":
                assertEquals("[[FAILED]] : !\n" + "ACTUAL: " + response.jsonPath().get("firstname").toString() + "\nEXPECTED: " + expectedTestData.get("firstname"), expectedTestData.get("firstname"), response.jsonPath().get("firstname").toString());
            default:
                expectedTestData.keySet().forEach(key -> {
                    if (key.contains("check")) {
                        assertEquals("[[FAILED]] : !\n" + "ACTUAL: " + response.jsonPath().get("bookingdates." + key).toString() + "\nEXPECTED: " + expectedTestData.get(key), expectedTestData.get(key), response.jsonPath().get("bookingdates." + key).toString());
                    } else {
                        assertEquals("[[FAILED]] : !\n" + "ACTUAL: " + response.jsonPath().get(key).toString() + "\nEXPECTED: " + expectedTestData.get(key), expectedTestData.get(key), response.jsonPath().get(key).toString());
                    }
                });
                break;
        }
    }
}
