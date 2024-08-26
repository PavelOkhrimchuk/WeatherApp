package servlet.weather;

import dto.WeatherResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Location;
import org.thymeleaf.context.WebContext;
import repository.LocationRepository;
import service.LocationService;
import service.WeatherService;
import servlet.BaseServlet;
import util.ContextUtil;
import util.HibernateUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet("/locations/weather/details")
public class WeatherDetailsServlet extends BaseServlet {


    private LocationService locationService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.locationService = new LocationService(new LocationRepository(HibernateUtil.getSessionFactory()), new WeatherService());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String locationIdStr = req.getParameter("id");

        if (locationIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/locations");
            return;
        }

        try {
            int locationId = Integer.parseInt(locationIdStr);
            Optional<Location> locationOpt = locationService.getLocationById(locationId);

            if (locationOpt.isPresent()) {
                Location location = locationOpt.get();
                Optional<WeatherResponseDto> weatherOpt = locationService.getWeatherForLocation(location);

                if (weatherOpt.isPresent()) {
                    WeatherResponseDto weather = weatherOpt.get();


                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");


                    ZonedDateTime sunriseUtc = Instant.ofEpochSecond(weather.getSys().getSunrise()).atZone(ZoneId.of("UTC"));
                    ZonedDateTime sunsetUtc = Instant.ofEpochSecond(weather.getSys().getSunset()).atZone(ZoneId.of("UTC"));

                    String sunriseTime = sunriseUtc.withZoneSameInstant(ZoneId.of("Europe/Moscow")).format(formatter);
                    String sunsetTime = sunsetUtc.withZoneSameInstant(ZoneId.of("Europe/Moscow")).format(formatter);


                    WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                    context.setVariable("weather", weather);
                    context.setVariable("location", location);
                    context.setVariable("sunriseTime", sunriseTime);
                    context.setVariable("sunsetTime", sunsetTime);

                    templateEngine.process("weather_details.html", context, resp.getWriter());
                } else {
                    resp.sendRedirect(req.getContextPath() + "/locations");
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/locations");
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/locations");
        }
    }
}
