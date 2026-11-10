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

import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Two tests that pass or fail non-deterministically across runs — useful for
 * exercising Allure's trend chart, flaky-test detection, and retry history.
 *
 * <p><b>TC-F1 — Random even-price assertion</b><br>
 * Creates a booking whose {@code totalprice} is chosen randomly at runtime
 * (1–200). The assertion then checks that the echoed price is an even number.
 * Because exactly half of integers are even, this test passes ~50 % of runs
 * and fails ~50 %, making the random behaviour very visible in the report.
 *
 * <p><b>TC-F2 — Burst response-time SLA</b><br>
 * Fires 5 rapid GET /ping calls back-to-back and asserts that every single
 * one completes within {@value #PING_SLA_MS} ms. The Restful-Booker API is
 * hosted on a free Heroku dyno; cold starts, GC pauses and network jitter
 * make at least one of the 5 calls exceed the threshold on a significant
 * fraction of runs, so the result varies genuinely with infrastructure state.
 */
@Epic("Restful Booker API")
@Feature("API Reliability")
@Layer("e2e")
@Owner("qa-team")
public class FlakyBookingTest extends BaseTest {

    /** Per-call SLA for the burst ping test (TC-F2).
     *  Restful-Booker typically responds in 600–750 ms; this threshold sits
     *  in the middle of that range so the result varies with dyno/network state. */
    private static final long PING_SLA_MS = 700;

    /** Number of back-to-back pings in TC-F2. */
    private static final int PING_BURST = 5;

    private BookingApiClient client;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        client = new BookingApiClient(requestSpec);
    }

    // -------------------------------------------------------------------------
    // TC-F1: Random even-price — ~50 % pass rate across runs
    // -------------------------------------------------------------------------
    @Test(description =
            "[FLAKY ~50%] Creates a booking with a random totalprice (1–200) " +
            "and asserts the price is even. Passes on even rolls, fails on odd.")
    @Story("Booking Price Consistency")
    @Severity(SeverityLevel.MINOR)
    public void bookingWithRandomPriceShouldBeEven() {
        // Pick a random price in [1, 200]; exactly half are even, half odd.
        int price = ThreadLocalRandom.current().nextInt(1, 201);

        Booking booking = Booking.builder()
                .firstname("Random")
                .lastname("PriceTest")
                .totalprice(price)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin("2025-09-01")
                        .checkout("2025-09-05")
                        .build())
                .additionalneeds("None")
                .build();

        Response createResp = client.createBooking(booking);
        assertEquals(createResp.getStatusCode(), 200,
                "Booking creation must succeed for the flaky assertion to run");

        int echoedPrice = createResp.as(BookingResponse.class)
                                    .getBooking()
                                    .getTotalprice();

        // The flaky assertion: even price → pass, odd price → fail.
        assertEquals(echoedPrice % 2, 0,
                String.format(
                        "Flaky assertion: expected totalprice %d to be even " +
                        "(randomly rolled %d — odd numbers fail this test).",
                        echoedPrice, price));
    }

    // -------------------------------------------------------------------------
    // TC-F2: Burst ping SLA — flaky based on live Heroku dyno latency
    // -------------------------------------------------------------------------
    @Test(description =
            "[FLAKY] Fires " + PING_BURST + " back-to-back GET /ping calls and " +
            "asserts every one completes within " + PING_SLA_MS + " ms. " +
            "Fails when any single call is slow (Heroku cold start, GC, jitter).")
    @Story("API Response Time SLA")
    @Severity(SeverityLevel.NORMAL)
    public void allBurstPingsShouldCompleteWithinSla() {
        long[] timings = new long[PING_BURST];
        int[] statuses = new int[PING_BURST];
        int firstBreachIndex = -1;
        long firstBreachMs = 0;

        for (int i = 0; i < PING_BURST; i++) {
            long start = System.currentTimeMillis();
            Response resp = client.ping();
            long elapsed = System.currentTimeMillis() - start;

            statuses[i] = resp.getStatusCode();
            timings[i] = elapsed;

            if (firstBreachIndex == -1 && elapsed >= PING_SLA_MS) {
                firstBreachIndex = i;
                firstBreachMs = elapsed;
            }
        }

        // Build a summary string for the assertion message.
        StringBuilder summary = new StringBuilder("Ping timings (ms): ");
        for (int i = 0; i < PING_BURST; i++) {
            summary.append(String.format("[call %d: %d ms / HTTP %d]%s",
                    i + 1, timings[i], statuses[i], i < PING_BURST - 1 ? ", " : ""));
        }

        assertTrue(firstBreachIndex == -1,
                String.format(
                        "SLA breach on call %d: took %d ms (threshold = %d ms). %s",
                        firstBreachIndex + 1, firstBreachMs, PING_SLA_MS, summary));
    }
}
