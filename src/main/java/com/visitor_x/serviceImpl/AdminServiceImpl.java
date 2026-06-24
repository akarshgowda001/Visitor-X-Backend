package com.visitor_x.serviceImpl;

import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;
import com.visitor_x.dto.ChangePasswordRequestDTO;
import com.visitor_x.entity.Admin;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.AdminRepository;
import com.visitor_x.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminResponseDTO createAdmin(AdminRequestDTO request) {
        adminRepository.findByUsername(request.getUsername())
                .ifPresent(a -> {
                    throw new DuplicateResourceException(
                            "Username already exists");
                });

        Admin admin = Admin.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ADMIN")
                .build();

        return toDTO(adminRepository.save(admin));
    }

    @Override
    public AdminResponseDTO getAdmin(Long id) {
        return adminRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin not found with id: " + id));
    }

    @Override
    public List<AdminResponseDTO> getAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Admin not found with id: " + id);
        }
        if (adminRepository.count() == 1) {
            throw new IllegalStateException(
                    "Cannot delete the last admin");
        }

        adminRepository.deleteById(id);
    }
    @Override
    public void changePassword(String username, ChangePasswordRequestDTO request) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminRepository.save(admin);
    }

    private AdminResponseDTO toDTO(Admin admin) {
        return AdminResponseDTO.builder()
                .adminId(admin.getAdminId())
                .username(admin.getUsername())
                .role(admin.getRole())
                .build();
    }
}