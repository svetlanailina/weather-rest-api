package ru.svetlanailina.weatherrestapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {
    private Location location;
    private Current current;
    private Forecast forecast;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private String name;
        private String region;
        private String country;
        private double lat;
        private double lon;
        @JsonProperty("tz_id")
        private String tzId;
        @JsonProperty("localtime_epoch")
        private long localtimeEpoch;
        private String localtime;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        @JsonProperty("temp_c")
        private double tempC;
        @JsonProperty("wind_kph")
        private double windKph;
        private Condition condition;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Condition {
            private String text;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Forecast {
        private ForecastDay[] forecastday;


        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ForecastDay {
            private String date;
            private Day day;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Day {
        @JsonProperty("avgtemp_c")
        private double avgTempC;
        @JsonProperty("maxwind_kph")
        private double maxWindKph;
        private Current.Condition condition;
    }
}
