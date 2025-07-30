package com.example.nhonApp.controller;

import com.example.nhonApp.dto.weather.WeatherResponse;
import com.example.nhonApp.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherResponse> getCurrentWeather(@RequestParam String city) {
        WeatherResponse weatherData = weatherService.getCurrentWeather(city);
        if (weatherData != null) {
            return ResponseEntity.ok(weatherData);
        }
        return ResponseEntity.notFound().build();
    }
}