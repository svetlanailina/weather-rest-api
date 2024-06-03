package ru.svetlanailina.weatherrestapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.svetlanailina.weatherrestapi.model.WeatherResponse;
import ru.svetlanailina.weatherrestapi.model.YandexWeatherResponse;
import ru.svetlanailina.weatherrestapi.model.CityCoordinates;

import java.util.ArrayList;
import java.util.List;

@Service
public class YandexWeatherService implements WeatherProvider {

    @Value("${weather.yandex.api-key}")
    private String apiKey;

    @Value("${weather.yandex.url}")
    private String apiUrl;

    @Autowired
    private GeocodingService geocodingService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WeatherResponse getTodayWeather(String city) {
        CityCoordinates coordinates = geocodingService.getCoordinates(city);
        String url = apiUrl + "?lat=" + coordinates.getLat() + "&lon=" + coordinates.getLon();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Yandex-API-Key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<YandexWeatherResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, YandexWeatherResponse.class);
        YandexWeatherResponse weatherData = response.getBody();

        if (weatherData != null && weatherData.getFact() != null) {
            return new WeatherResponse(
                    coordinates.getCityName(),
                    weatherData.getFact().getTemp(),
                    weatherData.getFact().getWindSpeed(),
                    weatherData.getFact().getCondition(),
                    "Yandex"
            );
        } else {
            return new WeatherResponse(coordinates.getCityName(), 0.0, 0.0, "No data", "Yandex");
        }
    }

    @Override
    public List<WeatherResponse> getWeekWeather(String city) {
        CityCoordinates coordinates = geocodingService.getCoordinates(city);
        String url = apiUrl + "?lat=" + coordinates.getLat() + "&lon=" + coordinates.getLon();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Yandex-API-Key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<YandexWeatherResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, YandexWeatherResponse.class);
        YandexWeatherResponse weatherData = response.getBody();

        List<WeatherResponse> weekWeather = new ArrayList<>();
        if (weatherData != null && weatherData.getForecasts() != null) {
            for (YandexWeatherResponse.Forecast forecast : weatherData.getForecasts()) {
                WeatherResponse weatherResponse = new WeatherResponse(
                        coordinates.getCityName(),
                        forecast.getParts().getDay().getTempAvg(),
                        forecast.getParts().getDay().getWindSpeed(),
                        forecast.getParts().getDay().getCondition(),
                        "Yandex"
                );
                weekWeather.add(weatherResponse);
            }
        } else {
            weekWeather.add(new WeatherResponse(coordinates.getCityName(), 0.0, 0.0, "No data", "Yandex"));
        }
        return weekWeather;
    }
}
