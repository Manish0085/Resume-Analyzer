package com.substring.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateReportRequestDto {

    @NotBlank(message = "Self description is required")
    private String selfDescription;

    @NotBlank(message = "Job description is required")
    private String jobDescription;
}
