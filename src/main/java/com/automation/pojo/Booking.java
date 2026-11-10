package com.automation.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request/response payload for the Restful-Booker {@code /booking} resource.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;
}
