package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CoordinatesDto;
import dto.WeatherResponseDto;
import model.Location;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class WeatherService {

    private static final String API_KEY = "700daeb1e0b92b52a99f2c05f092db91";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public Optional<WeatherResponseDto> getWeatherByCity(String cityName) {
        HttpRequest request = buildRequestByCity(cityName);
        return sendRequest(request);
    }

    public Optional<WeatherResponseDto> getWeatherByCoordinates(double lat, double lon) {
        HttpRequest request = buildRequestByCoordinates(lat, lon);
        return sendRequest(request);
    }

    private HttpRequest buildRequestByCity(String cityName) {
        String url = String.format("%s?q=%s&appid=%s", BASE_URL, cityName, API_KEY);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }

    private HttpRequest buildRequestByCoordinates(double lat, double lon) {
        String url = String.format("%s?lat=%f&lon=%f&appid=%s", BASE_URL, lat, lon, API_KEY);
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }

    private Optional<WeatherResponseDto> sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                WeatherResponseDto weatherResponse = objectMapper.readValue(response.body(), WeatherResponseDto.class);
                return Optional.of(weatherResponse);
            } else {
                System.err.println("Error: " + response.statusCode() + " " + response.body());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            return Optional.empty();
        }
    }




}
