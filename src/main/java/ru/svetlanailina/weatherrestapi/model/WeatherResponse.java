package ru.svetlanailina.weatherrestapi.model;

import lombok.Data;

@Data
public class WeatherResponse {
    private String city;
    private double temperature;
    private double windSpeed;
    private String weatherConditions;
    private String source;

    public WeatherResponse(String city, double temperature, double windSpeed, String weatherConditions, String source) {
        this.city = city;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.weatherConditions = weatherConditions;
        this.source = source;
    }
}
