package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.main.CitySearchResponseDto;
import dto.main.forecast.WeatherForecastResponseDto;
import dto.main.weather.WeatherResponseDto;
import exception.location.CityNotFoundException;
import exception.location.InvalidCityNameException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class WeatherService {

    private static final String API_KEY = "700daeb1e0b92b52a99f2c05f092db91";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String GEO_URL = "https://api.openweathermap.org/geo/1.0/";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public Optional<WeatherResponseDto> getWeatherByCity(String cityName) {
        validateCityName(cityName);
        String encodedCityName = encodeCityName(cityName);
        String url = buildUrl("weather", "q=" + encodedCityName);
        return sendRequest(url, WeatherResponseDto.class);
    }

    public Optional<WeatherResponseDto> getWeatherByCoordinates(double lat, double lon) {
        String url = buildUrl("weather", String.format("lat=%f&lon=%f", lat, lon));
        return sendRequest(url, WeatherResponseDto.class);
    }

    public Optional<WeatherForecastResponseDto> getForecastByCoordinates(double lat, double lon) {
        String url = buildUrl("forecast", String.format("lat=%f&lon=%f", lat, lon));
        return sendRequest(url, WeatherForecastResponseDto.class);
    }

    public List<CitySearchResponseDto> searchCitiesByName(String cityName) {
        validateCityName(cityName);
        String encodedCityName = encodeCityName(cityName);
        String url = buildGeoUrl("direct", "q=" + encodedCityName + "&limit=5");
        return sendRequestForList(url, new TypeReference<>() {
        });
    }

    private void validateCityName(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new InvalidCityNameException("City name cannot be empty.");
        }
    }

    private String encodeCityName(String cityName) {
        return URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8);
    }

    private String buildUrl(String endpoint, String params) {
        return String.format("%s%s?%s&appid=%s", BASE_URL, endpoint, params, API_KEY);
    }

    private String buildGeoUrl(String endpoint, String params) {
        return String.format("%s%s?%s&appid=%s", GEO_URL, endpoint, params, API_KEY);
    }

    private <T> Optional<T> sendRequest(String url, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                T responseDto = objectMapper.readValue(response.body(), responseType);
                return Optional.of(responseDto);
            } else {
                handleError(response.statusCode(), response.body());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
    }

    private <T> List<T> sendRequestForList(String url, TypeReference<List<T>> typeReference) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), typeReference);
            } else {
                handleError(response.statusCode(), response.body());
                return List.of();
            }
        } catch (IOException | InterruptedException e) {
            return List.of();
        }
    }

    private void handleError(int statusCode, String responseBody) {
        if (statusCode == 404) {
            throw new CityNotFoundException("City not found.");
        } else {
            System.err.println("Error: " + statusCode + " " + responseBody);
        }
    }


}
