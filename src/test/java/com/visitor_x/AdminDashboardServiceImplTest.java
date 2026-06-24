package com.visitor_x;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.dto.VisitorResponseDTO;

import com.visitor_x.entity.Visitor;
import com.visitor_x.enums.PurposeOfVisit;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.AdminDashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest {

    @Mock
    private VisitorRepository visitorRepository;

    @InjectMocks
    private AdminDashboardServiceImpl dashboardService;

    private Visitor visitor;

    @BeforeEach
    void setUp() {

        visitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .email("gangadhar@gmail.com")
                .mobileNumber("9876543210")
                .purposeOfVisit(PurposeOfVisit.INTERVIEW)
                .visitDateTime(LocalDateTime.now())
                .photo("photo".getBytes())
                .build();
    }

    @Test
    void getDashboard_Success() {

        when(visitorRepository.count()).thenReturn(100L);

        when(visitorRepository.countByVisitDateTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(10L, 30L, 50L);

        DashboardResponse response =
                dashboardService.getDashboard();

        assertNotNull(response);
        assertEquals(100L, response.getTotalVisitors());
        assertEquals(10L, response.getTodayVisitors());
        assertEquals(30L, response.getThisWeekVisitors());
        assertEquals(50L, response.getThisMonthVisitors());
    }
}