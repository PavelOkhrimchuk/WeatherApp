package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Session;
import model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import repository.SessionRepository;
import service.SessionService;
import util.ContextUtil;
import util.HibernateUtil;
import util.ThymeleafUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@WebServlet("/profile")
public class ProfileServlet extends BaseServlet{

    private SessionService sessionService;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.sessionService = new SessionService(new SessionRepository(HibernateUtil.getSessionFactory()));
        } catch (Exception e) {
            throw new ServletException("Failed to initialize SessionService", e);
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
                Session session = sessionOpt.get();
                User user = session.getUser();

                WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
                context.setVariable("user", user);

                resp.setContentType("text/html");
                templateEngine.process("profile.html", context, resp.getWriter());
            } else {
                resp.sendRedirect(req.getContextPath() + "/login");
            }
        } catch (IllegalArgumentException e) {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }


}
