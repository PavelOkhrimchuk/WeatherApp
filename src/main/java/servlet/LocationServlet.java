package servlet;

import dto.WeatherResponseDto;
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

                Map<Location, WeatherResponseDto> locationWeatherMap = locationService.getLocationWeatherMap(locations);

                WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                context.setVariable("locations", locations);
                context.setVariable("locationWeatherMap", locationWeatherMap);
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

        try {
            UUID sessionId = UUID.fromString(sessionIdStr);
            Optional<Session> sessionOpt = sessionService.getValidSession(sessionId);

            if (sessionOpt.isPresent()) {
                User user = sessionOpt.get().getUser();


                String locationIdStr = req.getParameter("locationId");
                if (locationIdStr != null) {
                    int locationId = Integer.parseInt(locationIdStr);
                    Optional<Location> locationOpt = locationService.getLocationById(locationId);

                    if (locationOpt.isPresent()) {
                        Location location = locationOpt.get();

                        // Удаление локации, если она принадлежит текущему пользователю
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
                        WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                        context.setVariable("error", "Не удалось найти город или добавить локацию.");
                        doGet(req, resp);
                    }
                }
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }




}
