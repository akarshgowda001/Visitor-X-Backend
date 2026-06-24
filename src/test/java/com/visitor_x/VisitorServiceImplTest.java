package com.visitor_x.serviceImpl;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.enums.PurposeOfVisit;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.ExportService;
import com.visitor_x.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceImplTest {

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private ExportService exportService;

    @Mock
    private PhotoService photoService;

    @InjectMocks
    private VisitorServiceImpl visitorService;

    private VisitorRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new VisitorRequestDTO();
        requestDTO.setName("Gangadhar");
        requestDTO.setEmail("gangadhar@gmail.com");
        requestDTO.setMobileNumber("9876543210");
        requestDTO.setPhotoBase64("dummyBase64String");
    }

    @Test
    void registerVisitorWithPhoto_Success() {

        byte[] photoBytes = "photo".getBytes();

        Visitor savedVisitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .email("gangadhar@gmail.com")
                .mobileNumber("9876543210")
                .purposeOfVisit(PurposeOfVisit.MEETING)
                .photo(photoBytes)
                .build();

        when(visitorRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(anyString()))
                .thenReturn(Optional.empty());

        when(photoService.convertBase64ToJpg(anyString()))
                .thenReturn(photoBytes);

        when(visitorRepository.save(any(Visitor.class)))
                .thenReturn(savedVisitor);

        VisitorResponseDTO response =
                visitorService.registerVisitorWithPhoto(
                        requestDTO,
                        PurposeOfVisit.MEETING);

        assertNotNull(response);
        assertEquals("Gangadhar", response.getName());
        assertEquals("gangadhar@gmail.com", response.getEmail());

        verify(visitorRepository).save(any(Visitor.class));
        verify(exportService).autoSaveToFile();
    }

    @Test
    void registerVisitorWithPhoto_InvalidEmail() {

        requestDTO.setEmail("test@yahoo.com");

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> visitorService.registerVisitorWithPhoto(
                                requestDTO,
                                PurposeOfVisit.MEETING));

        assertEquals(
                "Only Gmail addresses are allowed",
                ex.getMessage());
    }

    @Test
    void registerVisitorWithPhoto_InvalidMobile() {

        requestDTO.setMobileNumber("12345");

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> visitorService.registerVisitorWithPhoto(
                                requestDTO,
                                PurposeOfVisit.MEETING));

        assertEquals(
                "Mobile number must contain exactly 10 digits",
                ex.getMessage());
    }

    @Test
    void registerVisitorWithPhoto_PhotoMissing() {

        requestDTO.setPhotoBase64("");

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> visitorService.registerVisitorWithPhoto(
                                requestDTO,
                                PurposeOfVisit.MEETING));

        assertEquals(
                "Photo is required",
                ex.getMessage());
    }

    @Test
    void registerVisitorWithPhoto_DuplicateEmail() {

        Visitor visitor = Visitor.builder().build();

        when(visitorRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(visitor));

        assertThrows(
                DuplicateResourceException.class,
                () -> visitorService.registerVisitorWithPhoto(
                        requestDTO,
                        PurposeOfVisit.MEETING));

        verify(visitorRepository, never())
                .save(any());
    }

    @Test
    void registerVisitorWithPhoto_DuplicateMobile() {

        Visitor visitor = Visitor.builder().build();

        when(visitorRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(anyString()))
                .thenReturn(Optional.of(visitor));

        assertThrows(
                DuplicateResourceException.class,
                () -> visitorService.registerVisitorWithPhoto(
                        requestDTO,
                        PurposeOfVisit.MEETING));

        verify(visitorRepository, never())
                .save(any());
    }

    @Test
    void getVisitorById_Success() {

        Visitor visitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .email("gangadhar@gmail.com")
                .mobileNumber("9876543210")
                .purposeOfVisit(PurposeOfVisit.MEETING)
                .build();

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.of(visitor));

        VisitorResponseDTO response =
                visitorService.getVisitorById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getVisitorId());
        assertEquals("Gangadhar", response.getName());
    }

    @Test
    void getVisitorById_NotFound() {

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> visitorService.getVisitorById(1L));
    }
}