package ru.svetlanailina.weatherrestapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YandexWeatherResponse {
    private Fact fact;
    private List<Forecast> forecasts;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fact {
        private double temp;
        @JsonProperty("wind_speed")
        private double windSpeed;
        private String condition;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Forecast {
        private String date;
        private Parts parts;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parts {
        private Part day;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Part {
            @JsonProperty("temp_avg")
            private double tempAvg;
            @JsonProperty("wind_speed")
            private double windSpeed;
            private String condition;
        }
    }
}
