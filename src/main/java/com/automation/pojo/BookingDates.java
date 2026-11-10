package com.automation.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps to the nested {@code bookingdates} object in the Restful-Booker API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDates {
    private String checkin;
    private String checkout;
}
