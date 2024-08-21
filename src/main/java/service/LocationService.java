package service;

import dto.WeatherResponseDto;
import model.Location;
import model.User;
import repository.LocationRepository;

import java.util.List;
import java.util.Optional;

public class LocationService {


    private final LocationRepository locationRepository;
    private final WeatherService weatherService;

    public LocationService(LocationRepository locationRepository, WeatherService weatherService) {
        this.locationRepository = locationRepository;
        this.weatherService = weatherService;
    }


    public Location addLocation(String name, double latitude, double longitude, User user) {
        Optional<WeatherResponseDto> weatherOpt = weatherService.getWeatherByCoordinates(latitude, longitude);

        if (weatherOpt.isPresent()) {

            Location location = new Location();
            location.setName(name);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setUser(user);

            return locationRepository.save(location);
        } else {
            throw new RuntimeException("Не удалось получить данные о погоде для координат: " + latitude + ", " + longitude);
        }


    }

    public List<Location> getAllLocationsForUser(User user) {
        return locationRepository.findByUser(user);
    }

    public Optional<Location> getLocationById(Integer id) {
        return locationRepository.findById(id);
    }

    public void deleteLocationById(Integer id) {
        locationRepository.deleteById(id);
    }

    public Optional<WeatherResponseDto> getWeatherForLocation(Location location) {
        return weatherService.getWeatherByCoordinates(location.getLatitude(), location.getLongitude());
    }
}
