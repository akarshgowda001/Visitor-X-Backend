package com.visitor_x.serviceImpl;

import com.visitor_x.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(
            Arrays.asList(
                    "image/jpeg",
                    "image/jpg",
                    "image/png",
                    "image/gif",
                    "image/bmp"
            )
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final int THUMB_WIDTH = 200;
    private static final int THUMB_HEIGHT = 200;

    @Override
    public byte[] convertBase64ToJpg(String photoBase64) {
        try {
            if (photoBase64 == null || photoBase64.isBlank()) {
                throw new IllegalArgumentException("Photo is required");
            }

            log.info("Received Base64 length: {}", photoBase64.length());

            String base64Data = photoBase64.trim();
            String mimeType = null;

            if (base64Data.startsWith("data:")) {
                int commaIndex = base64Data.indexOf(",");
                if (commaIndex == -1) {
                    throw new IllegalArgumentException("Invalid data URI format");
                }

                String prefix = base64Data.substring(0, commaIndex);
                log.info("Photo prefix: {}", prefix);

                if (!prefix.contains(";base64")) {
                    throw new IllegalArgumentException("Photo must be base64 encoded");
                }

                mimeType = prefix.substring(5, prefix.indexOf(";"));
                if (!ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
                    throw new IllegalArgumentException("Unsupported image type: " + mimeType);
                }

                base64Data = base64Data.substring(commaIndex + 1).trim();
            }

            log.info("Pure Base64 length: {}", base64Data.length());

            byte[] imageBytes;
            try {
                imageBytes = Base64.getDecoder().decode(base64Data);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid Base64 encoding");
            }

            log.info("Decoded bytes length: {}", imageBytes.length);

            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (bufferedImage == null) {
                throw new IllegalArgumentException("Decoded data is not a valid supported image");
            }

            BufferedImage jpgImage = new BufferedImage(
                    THUMB_WIDTH,
                    THUMB_HEIGHT,
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = jpgImage.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, THUMB_WIDTH, THUMB_HEIGHT);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(bufferedImage, 0, 0, THUMB_WIDTH, THUMB_HEIGHT, null);
            g.dispose();

            ByteArrayOutputStream jpgOutput = new ByteArrayOutputStream();
            boolean written = ImageIO.write(jpgImage, "jpg", jpgOutput);
            if (!written) {
                throw new IOException("No suitable JPG writer found");
            }

            return jpgOutput.toByteArray();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        return ALLOWED_MIME_TYPES.contains(contentType.toLowerCase());
    }
}