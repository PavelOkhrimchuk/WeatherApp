package servlet;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import repository.SessionRepository;
import service.SessionService;
import util.HibernateUtil;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/logout")
public class LogoutServlet extends BaseServlet{

    private SessionService sessionService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.sessionService = new SessionService(new SessionRepository(HibernateUtil.getSessionFactory()));
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sessionIdStr = getCookieValue(req, "JSESSIONID");

        if (sessionIdStr != null) {
            UUID sessionId = UUID.fromString(sessionIdStr);
            sessionService.invalidateSession(sessionId);
            removeCookie(resp, "JSESSIONID");
        }

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
