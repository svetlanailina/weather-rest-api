package ru.svetlanailina.weatherrestapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.svetlanailina.weatherrestapi.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final Map<String, WeatherProvider> providers;
    private final String defaultCity;
    private final String defaultSource;
    private final ExecutorService executorService;

    @Autowired
    public WeatherService(List<WeatherProvider> providerList,
                          @Value("${weather.default-city}") String defaultCity,
                          @Value("${weather.source}") String defaultSource,
                          @Value("${weather.thread.pool.size:2}") int threadPoolSize) {
        this.defaultCity = defaultCity;
        this.defaultSource = defaultSource;
        this.providers = providerList.stream().collect(Collectors.toMap(p -> p.getClass().getSimpleName(), p -> p));
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public List<WeatherResponse> getTodayWeather(String city, String source) {
        final String finalCity = (city == null || city.isEmpty()) ? defaultCity : city;
        final String finalSource = (source == null || source.isEmpty()) ? defaultSource : source;

        List<WeatherResponse> responses = new ArrayList<>();
        List<Callable<WeatherResponse>> callables = new ArrayList<>();

        if ("yandex".equalsIgnoreCase(finalSource) || "all".equalsIgnoreCase(finalSource)) {
            callables.add(() -> providers.get("YandexWeatherService").getTodayWeather(finalCity));
        }
        if ("weatherapi".equalsIgnoreCase(finalSource) || "all".equalsIgnoreCase(finalSource)) {
            callables.add(() -> providers.get("WeatherApiService").getTodayWeather(finalCity));
        }

        executeCallables(callables, responses);

        return responses;
    }

    public List<WeatherResponse> getWeekWeather(String city, String source) {
        final String finalCity = (city == null || city.isEmpty()) ? defaultCity : city;
        final String finalSource = (source == null || source.isEmpty()) ? defaultSource : source;

        List<WeatherResponse> responses = new ArrayList<>();
        List<Callable<List<WeatherResponse>>> callables = new ArrayList<>();

        if ("yandex".equalsIgnoreCase(finalSource) || "all".equalsIgnoreCase(finalSource)) {
            callables.add(() -> providers.get("YandexWeatherService").getWeekWeather(finalCity));
        }
        if ("weatherapi".equalsIgnoreCase(finalSource) || "all".equalsIgnoreCase(finalSource)) {
            callables.add(() -> providers.get("WeatherApiService").getWeekWeather(finalCity));
        }

        try {
            List<Future<List<WeatherResponse>>> futures = executorService.invokeAll(callables);
            for (Future<List<WeatherResponse>> future : futures) {
                try {
                    responses.addAll(future.get());
                } catch (ExecutionException e) {
                    logger.error("Error fetching weather data: {}", e.getMessage(), e);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Error executing weather requests: {}", e.getMessage(), e);
        }

        return responses;
    }

    private void executeCallables(List<Callable<WeatherResponse>> callables, List<WeatherResponse> responses) {
        try {
            List<Future<WeatherResponse>> futures = executorService.invokeAll(callables);
            for (Future<WeatherResponse> future : futures) {
                try {
                    responses.add(future.get());
                } catch (ExecutionException e) {
                    logger.error("Error fetching weather data: {}", e.getMessage(), e);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Error executing weather requests: {}", e.getMessage(), e);
        }
    }
}
