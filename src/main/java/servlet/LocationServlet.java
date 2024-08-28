package servlet;

import dto.WeatherResponseDto;
import exception.CityNotFoundException;
import exception.InvalidCityNameException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Location;
import model.Session;
import model.User;
import org.thymeleaf.context.WebContext;
import repository.LocationRepository;
import repository.SessionRepository;
import service.LocationService;
import service.SessionService;
import service.WeatherService;
import util.ContextUtil;
import util.HibernateUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@WebServlet(urlPatterns = {"/locations", "/locations/delete"})
public class LocationServlet extends BaseServlet {

    private LocationService locationService;
    private SessionService sessionService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.locationService = new LocationService(new LocationRepository(HibernateUtil.getSessionFactory()), new WeatherService());
        this.sessionService = new SessionService(new SessionRepository(HibernateUtil.getSessionFactory()));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionIdStr = getCookieValue(req, "JSESSIONID");

        if (sessionIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            UUID sessionId = UUID.fromString(sessionIdStr);
            Optional<Session> sessionOpt = sessionService.getValidSession(sessionId);

            if (sessionOpt.isPresent()) {
                User user = sessionOpt.get().getUser();
                List<Location> locations = locationService.getLocationsByUser(user);

                WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                context.setVariable("locations", locations);


                String error = (String) req.getAttribute("error");
                if (error != null) {
                    context.setVariable("error", error);
                }

                templateEngine.process("locations.html", context, resp.getWriter());
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cityName = req.getParameter("cityName");
        String sessionIdStr = getCookieValue(req, "JSESSIONID");

        if (sessionIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());

        try {
            UUID sessionId = UUID.fromString(sessionIdStr);
            Optional<Session> sessionOpt = sessionService.getValidSession(sessionId);

            if (sessionOpt.isPresent()) {
                User user = sessionOpt.get().getUser();

                List<Location> locations = locationService.getLocationsByUser(user);
                context.setVariable("locations", locations);

                String locationIdStr = req.getParameter("locationId");
                if (locationIdStr != null) {
                    int locationId = Integer.parseInt(locationIdStr);
                    Optional<Location> locationOpt = locationService.getLocationById(locationId);

                    if (locationOpt.isPresent()) {
                        Location location = locationOpt.get();

                        if (location.getUser().equals(user)) {
                            locationService.deleteLocationFromUser(location);
                        }
                    }

                    resp.sendRedirect(req.getContextPath() + "/locations");
                } else if (cityName != null) {
                    Optional<Location> locationOpt = locationService.addLocationByCityName(cityName, user);

                    if (locationOpt.isPresent()) {
                        resp.sendRedirect(req.getContextPath() + "/locations");
                    } else {
                        context.setVariable("error", "Failed to find a city or add a location");
                        templateEngine.process("locations.html", context, resp.getWriter());
                    }
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (InvalidCityNameException e) {
            context.setVariable("error", e.getMessage());
            List<Location> locations = locationService.getLocationsByUser(sessionService.getValidSession(UUID.fromString(sessionIdStr)).orElseThrow().getUser());
            context.setVariable("locations", locations);
            templateEngine.process("locations.html", context, resp.getWriter());
        } catch (CityNotFoundException e) {
            context.setVariable("error", "City not found.");
            List<Location> locations = locationService.getLocationsByUser(sessionService.getValidSession(UUID.fromString(sessionIdStr)).orElseThrow().getUser());
            context.setVariable("locations", locations);
            templateEngine.process("locations.html", context, resp.getWriter());
        } catch (IllegalArgumentException e) {
            context.setVariable("error", "Invalid data format.");
            List<Location> locations = locationService.getLocationsByUser(sessionService.getValidSession(UUID.fromString(sessionIdStr)).orElseThrow().getUser());
            context.setVariable("locations", locations);
            templateEngine.process("locations.html", context, resp.getWriter());
        } catch (Exception e) {
            context.setVariable("error", "An unexpected error occurred.");
            List<Location> locations = locationService.getLocationsByUser(sessionService.getValidSession(UUID.fromString(sessionIdStr)).orElseThrow().getUser());
            context.setVariable("locations", locations);
            templateEngine.process("locations.html", context, resp.getWriter());
        }
    }








}
