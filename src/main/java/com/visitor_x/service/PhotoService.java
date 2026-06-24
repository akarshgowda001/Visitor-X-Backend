package com.visitor_x.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {

    /**
     * Convert a base64 encoded string to a byte array representing a JPG image
     * @param photoBase64 The base64 encoded string of the photo
     * @return byte array of the JPG image
     */
    byte[] convertBase64ToJpg(String photoBase64);
    /**
     * Validate if the uploaded file is a valid image
     * @param file The file to validate
     * @return true if valid image, false otherwise
     */
    boolean isValidImage(MultipartFile file);

}

