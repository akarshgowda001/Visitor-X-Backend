
package com.visitor_x.serviceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.visitor_x.service.QRService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class QRServiceImpl implements QRService {

    @Value("${app.qr.save-path}")
    private String savePath;

    @Override
    public byte[] generateQRCode(String text) throws WriterException, IOException {
        BitMatrix bitMatrix = encode(text);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public String saveQRCode(String text) throws WriterException, IOException {
        Path dirPath = Paths.get(savePath);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("QR text cannot be empty");
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "visitor_qr_" + timestamp + ".png";
        Path filePath = dirPath.resolve(fileName);

        MatrixToImageWriter.writeToPath(encode(text), "PNG", filePath);

        if (!Files.exists(filePath)) {
            throw new IOException("Failed to save QR code image");
        }
        return filePath.toAbsolutePath().toString();
    }

    private BitMatrix encode(String text) throws WriterException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("QR text cannot be empty");
        }
        return new QRCodeWriter()
                .encode(text, BarcodeFormat.QR_CODE, 300, 300);
    }
}