package com.visitor_x.serviceImpl;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.dto.UpdateVisitorRequestDTO;
import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final VisitorRepository visitorRepository;

    @Override
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();

        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd   = today.plusDays(1).atStartOfDay();

        LocalDateTime weekStart = today.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime weekEnd   = today.with(DayOfWeek.SUNDAY)
                .plusDays(1).atStartOfDay();

        LocalDateTime monthStart = today
                .with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime monthEnd   = today
                .with(TemporalAdjusters.firstDayOfNextMonth()).atStartOfDay();

        return DashboardResponse.builder()
                .totalVisitors(visitorRepository.count())
                .todayVisitors(visitorRepository
                        .countByVisitDateTimeBetween(todayStart, todayEnd))
                .thisWeekVisitors(visitorRepository
                        .countByVisitDateTimeBetween(weekStart, weekEnd))
                .thisMonthVisitors(visitorRepository
                        .countByVisitDateTimeBetween(monthStart, monthEnd))
                .build();
    }

    @Override
    public Page<VisitorResponseDTO> getAllVisitors(Pageable pageable) {

        Page<Visitor> visitors = visitorRepository.findAll(pageable);

        if (visitors.isEmpty()) {
            throw new ResourceNotFoundException("No visitors found");
        }

        return visitors.map(this::toDTO);
    }
    @Override
    public VisitorResponseDTO getVisitor(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid visitor ID: " + id);
        }if (!visitorRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Visitor not found with id: " + id);
        }
        return visitorRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Visitor not found with id: " + id));
    }

    @Override
    public Page<VisitorResponseDTO> searchVisitors(
            String keyword, Pageable pageable) {

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Search keyword cannot be empty");
        }

        Page<Visitor> visitors =
                visitorRepository.searchByNameOrMobile(keyword, pageable);

        if (visitors.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No visitors found matching: " + keyword);
        }

        return visitors.map(this::toDTO);
    }


    @Override
    public List<VisitorResponseDTO> getTodayVisitors() {
        LocalDate today = LocalDate.now();

        List<Visitor> visitors = visitorRepository.findByVisitDateTimeBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        if (visitors.isEmpty()) {
            throw new ResourceNotFoundException("No visitors found for today");
        }

        return visitors.stream().map(this::toDTO).toList();
    }


    @Override
    public String deleteVisitor(Long id) {
        if (!visitorRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Visitor not found with id: " + id);
        }
        visitorRepository.deleteById(id);
        return "Visitor deleted successfully";
    }

    @Override
    public VisitorResponseDTO updateVisitor(
            Long id,
            UpdateVisitorRequestDTO requestDTO) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid visitor ID: " + id);
        }
        if (visitorRepository.existsByEmailAndVisitorIdNot(
                requestDTO.getEmail(), id)) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        if (visitorRepository.existsByMobileNumberAndVisitorIdNot(
                requestDTO.getMobileNumber(), id)) {
            throw new IllegalArgumentException(
                    "Mobile number already exists");
        }

        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visitor not found with id: " + id));

        visitor.setName(requestDTO.getName());
        visitor.setMobileNumber(requestDTO.getMobileNumber());
        visitor.setEmail(requestDTO.getEmail());
        visitor.setPurposeOfVisit(requestDTO.getPurposeOfVisit());

        Visitor updatedVisitor = visitorRepository.save(visitor);

        return toDTO(updatedVisitor);
    }


    private VisitorResponseDTO toDTO(Visitor v) {
        String photoBase64 = null;

        if (v.getPhoto() != null &&
                v.getPhoto().length > 0) {

            photoBase64 = "data:image/jpeg;base64," +
                    Base64.getEncoder()
                            .encodeToString(v.getPhoto());
        }
        return VisitorResponseDTO.builder()
                .visitorId(v.getVisitorId())
                .name(v.getName())
                .email(v.getEmail())
                .mobileNumber(v.getMobileNumber())
                .purposeOfVisit(v.getPurposeOfVisit())
                .photoBase64(photoBase64)
                .visitDateTime(v.getVisitDateTime())
                .build();
    }
}