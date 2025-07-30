package com.example.nhonApp.service;

import com.example.nhonApp.dto.weather.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    @Value("${api.openweathermap.key}")
    private String apiKey;

    public WeatherResponse getCurrentWeather(String city) {
        // Dùng UriComponentsBuilder để xây dựng URL an toàn, tự động mã hóa tham số
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric") // Lấy nhiệt độ theo độ C
                .queryParam("lang", "vi")      // Lấy mô tả bằng tiếng Việt
                .toUriString();

        try {
            log.info("Gọi API thời tiết: {}", url);
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            return response;
        } catch (HttpClientErrorException e) {
            // Xử lý lỗi khi không tìm thấy thành phố (lỗi 404)
            log.error("Lỗi khi gọi API cho thành phố {}: {}", city, e.getStatusCode());
            // Bạn có thể ném ra một exception tùy chỉnh ở đây
            return null;
        }
    }
}