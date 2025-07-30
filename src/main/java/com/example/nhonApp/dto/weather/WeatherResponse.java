package com.example.nhonApp.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Bỏ qua các field không cần thiết trong JSON
public class WeatherResponse {
    private List<Weather> weather;
    private Main main;
    private String name; // Tên thành phố
}