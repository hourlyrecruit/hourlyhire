
package com.hourlyhire.workmanagement.booking.service.impl;

import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.security.jwt.AuthenticatedUser;
import com.hourlyhire.workforce.entity.EmployerProfile;
import com.hourlyhire.workforce.entity.WorkerProfile;
import com.hourlyhire.workforce.repository.EmployerProfileRepository;
import com.hourlyhire.workforce.repository.WorkerProfileRepository;
import com.hourlyhire.workmanagement.booking.dto.request.BookingRequest;
import com.hourlyhire.workmanagement.booking.dto.response.BookingResponse;
import com.hourlyhire.workmanagement.booking.entity.Booking;
import com.hourlyhire.workmanagement.booking.enums.BookingStatus;
import com.hourlyhire.workmanagement.booking.mapper.BookingMapper;
import com.hourlyhire.workmanagement.booking.repository.BookingRepository;
import com.hourlyhire.workmanagement.booking.service.BookingService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            EmployerProfileRepository employerProfileRepository,
            WorkerProfileRepository workerProfileRepository,
            BookingMapper bookingMapper) {

        this.bookingRepository = bookingRepository;
        this.employerProfileRepository = employerProfileRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.bookingMapper = bookingMapper;
    }

    private EmployerProfile getAuthenticatedEmployer() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        if (user.role() != Role.EMPLOYER) {
            throw new AccessDeniedException(
                    "Only employers can manage bookings");
        }

        return employerProfileRepository.findByUserAccount_Id(user.id())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employer profile not found"));
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {

        EmployerProfile employer = getAuthenticatedEmployer();

        WorkerProfile worker = workerProfileRepository
                .findById(request.getWorkerProfileId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Worker profile not found"));

        Booking booking = new Booking();

        booking.setEmployerProfile(employer);
        booking.setWorkerProfile(worker);
        booking.setJobTitle(request.getJobTitle());
        booking.setDescription(request.getDescription());
        booking.setWorkDate(request.getWorkDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setLocation(request.getLocation());
        booking.setHourlyRate(request.getHourlyRate());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {

        EmployerProfile employer = getAuthenticatedEmployer();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found: " + bookingId));

        if (!booking.getEmployerProfile().getId()
                .equals(employer.getId())) {
            throw new AccessDeniedException(
                    "You cannot access this booking");
        }

        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {

        EmployerProfile employer = getAuthenticatedEmployer();

        return bookingRepository
                .findAllByEmployerProfile_Id(employer.getId())
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    public BookingResponse updateBooking(
            Long bookingId,
            BookingRequest request) {

        EmployerProfile employer = getAuthenticatedEmployer();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found: " + bookingId));

        if (!booking.getEmployerProfile().getId()
                .equals(employer.getId())) {
            throw new AccessDeniedException(
                    "You cannot update this booking");
        }

        WorkerProfile worker = workerProfileRepository
                .findById(request.getWorkerProfileId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Worker profile not found"));

        booking.setWorkerProfile(worker);
        booking.setJobTitle(request.getJobTitle());
        booking.setDescription(request.getDescription());
        booking.setWorkDate(request.getWorkDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setLocation(request.getLocation());
        booking.setHourlyRate(request.getHourlyRate());

        Booking updatedBooking = bookingRepository.save(booking);

        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    public void deleteBooking(Long bookingId) {

        EmployerProfile employer = getAuthenticatedEmployer();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Booking not found: " + bookingId));

        if (!booking.getEmployerProfile().getId()
                .equals(employer.getId())) {
            throw new AccessDeniedException(
                    "You cannot delete this booking");
        }

        bookingRepository.delete(booking);
    }
}
