package service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

public class WeatherService {

    private static final String API_KEY = "700daeb1e0b92b52a99f2c05f092db91";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public Optional<WeatherResponseDto> getWeatherByCity(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new InvalidCityNameException("City name cannot be empty.");
        }
        String encodedCityName = URLEncoder.encode(cityName.trim(), StandardCharsets.UTF_8);
        HttpRequest request = buildRequest("weather", "q=" + encodedCityName);
        return sendRequest(request, WeatherResponseDto.class);
    }

    public Optional<WeatherResponseDto> getWeatherByCoordinates(double lat, double lon) {
        HttpRequest request = buildRequest("weather", String.format("lat=%f&lon=%f", lat, lon));
        return sendRequest(request, WeatherResponseDto.class);
    }

    public Optional<WeatherForecastResponseDto> getForecastByCoordinates(double lat, double lon) {
        HttpRequest request = buildRequest("forecast", String.format("lat=%f&lon=%f", lat, lon));
        return sendRequest(request, WeatherForecastResponseDto.class);
    }

    private HttpRequest buildRequest(String endpoint, String params) {
        String url = String.format("%s%s?%s&appid=%s", BASE_URL, endpoint, params, API_KEY);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }

    private <T> Optional<T> sendRequest(HttpRequest request, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                T responseDto = objectMapper.readValue(response.body(), responseType);
                return Optional.of(responseDto);
            } else if (response.statusCode() == 404) {
                throw new CityNotFoundException("City not found: " + request.uri().toString());
            } else {
                System.err.println("Error: " + response.statusCode() + " " + response.body());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
    }







}
