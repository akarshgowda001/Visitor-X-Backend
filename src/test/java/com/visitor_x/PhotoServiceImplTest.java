package com.visitor_x;

import com.visitor_x.serviceImpl.PhotoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PhotoServiceImplTest {

    private PhotoServiceImpl photoService;

    @BeforeEach
    void setUp() {
        photoService = new PhotoServiceImpl();
    }

    private String generateValidBase64Image() throws Exception {

        BufferedImage image =
                new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        ImageIO.write(image, "png", baos);

        String base64 =
                Base64.getEncoder().encodeToString(baos.toByteArray());

        return "data:image/png;base64," + base64;
    }

    @Test
    void convertBase64ToJpg_ShouldConvertSuccessfully() throws Exception {

        String base64Image = generateValidBase64Image();

        byte[] result =
                photoService.convertBase64ToJpg(base64Image);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void convertBase64ToJpg_ShouldThrow_WhenNullInput() {

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> photoService.convertBase64ToJpg(null)
                );

        assertEquals("Photo is required", ex.getMessage());
    }

    @Test
    void convertBase64ToJpg_ShouldThrow_WhenBlankInput() {

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> photoService.convertBase64ToJpg(" ")
                );

        assertEquals("Photo is required", ex.getMessage());
    }

    @Test
    void convertBase64ToJpg_ShouldThrow_WhenInvalidDataUri() {

        String invalid =
                "data:image/png;base64";

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> photoService.convertBase64ToJpg(invalid)
                );

        assertEquals(
                "Invalid data URI format",
                ex.getMessage()
        );
    }

    @Test
    void convertBase64ToJpg_ShouldThrow_WhenUnsupportedMimeType() {

        String invalid =
                "data:image/tiff;base64,abcd";

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> photoService.convertBase64ToJpg(invalid)
                );

        assertTrue(
                ex.getMessage().contains("Unsupported image type")
        );
    }

    @Test
    void convertBase64ToJpg_ShouldThrow_WhenInvalidBase64() {

        String invalid =
                "data:image/png;base64,@@@@@@";

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> photoService.convertBase64ToJpg(invalid)
                );

        assertEquals(
                "Invalid Base64 encoding",
                ex.getMessage()
        );
    }

    @Test
    void convertBase64ToJpg_ShouldThrow_WhenNotImageData() {

        String text =
                Base64.getEncoder()
                        .encodeToString("Hello".getBytes());

        String invalid =
                "data:image/png;base64," + text;

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> photoService.convertBase64ToJpg(invalid)
                );

        assertEquals(
                "Decoded data is not a valid supported image",
                ex.getMessage()
        );
    }

    @Test
    void isValidImage_ShouldReturnTrue_ForValidImage() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.png",
                        "image/png",
                        new byte[]{1, 2, 3}
                );

        assertTrue(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_ShouldReturnFalse_WhenFileIsNull() {

        assertFalse(photoService.isValidImage(null));
    }

    @Test
    void isValidImage_ShouldReturnFalse_WhenFileIsEmpty() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "empty.png",
                        "image/png",
                        new byte[]{}
                );

        assertFalse(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_ShouldReturnFalse_WhenFileTooLarge() {

        byte[] data =
                new byte[6 * 1024 * 1024];

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "large.png",
                        "image/png",
                        data
                );

        assertFalse(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_ShouldReturnFalse_WhenContentTypeNull() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.png",
                        null,
                        new byte[]{1, 2}
                );

        assertFalse(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_ShouldReturnFalse_WhenUnsupportedType() {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "file.pdf",
                        "application/pdf",
                        new byte[]{1, 2}
                );

        assertFalse(photoService.isValidImage(file));
    }
}