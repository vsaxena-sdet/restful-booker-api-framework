package com.automation.base;

import com.automation.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

/**
 * Shared setup for every test class. Configures REST Assured once per suite
 * (base URI + a default request specification) so individual test classes
 * only express intent via {@link com.automation.client.BookingApiClient},
 * never raw connection details.
 */
public abstract class BaseTest {

    protected static RequestSpecification requestSpec;

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        RestAssured.baseURI = ConfigManager.baseUri();

        // NOTE: intentionally not using setAccept(ContentType.JSON) here - REST Assured
        // expands that to "application/json, application/javascript, text/javascript, text/json",
        // and the Restful-Booker demo API's body-parser rejects that multi-value Accept
        // header with an unexpected 418 "I'm a teapot". A plain "application/json" works.
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json");

        if (ConfigManager.logRequests()) {
            builder.addFilter(new RequestLoggingFilter());
            builder.addFilter(new ResponseLoggingFilter());
        }

        requestSpec = builder.build();
    }
}
