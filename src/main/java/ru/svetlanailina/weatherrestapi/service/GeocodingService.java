package ru.svetlanailina.weatherrestapi.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.svetlanailina.weatherrestapi.model.CityCoordinates;

@Service
public class GeocodingService {

    @Value("${weather.geocoding.api-key}")
    private String apiKey;

    @Value("${weather.geocoding.url}")
    private String geocodingUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public CityCoordinates getCoordinates(String city) {
        String url = String.format("%s?q=%s&appid=%s", geocodingUrl, city, apiKey);
        GeocodingResponse[] response = restTemplate.getForObject(url, GeocodingResponse[].class);

        if (response != null && response.length > 0) {
            return new CityCoordinates(city, response[0].getLat(), response[0].getLon());
        } else {
            // Return Moscow coordinates and name by default if city not found
            return new CityCoordinates("Moscow", 55.7558, 37.6173);
        }
    }

    @Data
    private static class GeocodingResponse {
        private double lat;
        private double lon;
    }
}
