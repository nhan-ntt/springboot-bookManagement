package com.example.nhonApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponse {
    private String filename;
    private InputStreamResource fileResource;
}
