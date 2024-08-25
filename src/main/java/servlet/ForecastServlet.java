package servlet;


import dto.WeatherForecastResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Location;
import org.thymeleaf.context.WebContext;
import repository.LocationRepository;

import service.LocationService;
import service.WeatherService;
import util.ContextUtil;
import util.HibernateUtil;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/locations/forecast")
public class ForecastServlet extends BaseServlet {


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
                Optional<WeatherForecastResponseDto> forecastOpt = locationService.getForecastForLocation(location);

                if (forecastOpt.isPresent()) {
                    WeatherForecastResponseDto forecast = forecastOpt.get();
                    WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                    context.setVariable("forecast", forecast);
                    context.setVariable("location", location);
                    templateEngine.process("forecast.html", context, resp.getWriter());
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
