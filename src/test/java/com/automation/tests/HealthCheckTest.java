package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.client.BookingApiClient;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test 1 - confirms the demo API is reachable before any other test runs.
 */
public class HealthCheckTest extends BaseTest {

    private BookingApiClient client;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        client = new BookingApiClient(requestSpec);
    }

    @Test(description = "GET /ping should report the API is up with a 201")
    public void pingShouldReturn201() {
        Response response = client.ping();

        assertEquals(response.getStatusCode(), 201, "Expected /ping to confirm the API is healthy");
    }
}
