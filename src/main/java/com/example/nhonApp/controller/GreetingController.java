package com.example.nhonApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import java.util.Locale;

@RestController
@RequestMapping("/i18n")
public class GreetingController {

    private final MessageSource messageSource;

    // Spring sẽ tự động tiêm (inject) MessageSource bean đã được cấu hình sẵn
    public GreetingController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Endpoint để lấy lời chào đã được dịch.
     * Spring sẽ tự động lấy Locale từ header "Accept-Language" của request.
     *
     * @param locale Locale của người dùng (ví dụ: "vi", "ja", "en")
     * @return Một chuỗi lời chào đã được dịch
     */
    // Thêm annotation @Operation để mô tả header
    @Operation(summary = "Lấy lời chào đã được dịch", parameters = {
            @Parameter(in = ParameterIn.HEADER, // Chỉ định tham số nằm ở Header
                    name = "Accept-Language",
                    description = "Ngôn ngữ ưu tiên (vd: vi, ja, en)",
                    required = false)
    })
    @GetMapping("/greeting")
    // Dùng @Parameter(hidden = true) để ẩn tham số Locale vô dụng khỏi UI
    public String getGreeting(@Parameter(hidden = true) Locale locale) {
        return messageSource.getMessage("greeting.hello", null, locale);
    }

    /**
     * Một ví dụ khác để lấy thông điệp chào mừng.
     */
    @GetMapping("/welcome")
    public String getWelcomeMessage(Locale locale) {
        return messageSource.getMessage("greeting.welcome", null, locale);
    }
}