package ru.svetlanailina.weatherrestapi.service;

import ru.svetlanailina.weatherrestapi.model.WeatherApiResponse;
import ru.svetlanailina.weatherrestapi.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WeatherResponse getTodayWeather(String city) {
        String url = currentUrl + "?key=" + apiKey + "&q=" + city;
        ResponseEntity<WeatherApiResponse> response = restTemplate.getForEntity(url, WeatherApiResponse.class);
        WeatherApiResponse weatherData = response.getBody();

        if (weatherData != null && weatherData.getCurrent() != null) {
            return new WeatherResponse(
                    city,
                    weatherData.getCurrent().getTempC(),
                    weatherData.getCurrent().getWindKph(),
                    weatherData.getCurrent().getCondition().getText(),
                    "WeatherAPI"
            );
        } else {
            return new WeatherResponse(city, 0.0, 0.0, "No data", "WeatherAPI");
        }
    }

    @Override
    public List<WeatherResponse> getWeekWeather(String city) {
        String url = forecastUrl + "?key=" + apiKey + "&q=" + city + "&days=7";
        ResponseEntity<WeatherApiResponse> response = restTemplate.getForEntity(url, WeatherApiResponse.class);
        WeatherApiResponse weatherData = response.getBody();

        List<WeatherResponse> weekWeather = new ArrayList<>();
        if (weatherData != null && weatherData.getForecast() != null && weatherData.getForecast().getForecastday() != null) {
            for (WeatherApiResponse.Forecast.ForecastDay forecastDay : weatherData.getForecast().getForecastday()) {
                WeatherResponse weatherResponse = new WeatherResponse(
                        city,
                        forecastDay.getDay().getAvgTempC(),
                        forecastDay.getDay().getMaxWindKph(),
                        forecastDay.getDay().getCondition().getText(),
                        "WeatherAPI"
                );
                weekWeather.add(weatherResponse);
            }
        } else {
            weekWeather.add(new WeatherResponse(city, 0.0, 0.0, "No data", "WeatherAPI"));
        }
        return weekWeather;
    }
}
