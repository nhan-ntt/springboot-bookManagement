package com.example.nhonApp.dto.weather;

import lombok.Data;

@Data
public class Weather {
    private String main;
    private String description;
    private String icon;
}
