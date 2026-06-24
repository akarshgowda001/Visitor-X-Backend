package com.visitor_x.dto;



import com.visitor_x.enums.PurposeOfVisit;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VisitorResponseDTO {

    private Long visitorId;
    private String name;
    private String mobileNumber;
    private String email;
    private PurposeOfVisit purposeOfVisit;
    private String photoBase64;
    private LocalDateTime visitDateTime;
}