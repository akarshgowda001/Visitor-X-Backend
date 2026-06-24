package com.visitor_x.controller;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.dto.UpdateVisitorRequestDTO;
import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.service.AdminDashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/visitors")
    public ResponseEntity<Page<VisitorResponseDTO>> getAllVisitors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("visitDateTime").descending());
        return ResponseEntity.ok(
                dashboardService.getAllVisitors(pageable));
    }

    @GetMapping("/visitors/today")
    public ResponseEntity<List<VisitorResponseDTO>> getTodayVisitors() {
        return ResponseEntity.ok(dashboardService.getTodayVisitors());
    }

    @GetMapping("/visitors/search")
    public ResponseEntity<Page<VisitorResponseDTO>> searchVisitors(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("visitDateTime").descending());
        return ResponseEntity.ok(
                dashboardService.searchVisitors(keyword, pageable));
    }

    @GetMapping("/visitors/{id}")
    public ResponseEntity<VisitorResponseDTO> getVisitorById(
            @PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.getVisitor(id));
    }

    @DeleteMapping("/visitors/{id}")
    public ResponseEntity<Void> deleteVisitor(
            @PathVariable Long id) {
        dashboardService.deleteVisitor(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/visitors/{id}")
    public ResponseEntity<VisitorResponseDTO> updateVisitor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVisitorRequestDTO requestDTO) {

        return ResponseEntity.ok(
                dashboardService.updateVisitor(id, requestDTO)
        );
    }

}