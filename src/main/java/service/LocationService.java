package service;

import dto.main.CitySearchResponseDto;
import dto.main.forecast.WeatherForecastResponseDto;
import dto.main.weather.WeatherResponseDto;
import model.Location;
import model.User;
import repository.LocationRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LocationService {


    private final LocationRepository locationRepository;
    private final WeatherService weatherService;



    public LocationService(LocationRepository locationRepository, WeatherService weatherService) {
        this.locationRepository = locationRepository;
        this.weatherService = weatherService;
    }

    public List<Location> getLocationsByUser(User user) {
        return locationRepository.findByUser(user);
    }

    public void deleteLocationFromUser(Location location) {
        locationRepository.deleteById(location.getId());
    }

    public Optional<Location> getLocationById(int locationId) {
        return locationRepository.findById(locationId);
    }


    public Optional<WeatherResponseDto> getWeatherForLocation(Location location) throws IOException {
        return weatherService.getWeatherByCoordinates(location.getLatitude(), location.getLongitude());
    }

    public Optional<WeatherForecastResponseDto> getForecastForLocation(Location location) throws IOException {
        return weatherService.getForecastByCoordinates(location.getLatitude(), location.getLongitude());
    }


    public List<CitySearchResponseDto> searchCities(String cityName) {
        return weatherService.searchCitiesByName(cityName);
    }

    public void saveLocation(Location location) {
        try {
            locationRepository.save(location);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сохранить местоположение. Попробуйте снова.");
        }
    }

}