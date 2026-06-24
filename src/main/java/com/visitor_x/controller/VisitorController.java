package com.visitor_x.controller;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.enums.PurposeOfVisit;
import com.visitor_x.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;


@PostMapping("/register")
public ResponseEntity<?> registerVisitorWithPhoto(
        @Valid @RequestBody VisitorRequestDTO request) {

      PurposeOfVisit purpose = request.getPurposeOfVisit();

    System.out.println("Name = " + request.getName());
    System.out.println("Email = " + request.getEmail());
    System.out.println("Mobile = " + request.getMobileNumber());
    System.out.println("Purpose = " + purpose);

    if (request.getPhotoBase64() != null) {
        System.out.println("Photo Length = " + request.getPhotoBase64().length());
    }

    return ResponseEntity.ok(
            visitorService.registerVisitorWithPhoto(request,request.getPurposeOfVisit())
    );
}

}