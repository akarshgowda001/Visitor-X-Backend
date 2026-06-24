package com.visitor_x.controller;

import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;
import com.visitor_x.dto.ChangePasswordRequestDTO;
import com.visitor_x.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(
            @Valid @RequestBody AdminRequestDTO request) {
        return ResponseEntity.ok("Admin Created Successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponseDTO> getAdmin(
            @PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAdmin(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAdmin(
            @PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequestDTO request,
            Authentication authentication) {
        adminService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}