package servlet.authentication;


import dto.user.UserLoginDto;
import exception.user.InvalidCredentialsException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Session;
import model.User;
import repository.SessionRepository;
import repository.UserRepository;
import service.SessionService;
import service.UserService;
import servlet.BaseServlet;
import util.HibernateUtil;

import java.io.IOException;

@WebServlet({"/login", "/"})

public class LoginServlet extends BaseServlet {

    private SessionService sessionService;

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.sessionService = new SessionService(new SessionRepository(HibernateUtil.getSessionFactory()));
            this.userService = new UserService(new UserRepository(HibernateUtil.getSessionFactory()));
        } catch (Exception e) {
            throw new ServletException("Failed to initialize LoginServlet", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (templateEngine == null || context == null) {
            throw new ServletException("Template engine or context is not initialized");
        }

        templateEngine.process("login.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setLogin(login);
        loginDto.setPassword(password);

        try {
            User user = userService.authenticateUser(loginDto);
            Session session = sessionService.createSession(user, 2);
            setCookie(resp, "JSESSIONID", session.getId().toString(), 3600);
            resp.sendRedirect(req.getContextPath() + "/locations");
        } catch (InvalidCredentialsException e) {
            context.setVariable("error", e.getMessage());
            templateEngine.process("login.html", context, resp.getWriter());
        } catch (Exception e) {
            context.setVariable("error", "An unexpected error occurred. Please try again later.");
            templateEngine.process("login.html", context, resp.getWriter());
        }
    }

}
