package ru.svetlanailina.weatherrestapi.service;

import ru.svetlanailina.weatherrestapi.model.WeatherResponse;

import java.util.List;

public interface WeatherProvider {
    WeatherResponse getTodayWeather(String city);
    List<WeatherResponse> getWeekWeather(String city);
}
