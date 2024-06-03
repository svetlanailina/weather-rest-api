package ru.svetlanailina.weatherrestapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityCoordinates {
    private String cityName;
    private double lat;
    private double lon;
}
