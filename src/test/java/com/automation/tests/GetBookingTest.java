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

/**
 * Test 3 - fetches a previously created booking by id and verifies the
 * returned fields match what was submitted. The fixture booking is created
 * in {@code @BeforeClass} so this test is self-contained and does not
 * depend on test execution order against other classes.
 */
public class GetBookingTest extends BaseTest {

    private BookingApiClient client;
    private int bookingId;
    private Booking fixtureBooking;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        client = new BookingApiClient(requestSpec);

        fixtureBooking = Booking.builder()
                .firstname("Jane")
                .lastname("Smith")
                .totalprice(200)
                .depositpaid(false)
                .bookingdates(BookingDates.builder()
                        .checkin("2025-02-10")
                        .checkout("2025-02-15")
                        .build())
                .additionalneeds("Late checkout")
                .build();

        Response createResponse = client.createBooking(fixtureBooking);
        assertEquals(createResponse.getStatusCode(), 200, "Fixture booking creation must succeed before this test can run");
        bookingId = createResponse.as(BookingResponse.class).getBookingid();
    }

    @Test(description = "GET /booking/{id} should return the previously created booking")
    public void getBookingByIdShouldReturnMatchingBooking() {
        Response response = client.getBooking(bookingId);
        assertEquals(response.getStatusCode(), 200, "Expected booking lookup to succeed for id " + bookingId);

        Booking fetched = response.as(Booking.class);
        assertEquals(fetched.getFirstname(), fixtureBooking.getFirstname());
        assertEquals(fetched.getLastname(), fixtureBooking.getLastname());
        assertEquals(fetched.getTotalprice(), fixtureBooking.getTotalprice());
        assertEquals(fetched.isDepositpaid(), fixtureBooking.isDepositpaid());
        assertEquals(fetched.getAdditionalneeds(), fixtureBooking.getAdditionalneeds());
        assertEquals(fetched.getBookingdates().getCheckin(), fixtureBooking.getBookingdates().getCheckin());
        assertEquals(fetched.getBookingdates().getCheckout(), fixtureBooking.getBookingdates().getCheckout());
    }
}
