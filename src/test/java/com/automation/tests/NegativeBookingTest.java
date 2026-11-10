package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.client.BookingApiClient;
import com.automation.pojo.Booking;
import com.automation.pojo.BookingDates;
import com.automation.pojo.BookingResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import com.automation.annotations.Layer;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Three deliberately failing tests that exercise real API calls but assert
 * incorrect values so failures appear in the Allure report with meaningful
 * context — useful for validating CI/report pipelines.
 *
 *  1. getNonExistentBookingShouldReturn200
 *     – GETs a booking id that does not exist (999_999_999).
 *       The API correctly returns 404; the test incorrectly expects 200.
 *
 *  2. createBookingShouldHaveWrongFirstname
 *     – Creates a real booking for "Alice" then asserts the firstname is
 *       "Bob". The API echoes back "Alice"; the assertion must fail.
 *
 *  3. healthCheckShouldReturn200
 *     – Hits GET /ping (the API's own health-check endpoint).
 *       Restful-Booker intentionally returns 201 Created; the test
 *       incorrectly expects 200 OK.
 */
@Epic("Restful Booker API")
@Feature("Booking Validation")
@Layer("integration")
@Owner("qa-team")
public class NegativeBookingTest extends BaseTest {

    private BookingApiClient client;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        client = new BookingApiClient(requestSpec);
    }

    // -------------------------------------------------------------------------
    // TC-N1: fetching a non-existent booking id should return 200 [WILL FAIL]
    // The API returns 404 for an id that does not exist. This assertion is
    // intentionally wrong to demonstrate a failure in the Allure report.
    // -------------------------------------------------------------------------
    @Test(description = "[INTENTIONAL FAILURE] GET /booking/{id} for a non-existent id should return 200 — expects 200 but API returns 404")
    @Story("Invalid Booking ID Handling")
    @Severity(SeverityLevel.NORMAL)
    public void getNonExistentBookingShouldReturn200() {
        Response response = client.getBooking(999_999_999);

        assertEquals(
                response.getStatusCode(),
                200,  // Wrong: the real response is 404
                "Expected 200 for non-existent booking id 999999999, but API returned " + response.getStatusCode()
        );
    }

    // -------------------------------------------------------------------------
    // TC-N2: created booking should have a different firstname [WILL FAIL]
    // A booking is created for "Alice"; the assertion checks for "Bob".
    // This demonstrates a payload mismatch failure scenario.
    // -------------------------------------------------------------------------
    @Test(description = "[INTENTIONAL FAILURE] POST /booking response should echo firstname 'Bob' — expects 'Bob' but booking was created with 'Alice'")
    @Story("Create Booking Payload Validation")
    @Severity(SeverityLevel.MINOR)
    public void createBookingShouldHaveWrongFirstname() {
        Booking requestBody = Booking.builder()
                .firstname("Alice")
                .lastname("Wonderland")
                .totalprice(300)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin("2025-06-01")
                        .checkout("2025-06-07")
                        .build())
                .additionalneeds("None")
                .build();

        Response response = client.createBooking(requestBody);
        assertEquals(response.getStatusCode(), 200, "Booking creation must succeed for the assertion below to run");

        BookingResponse body = response.as(BookingResponse.class);

        assertEquals(
                body.getBooking().getFirstname(),
                "Bob",   // Wrong: API echoes back "Alice"
                "Expected firstname 'Bob' but API echoed back '" + body.getBooking().getFirstname() + "'"
        );
    }

    // -------------------------------------------------------------------------
    // TC-N3: health-check endpoint should return 200 [WILL FAIL]
    // GET /ping on Restful-Booker intentionally returns 201 Created to signal
    // it is alive. This test asserts 200 to demonstrate a status-code failure.
    // -------------------------------------------------------------------------
    @Test(description = "[INTENTIONAL FAILURE] GET /ping should return 200 — expects 200 but Restful-Booker API deliberately returns 201")
    @Story("API Status Code Validation")
    @Severity(SeverityLevel.MINOR)
    public void healthCheckShouldReturn200() {
        Response response = client.ping();

        assertEquals(
                response.getStatusCode(),
                200,  // Wrong: the real response is 201 Created
                "Expected health-check status 200 but API returned " + response.getStatusCode()
        );
    }
}
