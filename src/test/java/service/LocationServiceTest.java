package service;

import dto.main.CitySearchResponseDto;
import dto.main.forecast.WeatherForecastResponseDto;
import dto.main.weather.WeatherResponseDto;
import model.Location;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.LocationRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationServiceTest {


    @Mock
    private LocationRepository locationRepository;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private LocationService locationService;

    private User user;
    private Location location1;
    private Location location2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setLogin("testUser");
        user.setPassword("testPassword");

        location1 = new Location();
        location1.setId(1);
        location1.setName("Moscow");
        location1.setLatitude(55.7558);
        location1.setLongitude(37.6173);
        location1.setUser(user);

        location2 = new Location();
        location2.setId(2);
        location2.setName("Saint Petersburg");
        location2.setLatitude(59.9343);
        location2.setLongitude(30.3351);
        location2.setUser(user);
    }

    @Test
    void testGetLocationsByUser() {

        when(locationRepository.findByUser(user)).thenReturn(List.of(location1, location2));

        List<Location> locations = locationService.getLocationsByUser(user);

        assertNotNull(locations, "Location list should not be null");
        assertEquals(2, locations.size(), "There should be 2 locations returned");
        verify(locationRepository, times(1)).findByUser(user);
    }

    @Test
    void testDeleteLocationFromUser() {

        doNothing().when(locationRepository).deleteById(location1.getId());

        locationService.deleteLocationFromUser(location1);

        verify(locationRepository, times(1)).deleteById(location1.getId());
    }

    @Test
    void testGetLocationById_Found() {

        when(locationRepository.findById(1)).thenReturn(Optional.of(location1));

        Optional<Location> result = locationService.getLocationById(1);

        assertTrue(result.isPresent(), "Location should be found");
        assertEquals(location1, result.get(), "Returned location should match the expected location");
        verify(locationRepository, times(1)).findById(1);
    }

    @Test
    void testGetLocationById_NotFound() {

        when(locationRepository.findById(3)).thenReturn(Optional.empty());

        Optional<Location> result = locationService.getLocationById(3);

        assertFalse(result.isPresent(), "Location should not be found");
        verify(locationRepository, times(1)).findById(3);
    }

    @Test
    void testGetWeatherForLocation_Success() throws IOException {

        WeatherResponseDto weatherResponse = WeatherResponseDto.builder()
                .id(location1.getId())
                .name(location1.getName())
                .build();

        when(weatherService.getWeatherByCoordinates(location1.getLatitude(), location1.getLongitude()))
                .thenReturn(Optional.of(weatherResponse));

        Optional<WeatherResponseDto> result = locationService.getWeatherForLocation(location1);

        assertTrue(result.isPresent(), "Weather should be retrieved");
        assertEquals(weatherResponse, result.get(), "Retrieved weather should match the expected weather");
        verify(weatherService, times(1))
                .getWeatherByCoordinates(location1.getLatitude(), location1.getLongitude());
    }

    @Test
    void testGetWeatherForLocation_Failure() throws IOException {

        when(weatherService.getWeatherByCoordinates(location1.getLatitude(), location1.getLongitude()))
                .thenReturn(Optional.empty());

        Optional<WeatherResponseDto> result = locationService.getWeatherForLocation(location1);

        assertFalse(result.isPresent(), "Weather should not be retrieved");
        verify(weatherService, times(1))
                .getWeatherByCoordinates(location1.getLatitude(), location1.getLongitude());
    }

    @Test
    void testGetForecastForLocation_Success() throws IOException {

        WeatherForecastResponseDto forecastResponse = WeatherForecastResponseDto.builder()
                .city(null)
                .build();

        when(weatherService.getForecastByCoordinates(location2.getLatitude(), location2.getLongitude()))
                .thenReturn(Optional.of(forecastResponse));

        Optional<WeatherForecastResponseDto> result = locationService.getForecastForLocation(location2);

        assertTrue(result.isPresent(), "Weather forecast should be retrieved");
        assertEquals(forecastResponse, result.get(), "Retrieved forecast should match the expected forecast");
        verify(weatherService, times(1))
                .getForecastByCoordinates(location2.getLatitude(), location2.getLongitude());
    }

    @Test
    void testGetForecastForLocation_Failure() throws IOException {

        when(weatherService.getForecastByCoordinates(location2.getLatitude(), location2.getLongitude()))
                .thenReturn(Optional.empty());

        Optional<WeatherForecastResponseDto> result = locationService.getForecastForLocation(location2);

        assertFalse(result.isPresent(), "Weather forecast should not be retrieved");
        verify(weatherService, times(1))
                .getForecastByCoordinates(location2.getLatitude(), location2.getLongitude());
    }

    @Test
    void testSearchCities() {

        String cityName = "Moscow";
        CitySearchResponseDto city1 = new CitySearchResponseDto();
        CitySearchResponseDto city2 = new CitySearchResponseDto();

        when(weatherService.searchCitiesByName(cityName)).thenReturn(List.of(city1, city2));

        List<CitySearchResponseDto> result = locationService.searchCities(cityName);

        assertNotNull(result, "City list should not be null");
        assertEquals(2, result.size(), "There should be 2 cities found");
        verify(weatherService, times(1)).searchCitiesByName(cityName);
    }






}