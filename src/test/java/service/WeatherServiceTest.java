package service;


import com.fasterxml.jackson.databind.ObjectMapper;
import dto.main.weather.WeatherResponseDto;
import exception.location.CityNotFoundException;
import exception.location.InvalidCityNameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherServiceTest {



    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherService = new WeatherService(mockHttpClient, new ObjectMapper());
    }

    @Test
    void testGetWeatherByCity_SuccessfulResponse() throws IOException, InterruptedException {

        String mockResponseBody = "{ \"name\": \"London\", \"cod\": 200 }";
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        setUpHttpClientMock();


        Optional<WeatherResponseDto> response = weatherService.getWeatherByCity("London");


        assertTrue(response.isPresent());
        assertEquals("London", response.get().getName());
    }

    @Test
    void testGetWeatherByCity_CityNotFound() throws IOException, InterruptedException {

        String mockResponseBody = "{ \"cod\": \"404\", \"message\": \"city not found\" }";
        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        setUpHttpClientMock();


        assertThrows(CityNotFoundException.class, () -> {
            weatherService.getWeatherByCity("InvalidCity");
        });
    }

    @Test
    void testGetWeatherByCity_InvalidCityName() {

        assertThrows(InvalidCityNameException.class, () -> {
            weatherService.getWeatherByCity("");
        });
    }

    private void setUpHttpClientMock() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);
    }


}