package com.automation.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response envelope returned by {@code POST /booking}:
 * {@code { "bookingid": 1, "booking": { ... } } }.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private int bookingid;
    private Booking booking;
}
