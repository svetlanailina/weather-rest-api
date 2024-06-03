package ru.svetlanailina.weatherrestapi.controller;

import ru.svetlanailina.weatherrestapi.model.WeatherResponse;
import ru.svetlanailina.weatherrestapi.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Value("${weather.default-city}")
    private String defaultCity;

    @GetMapping("/today")
    public ResponseEntity<List<WeatherResponse>> getTodayWeather(
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "all") String source) {
        String finalCity = (city != null && !city.isEmpty()) ? city : defaultCity;
        return ResponseEntity.ok(weatherService.getTodayWeather(finalCity, source));
    }

    @GetMapping("/week")
    public ResponseEntity<List<WeatherResponse>> getWeekWeather(
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "all") String source) {
        String finalCity = (city != null && !city.isEmpty()) ? city : defaultCity;
        return ResponseEntity.ok(weatherService.getWeekWeather(finalCity, source));
    }
}
