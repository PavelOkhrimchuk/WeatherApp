package servlet;

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


@WebServlet("/locations")
public class LocationServlet extends BaseServlet {

    private LocationService locationService;
    private SessionService sessionService;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.locationService = new LocationService(new LocationRepository(HibernateUtil.getSessionFactory()), new WeatherService());
            this.sessionService = new SessionService(new SessionRepository(HibernateUtil.getSessionFactory()));
        } catch (Exception e) {
            throw new ServletException("Failed to initialize LocationServlet", e);
        }
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
                List<Location> locations = locationService.getAllLocationsForUser(user);

                WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                context.setVariable("locations", locations);
                context.setVariable("user", user);

                templateEngine.process("locations.html", context, resp.getWriter());
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
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

                if ("add".equals(action)) {
                    String name = req.getParameter("name");
                    String latitudeStr = req.getParameter("latitude");
                    String longitudeStr = req.getParameter("longitude");

                    if (name == null || latitudeStr == null || longitudeStr == null) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
                        return;
                    }

                    try {
                        double latitude = Double.parseDouble(latitudeStr);
                        double longitude = Double.parseDouble(longitudeStr);

                        locationService.addLocation(name, latitude, longitude, user);
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number format for latitude or longitude");
                        return;
                    }
                } else if ("delete".equals(action)) {
                    String locationIdStr = req.getParameter("locationId");

                    if (locationIdStr == null) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing locationId parameter");
                        return;
                    }

                    try {
                        Integer locationId = Integer.parseInt(locationIdStr);
                        locationService.deleteLocationById(locationId);
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number format for locationId");
                        return;
                    }
                }

                resp.sendRedirect(req.getContextPath() + "/locations");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
        }
    }


}
