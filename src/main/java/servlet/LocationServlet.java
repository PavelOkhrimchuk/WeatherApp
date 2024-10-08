package servlet;

import exception.location.CityNotFoundException;
import exception.location.InvalidCityNameException;
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
import java.util.Optional;
import java.util.UUID;


@WebServlet(urlPatterns = {"/locations", "/locations/delete", "/locations/add"})
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
        String sessionIdStr = getCookieValue(req, "JSESSIONID");
        String cityName = req.getParameter("cityName");
        String latitudeStr = req.getParameter("latitude");
        String longitudeStr = req.getParameter("longitude");
        String locationIdStr = req.getParameter("locationId");

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
                if (locationIdStr != null) {
                    handleLocationDeletion(req, resp, user, locationIdStr);
                } else if (cityName != null && latitudeStr != null && longitudeStr != null) {
                    handleCityAddition(req, resp, user, cityName, latitudeStr, longitudeStr);
                } else {

                    renderErrorPage(req, resp, "Invalid data. Please try again.", sessionIdStr);
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (Exception e) {
            renderErrorPage(req, resp, "An unexpected error occurred. Please try again later.", sessionIdStr);
        }
    }

    private void handleCityAddition(HttpServletRequest req, HttpServletResponse resp, User user, String cityName, String latitudeStr, String longitudeStr) throws IOException, ServletException {
        try {
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);

            Location location = new Location();
            location.setName(cityName);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setUser(user);

            locationService.saveLocation(location);

            resp.sendRedirect(req.getContextPath() + "/locations");
        } catch (NumberFormatException e) {
            renderErrorPage(req, resp, "Failed to add the location. Please check the coordinates.", getCookieValue(req, "JSESSIONID"));
        }
    }

    private void handleLocationDeletion(HttpServletRequest req, HttpServletResponse resp, User user, String locationIdStr) throws IOException, ServletException {
        try {
            int locationId = Integer.parseInt(locationIdStr);
            Optional<Location> locationOpt = locationService.getLocationById(locationId);

            if (locationOpt.isPresent() && locationOpt.get().getUser().equals(user)) {
                locationService.deleteLocationFromUser(locationOpt.get());
            }
            resp.sendRedirect(req.getContextPath() + "/locations");
        } catch (Exception e) {
            renderErrorPage(req, resp, "An unexpected error occurred. Please try again later.", getCookieValue(req, "JSESSIONID"));
        }
    }

    private void renderErrorPage(HttpServletRequest req, HttpServletResponse resp, String errorMessage, String sessionIdStr) throws IOException, ServletException {
        WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
        UUID sessionId = UUID.fromString(sessionIdStr);
        User user = sessionService.getValidSession(sessionId).orElseThrow().getUser();
        List<Location> locations = locationService.getLocationsByUser(user);

        context.setVariable("locations", locations);
        context.setVariable("error", errorMessage);
        templateEngine.process("locations.html", context, resp.getWriter());
    }




}




