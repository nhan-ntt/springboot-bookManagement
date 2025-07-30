package com.example.nhonApp.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Main {
    private double temp;
    @JsonProperty("feels_like") // Map field JSON "feels_like" vào "feelsLike"
    private double feelsLike;
    private int humidity;
}