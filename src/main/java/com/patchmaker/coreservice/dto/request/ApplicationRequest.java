package com.patchmaker.coreservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotNull(message = "Project id is required")
    private Long projectId;

    @NotBlank(message = "Message is required")
    private String message;
}