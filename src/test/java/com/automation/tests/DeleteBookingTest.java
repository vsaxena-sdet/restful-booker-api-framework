package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.client.BookingApiClient;
import com.automation.config.ConfigManager;
import com.automation.pojo.AuthResponse;
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
 * Test 5 - authenticates, deletes a booking via DELETE /booking/{id}, then
 * confirms it is actually gone with a follow-up GET returning 404.
 */
@Epic("Restful Booker API")
@Feature("Booking Management")
@Layer("e2e")
@Owner("qa-team")
public class DeleteBookingTest extends BaseTest {

    private BookingApiClient client;
    private int bookingId;
    private String authToken;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        client = new BookingApiClient(requestSpec);

        Response tokenResponse = client.createToken(ConfigManager.authUsername(), ConfigManager.authPassword());
        assertEquals(tokenResponse.getStatusCode(), 200, "Expected authentication to succeed before deleting a booking");
        authToken = tokenResponse.as(AuthResponse.class).getToken();

        Booking fixtureBooking = Booking.builder()
                .firstname("Temp")
                .lastname("ToDelete")
                .totalprice(50)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin("2025-04-01")
                        .checkout("2025-04-02")
                        .build())
                .additionalneeds("None")
                .build();

        Response createResponse = client.createBooking(fixtureBooking);
        assertEquals(createResponse.getStatusCode(), 200, "Fixture booking creation must succeed before this test can run");
        bookingId = createResponse.as(BookingResponse.class).getBookingid();
    }

    @Test(description = "DELETE /booking/{id} should remove the booking")
    @Story("Delete Booking")
    @Severity(SeverityLevel.NORMAL)
    public void deleteBookingShouldRemoveItPermanently() {
        Response deleteResponse = client.deleteBooking(bookingId, authToken);
        assertEquals(deleteResponse.getStatusCode(), 201, "Expected booking deletion to succeed for id " + bookingId);

        Response getResponse = client.getBooking(bookingId);
        assertEquals(getResponse.getStatusCode(), 404, "Expected deleted booking id " + bookingId + " to no longer be retrievable");
    }
}
