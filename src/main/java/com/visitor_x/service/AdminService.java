package com.visitor_x.service;

import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;
import com.visitor_x.dto.ChangePasswordRequestDTO;

import java.util.List;

public interface AdminService {
    AdminResponseDTO createAdmin(AdminRequestDTO request);
    AdminResponseDTO getAdmin(Long id);
    List<AdminResponseDTO> getAllAdmins();
    void deleteAdmin(Long id);
    void changePassword(String username, ChangePasswordRequestDTO request);
}