
package com.hourlyhire.workmanagement.booking.service;

import com.hourlyhire.workmanagement.booking.dto.request.BookingRequest;
import com.hourlyhire.workmanagement.booking.dto.response.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    BookingResponse getBookingById(Long bookingId);

    List<BookingResponse> getAllBookings();

    BookingResponse updateBooking(Long bookingId, BookingRequest request);

    void deleteBooking(Long bookingId);
    BookingResponse acceptBooking(Long bookingId);

    BookingResponse rejectBooking(Long bookingId);
    // Get all bookings assigned to the authenticated worker
    List<BookingResponse> getMyBookings();
    BookingResponse cancelBooking(Long bookingId);
}
