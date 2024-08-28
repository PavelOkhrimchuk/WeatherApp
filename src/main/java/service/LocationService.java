package service;

import dto.main.forecast.WeatherForecastResponseDto;
import dto.main.weather.WeatherResponseDto;
import exception.location.CityNotFoundException;
import exception.location.InvalidCityNameException;
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

    public Optional<Location> addLocationByCityName(String cityName, User user) {
        try {
            Optional<WeatherResponseDto> weatherOpt = weatherService.getWeatherByCity(cityName);

            if (weatherOpt.isPresent()) {
                WeatherResponseDto weather = weatherOpt.get();
                Location location = new Location();
                location.setName(cityName);
                location.setLatitude(weather.getCoord().getLat());
                location.setLongitude(weather.getCoord().getLon());
                location.setUser(user);

                locationRepository.save(location);
                return Optional.of(location);
            } else {
                return Optional.empty();
            }
        } catch (InvalidCityNameException | CityNotFoundException e) {
            System.err.println("Error adding location: " + e.getMessage());
            throw e;
        }
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


}