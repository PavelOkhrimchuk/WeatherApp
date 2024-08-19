package servlet;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import repository.UserRepository;
import util.HibernateUtil;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends BaseServlet{
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.userRepository = new UserRepository(HibernateUtil.getSessionFactory());
        } catch (Exception e) {
            throw new ServletException("Failed to initialize UserRepository", e);
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

        Optional<User> userOpt = userRepository.findByLogin(login);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user);

                setCookie(resp, "JSESSIONID", session.getId(), -1);


                resp.sendRedirect(req.getContextPath() + "/profile");
            } else {
                context.setVariable("error", "Неправильный логин или пароль");
                templateEngine.process("login.html", context, resp.getWriter());
            }
        } else {
            context.setVariable("error", "Неправильный логин или пароль");
            templateEngine.process("login.html", context, resp.getWriter());
        }
    }
}
