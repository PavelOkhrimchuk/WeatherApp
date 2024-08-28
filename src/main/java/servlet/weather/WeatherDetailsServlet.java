package servlet.weather;

import dto.main.weather.WeatherResponseDto;
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
import util.TimeUtil;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/locations/weather/details")
public class WeatherDetailsServlet extends BaseServlet {


    private static final String TIME_ZONE = "Europe/Moscow";

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
            resp.sendRedirect(getServletContext().getContextPath() + "/locations");
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

                    String sunriseTime = TimeUtil.formatEpochSecondsToTime(weather.getSys().getSunrise(), TIME_ZONE);
                    String sunsetTime = TimeUtil.formatEpochSecondsToTime(weather.getSys().getSunset(), TIME_ZONE);

                    WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                    context.setVariable("weather", weather);
                    context.setVariable("location", location);
                    context.setVariable("sunriseTime", sunriseTime);
                    context.setVariable("sunsetTime", sunsetTime);

                    templateEngine.process("weather_details.html", context, resp.getWriter());
                } else {
                    resp.sendRedirect(getServletContext().getContextPath() + "/locations");
                }
            } else {
                resp.sendRedirect(getServletContext().getContextPath() + "/locations");
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(getServletContext().getContextPath() + "/locations");
        }
    }
}



