package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.client.BookingApiClient;
import com.automation.pojo.Booking;
import com.automation.pojo.BookingDates;
import com.automation.pojo.BookingResponse;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test 2 - creates a booking via POST /booking and verifies both the
 * generated id and the echoed booking payload.
 */
public class CreateBookingTest extends BaseTest {

    private BookingApiClient client;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        client = new BookingApiClient(requestSpec);
    }

    @Test(description = "POST /booking should create a booking and echo back the submitted details")
    public void createBookingShouldReturnNewBookingIdAndEchoedPayload() {
        Booking requestBody = Booking.builder()
                .firstname("John")
                .lastname("Doe")
                .totalprice(150)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin("2025-01-01")
                        .checkout("2025-01-05")
                        .build())
                .additionalneeds("Breakfast")
                .build();

        Response response = client.createBooking(requestBody);
        assertEquals(response.getStatusCode(), 200, "Expected booking creation to succeed");

        BookingResponse body = response.as(BookingResponse.class);
        assertTrue(body.getBookingid() > 0, "Expected a positive generated bookingid");

        Booking created = body.getBooking();
        assertEquals(created.getFirstname(), requestBody.getFirstname());
        assertEquals(created.getLastname(), requestBody.getLastname());
        assertEquals(created.getTotalprice(), requestBody.getTotalprice());
        assertEquals(created.isDepositpaid(), requestBody.isDepositpaid());
        assertEquals(created.getAdditionalneeds(), requestBody.getAdditionalneeds());
        assertEquals(created.getBookingdates().getCheckin(), requestBody.getBookingdates().getCheckin());
        assertEquals(created.getBookingdates().getCheckout(), requestBody.getBookingdates().getCheckout());
    }
}
