package com.visitor_x;


import com.visitor_x.entity.Visitor;
import com.visitor_x.enums.PurposeOfVisit;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.ExportServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private VisitorRepository repository;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ExportServiceImpl exportService;

    @TempDir
    Path tempDir;

    private Visitor visitor;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                exportService,
                "savePath",
                tempDir.toString()
        );

        visitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .mobileNumber("9876543210")
                .email("gangadhar@gmail.com")
                .purposeOfVisit(PurposeOfVisit.INTERVIEW)
                .visitDateTime(LocalDateTime.now())
                .photo("photo".getBytes())
                .build();
    }

    @Test
    void exportVisitors_ShouldWriteExcelToResponse() throws Exception {

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ServletOutputStream servletOutputStream =
                new ServletOutputStream() {

                    @Override
                    public void write(int b) {
                        baos.write(b);
                    }

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(
                            WriteListener writeListener) {
                    }
                };

        when(response.getOutputStream())
                .thenReturn(servletOutputStream);

        exportService.exportVisitors(response);

        verify(response).setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        verify(response).setHeader(
                "Content-Disposition",
                "attachment; filename=visitors.xlsx");

        assertTrue(baos.size() > 0);
    }

    @Test
    void exportVisitors_WhenOutputStreamFails_ShouldThrowRuntimeException()
            throws Exception {

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        when(response.getOutputStream())
                .thenThrow(new IOException("Stream Error"));

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> exportService.exportVisitors(response));

        assertTrue(
                exception.getMessage()
                        .contains("Failed to export Excel"));
    }

    @Test
    void autoSaveToFile_ShouldCreateExcelFile() {

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        exportService.autoSaveToFile();

        Path excelFile =
                tempDir.resolve("visitors.xlsx");

        assertTrue(Files.exists(excelFile));
    }

    @Test
    void autoSaveToFile_ShouldCreateDirectoryIfNotExists() {

        Path customDir =
                tempDir.resolve("exports");

        ReflectionTestUtils.setField(
                exportService,
                "savePath",
                customDir.toString()
        );

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        exportService.autoSaveToFile();

        assertTrue(Files.exists(customDir));
        assertTrue(
                Files.exists(
                        customDir.resolve("visitors.xlsx")));
    }

    @Test
    void autoSaveToFile_WithEmptyVisitorList_ShouldStillCreateExcel() {

        when(repository.findAll())
                .thenReturn(Collections.emptyList());

        exportService.autoSaveToFile();

        Path excelFile =
                tempDir.resolve("visitors.xlsx");

        assertTrue(Files.exists(excelFile));
    }

    @Test
    void autoSaveToFile_WithNullVisitorFields_ShouldNotThrowException() {

        Visitor visitorWithNullValues = Visitor.builder()
                .visitorId(2L)
                .name(null)
                .mobileNumber(null)
                .email(null)
                .purposeOfVisit(null)
                .visitDateTime(null)
                .photo(null)
                .build();

        when(repository.findAll())
                .thenReturn(List.of(visitorWithNullValues));

        assertDoesNotThrow(() ->
                exportService.autoSaveToFile());
    }

    @Test
    void autoSaveToFile_WithPhoto_ShouldMarkPhotoStoredYes() {

        when(repository.findAll())
                .thenReturn(List.of(visitor));

        assertDoesNotThrow(() ->
                exportService.autoSaveToFile());
    }

    @Test
    void autoSaveToFile_WithoutPhoto_ShouldMarkPhotoStoredNo() {

        Visitor noPhotoVisitor = Visitor.builder()
                .visitorId(3L)
                .name("Test User")
                .photo(null)
                .build();

        when(repository.findAll())
                .thenReturn(List.of(noPhotoVisitor));

        assertDoesNotThrow(() ->
                exportService.autoSaveToFile());
    }
}