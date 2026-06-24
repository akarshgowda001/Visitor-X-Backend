package com.visitor_x.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponseDTO {
    private Long adminId;
    private String username;
    private String role;
}