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
 * Test 4 - authenticates to obtain a token, then fully replaces a booking
 * via PUT /booking/{id} and verifies the updated fields are reflected.
 */
@Epic("Restful Booker API")
@Feature("Booking Management")
@Layer("e2e")
@Owner("qa-team")
public class UpdateBookingTest extends BaseTest {

    private BookingApiClient client;
    private int bookingId;
    private String authToken;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        client = new BookingApiClient(requestSpec);

        Response tokenResponse = client.createToken(ConfigManager.authUsername(), ConfigManager.authPassword());
        assertEquals(tokenResponse.getStatusCode(), 200, "Expected authentication to succeed before updating a booking");
        authToken = tokenResponse.as(AuthResponse.class).getToken();

        Booking fixtureBooking = Booking.builder()
                .firstname("Alice")
                .lastname("Walker")
                .totalprice(100)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin("2025-03-01")
                        .checkout("2025-03-03")
                        .build())
                .additionalneeds("None")
                .build();

        Response createResponse = client.createBooking(fixtureBooking);
        assertEquals(createResponse.getStatusCode(), 200, "Fixture booking creation must succeed before this test can run");
        bookingId = createResponse.as(BookingResponse.class).getBookingid();
    }

    @Test(description = "PUT /booking/{id} should update the booking details")
    @Story("Update Booking")
    @Severity(SeverityLevel.NORMAL)
    public void updateBookingShouldPersistNewDetails() {
        Booking updatedBooking = Booking.builder()
                .firstname("Alice")
                .lastname("Walker-Smith")
                .totalprice(175)
                .depositpaid(false)
                .bookingdates(BookingDates.builder()
                        .checkin("2025-03-05")
                        .checkout("2025-03-10")
                        .build())
                .additionalneeds("Extra pillow")
                .build();

        Response response = client.updateBooking(bookingId, updatedBooking, authToken);
        assertEquals(response.getStatusCode(), 200, "Expected booking update to succeed for id " + bookingId);

        Booking returned = response.as(Booking.class);
        assertEquals(returned.getLastname(), updatedBooking.getLastname());
        assertEquals(returned.getTotalprice(), updatedBooking.getTotalprice());
        assertEquals(returned.isDepositpaid(), updatedBooking.isDepositpaid());
        assertEquals(returned.getAdditionalneeds(), updatedBooking.getAdditionalneeds());
        assertEquals(returned.getBookingdates().getCheckin(), updatedBooking.getBookingdates().getCheckin());
        assertEquals(returned.getBookingdates().getCheckout(), updatedBooking.getBookingdates().getCheckout());

        // Re-fetch to confirm the update was actually persisted server-side, not just echoed.
        Response getResponse = client.getBooking(bookingId);
        Booking persisted = getResponse.as(Booking.class);
        assertEquals(persisted.getLastname(), updatedBooking.getLastname());
        assertEquals(persisted.getTotalprice(), updatedBooking.getTotalprice());
    }
}
