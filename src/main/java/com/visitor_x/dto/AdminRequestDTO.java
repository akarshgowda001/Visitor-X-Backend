package com.visitor_x.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}