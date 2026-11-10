package com.automation.client;

import com.automation.pojo.AuthRequest;
import com.automation.pojo.Booking;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper around the Restful-Booker endpoints used by this framework.
 * Test classes talk to this client instead of building raw REST Assured
 * calls, so request construction lives in exactly one place and tests stay
 * focused on behaviour and assertions.
 */
public class BookingApiClient {

    private final RequestSpecification spec;

    public BookingApiClient(RequestSpecification spec) {
        this.spec = spec;
    }

    /** GET /ping - health check endpoint, expected to return 201 when the API is up. */
    public Response ping() {
        return given().spec(spec)
                .when()
                .get("/ping");
    }

    /** POST /auth - exchanges credentials for a token used by write/delete operations. */
    public Response createToken(String username, String password) {
        AuthRequest authRequest = AuthRequest.builder()
                .username(username)
                .password(password)
                .build();

        return given().spec(spec)
                .body(authRequest)
                .when()
                .post("/auth");
    }

    /** POST /booking - creates a new booking and returns its id + echoed payload. */
    public Response createBooking(Booking booking) {
        return given().spec(spec)
                .body(booking)
                .when()
                .post("/booking");
    }

    /** GET /booking - returns the list of all booking ids currently on the server. */
    public Response getBookingIds() {
        return given().spec(spec)
                .when()
                .get("/booking");
    }

    /** GET /booking/{id} - fetches a single booking by id. */
    public Response getBooking(int bookingId) {
        return given().spec(spec)
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}");
    }

    /** PUT /booking/{id} - fully replaces a booking; requires an auth token cookie. */
    public Response updateBooking(int bookingId, Booking booking, String token) {
        return given().spec(spec)
                .cookie("token", token)
                .pathParam("id", bookingId)
                .body(booking)
                .when()
                .put("/booking/{id}");
    }

    /** DELETE /booking/{id} - removes a booking; requires an auth token cookie. */
    public Response deleteBooking(int bookingId, String token) {
        return given().spec(spec)
                .cookie("token", token)
                .pathParam("id", bookingId)
                .when()
                .delete("/booking/{id}");
    }
}
