package servlet;


import dto.CitySearchResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Session;
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

@WebServlet(urlPatterns = {"/search"})
public class SearchServlet extends BaseServlet{

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

                WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                templateEngine.process("search.html", context, resp.getWriter());
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

        if (sessionIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            UUID sessionId = UUID.fromString(sessionIdStr);
            Optional<Session> sessionOpt = sessionService.getValidSession(sessionId);

            if (sessionOpt.isPresent()) {
                List<CitySearchResponseDto> cities = locationService.searchCities(cityName);
                WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                context.setVariable("cities", cities);

                if (cities.isEmpty()) {
                    context.setVariable("error", "No cities found with the name: " + cityName);
                }

                templateEngine.process("searchResults.html", context, resp.getWriter());
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (Exception e) {
            WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
            context.setVariable("error", "An error occurred while searching for cities. Please try again later.");
            templateEngine.process("search.html", context, resp.getWriter());
        }
    }
}
