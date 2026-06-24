package com.visitor_x;

import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;
import com.visitor_x.dto.ChangePasswordRequestDTO;
import com.visitor_x.entity.Admin;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.AdminRepository;
import com.visitor_x.serviceImpl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin admin;
    private AdminRequestDTO requestDTO;
    private ChangePasswordRequestDTO passwordRequest;

    @BeforeEach
    void setUp() {

        admin = Admin.builder()
                .adminId(1L)
                .username("admin")
                .password("encodedPassword")
                .role("ADMIN")
                .build();

        requestDTO = new AdminRequestDTO();
        requestDTO.setUsername("admin");
        requestDTO.setPassword("password123");

        passwordRequest = new ChangePasswordRequestDTO();
        passwordRequest.setOldPassword("oldPassword");
        passwordRequest.setNewPassword("newPassword");
    }

    @Test
    void createAdmin_Success() {

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(adminRepository.save(any(Admin.class)))
                .thenReturn(admin);

        AdminResponseDTO response =
                adminService.createAdmin(requestDTO);

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());

        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void createAdmin_DuplicateUsername() {

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> adminService.createAdmin(requestDTO)
                );

        assertEquals(
                "Username already exists",
                exception.getMessage()
        );
    }

    @Test
    void getAdmin_Success() {

        when(adminRepository.findById(1L))
                .thenReturn(Optional.of(admin));

        AdminResponseDTO response =
                adminService.getAdmin(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAdminId());
        assertEquals("admin", response.getUsername());
    }

    @Test
    void getAdmin_NotFound() {

        when(adminRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> adminService.getAdmin(1L)
                );

        assertEquals(
                "Admin not found with id: 1",
                exception.getMessage()
        );
    }

    @Test
    void getAllAdmins_Success() {

        Admin admin2 = Admin.builder()
                .adminId(2L)
                .username("admin2")
                .password("password")
                .role("ADMIN")
                .build();

        when(adminRepository.findAll())
                .thenReturn(List.of(admin, admin2));

        List<AdminResponseDTO> admins =
                adminService.getAllAdmins();

        assertEquals(2, admins.size());
    }

    @Test
    void deleteAdmin_Success() {

        when(adminRepository.existsById(1L))
                .thenReturn(true);

        when(adminRepository.count())
                .thenReturn(2L);

        adminService.deleteAdmin(1L);

        verify(adminRepository).deleteById(1L);
    }

    @Test
    void deleteAdmin_NotFound() {

        when(adminRepository.existsById(1L))
                .thenReturn(false);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> adminService.deleteAdmin(1L)
                );

        assertEquals(
                "Admin not found with id: 1",
                exception.getMessage()
        );
    }

    @Test
    void deleteAdmin_LastAdmin() {

        when(adminRepository.existsById(1L))
                .thenReturn(true);

        when(adminRepository.count())
                .thenReturn(1L);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> adminService.deleteAdmin(1L)
                );

        assertEquals(
                "Cannot delete the last admin",
                exception.getMessage()
        );
    }

    @Test
    void changePassword_Success() {

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(passwordEncoder.matches(
                "oldPassword",
                "encodedPassword"))
                .thenReturn(true);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("newEncodedPassword");

        adminService.changePassword(
                "admin",
                passwordRequest
        );

        verify(adminRepository).save(admin);

        assertEquals(
                "newEncodedPassword",
                admin.getPassword()
        );
    }

    @Test
    void changePassword_AdminNotFound() {

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> adminService.changePassword(
                                "admin",
                                passwordRequest)
                );

        assertEquals(
                "Admin not found",
                exception.getMessage()
        );
    }

    @Test
    void changePassword_WrongOldPassword() {

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(passwordEncoder.matches(
                "oldPassword",
                "encodedPassword"))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> adminService.changePassword(
                                "admin",
                                passwordRequest)
                );

        assertEquals(
                "Old password is incorrect",
                exception.getMessage()
        );
    }
}