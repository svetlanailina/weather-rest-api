package ru.svetlanailina.weatherrestapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.svetlanailina.weatherrestapi.model.WeatherApiResponse;
import ru.svetlanailina.weatherrestapi.model.WeatherResponse;
import ru.svetlanailina.weatherrestapi.model.CityCoordinates;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherApiService implements WeatherProvider {

    @Value("${weather.weatherapi.api-key}")
    private String apiKey;

    @Value("${weather.weatherapi.url}")
    private String currentUrl;

    @Value("${weather.weatherapi.url.forecast}")
    private String forecastUrl;

    @Autowired
    private GeocodingService geocodingService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WeatherResponse getTodayWeather(String city) {
        CityCoordinates coordinates = geocodingService.getCoordinates(city);
        String url = String.format("%s?key=%s&q=%s,%s", currentUrl, apiKey, coordinates.getLat(), coordinates.getLon());
        ResponseEntity<WeatherApiResponse> response = restTemplate.getForEntity(url, WeatherApiResponse.class);
        WeatherApiResponse weatherData = response.getBody();

        String cityName = coordinates.getCityName().isEmpty() ? "Moscow" : coordinates.getCityName();

        if (weatherData != null && weatherData.getCurrent() != null) {
            return new WeatherResponse(
                    cityName,
                    weatherData.getCurrent().getTempC(),
                    weatherData.getCurrent().getWindKph(),
                    weatherData.getCurrent().getCondition().getText(),
                    "WeatherAPI"
            );
        } else {
            return new WeatherResponse(cityName, 0.0, 0.0, "No data", "WeatherAPI");
        }
    }

    @Override
    public List<WeatherResponse> getWeekWeather(String city) {
        CityCoordinates coordinates = geocodingService.getCoordinates(city);
        String url = String.format("%s?key=%s&q=%s,%s&days=7", forecastUrl, apiKey, coordinates.getLat(), coordinates.getLon());
        ResponseEntity<WeatherApiResponse> response = restTemplate.getForEntity(url, WeatherApiResponse.class);
        WeatherApiResponse weatherData = response.getBody();

        String cityName = coordinates.getCityName().isEmpty() ? "Moscow" : coordinates.getCityName();

        List<WeatherResponse> weekWeather = new ArrayList<>();
        if (weatherData != null && weatherData.getForecast() != null && weatherData.getForecast().getForecastday() != null) {
            for (WeatherApiResponse.Forecast.ForecastDay forecastDay : weatherData.getForecast().getForecastday()) {
                WeatherResponse weatherResponse = new WeatherResponse(
                        cityName,
                        forecastDay.getDay().getAvgTempC(),
                        forecastDay.getDay().getMaxWindKph(),
                        forecastDay.getDay().getCondition().getText(),
                        "WeatherAPI"
                );
                weekWeather.add(weatherResponse);
            }
        } else {
            weekWeather.add(new WeatherResponse(cityName, 0.0, 0.0, "No data", "WeatherAPI"));
        }
        return weekWeather;
    }
}
