package com.hourlyhire.workmanagement.booking.controller;

import com.hourlyhire.workmanagement.booking.dto.request.BookingRequest;
import com.hourlyhire.workmanagement.booking.dto.response.BookingResponse;
import com.hourlyhire.workmanagement.booking.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "09 Booking Management", description = "APIs for managing worker bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Create booking
    @Operation(summary = "Create a new booking. Access: EMPLOYER",
            description = "Allows an authenticated employer to create a booking for a worker.")
    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response =
                bookingService.createBooking(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get booking by ID
    @Operation(summary = "Get booking by ID. Access:EMPLOYER",
            description = "Retrieves a booking belonging to the authenticated employer.")
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long bookingId) {

        return ResponseEntity.ok(
                bookingService.getBookingById(bookingId));
    }

    // Get all bookings
    @Operation(summary = "Get all employer bookings",
            description = "Retrieves all bookings created by the authenticated employer.")
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {

        return ResponseEntity.ok(
                bookingService.getAllBookings());
    }

    // Update booking
    @Operation(summary = "Update booking",
            description = "Updates the details of an existing employer booking.")
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingRequest request) {

        return ResponseEntity.ok(
                bookingService.updateBooking(bookingId, request));
    }

    // Delete booking
    @Operation(summary = "Delete booking",
            description = "Deletes a booking belonging to the authenticated employer.")
    @PreAuthorize("hasRole('EMPLOYER')")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable Long bookingId) {

        bookingService.deleteBooking(bookingId);

        return ResponseEntity.noContent().build();
    }

    // Worker accepts a booking
    @Operation(
            summary = "Accept booking. Access: WORKER",
            description = "Allows the assigned worker to accept a pending booking."
    )
    @PreAuthorize("hasRole('WORKER')")
    @PutMapping("/{bookingId}/accept")
    public ResponseEntity<BookingResponse> acceptBooking(
            @PathVariable Long bookingId) {

        BookingResponse response =
                bookingService.acceptBooking(bookingId);

        return ResponseEntity.ok(response);
    }

    // Worker rejects a booking
    @Operation(
            summary = "Reject booking. Access: WORKER",
            description = "Allows the assigned worker to reject a pending booking."
    )
    @PreAuthorize("hasRole('WORKER')")
    @PutMapping("/{bookingId}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(
            @PathVariable Long bookingId) {

        BookingResponse response =
                bookingService.rejectBooking(bookingId);

        return ResponseEntity.ok(response);
    }
    // Employer cancels a booking
    @Operation(
            summary = "Cancel booking. Access: EMPLOYER",
            description = "Allows an employer to cancel a pending or accepted booking."
    )
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId) {

        BookingResponse response =
                bookingService.cancelBooking(bookingId);

        return ResponseEntity.ok(response);
    }
    // Get all bookings assigned to the authenticated worker
    @Operation(
            summary = "Get my bookings. Access: WORKER",
            description = "Retrieves all bookings assigned to the authenticated worker."
    )
    @PreAuthorize("hasRole('WORKER')")
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {

        return ResponseEntity.ok(
                bookingService.getMyBookings()
        );
    }

}
