package servlet;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.thymeleaf.context.WebContext;
import repository.UserRepository;
import util.ContextUtil;
import util.HibernateUtil;

import java.io.IOException;

@WebServlet("/register")
public class RegistrationServlet extends BaseServlet {
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userRepository = new UserRepository(HibernateUtil.getSessionFactory());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
        templateEngine.process("register", context, resp.getWriter());
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());

        if (!password.equals(confirmPassword)) {
            context.setVariable("error", "Пароли не совпадают");
            templateEngine.process("register", context, resp.getWriter());
            return;
        }

        if (userRepository.findByLogin(login).isPresent()) {
            context.setVariable("error", "Логин уже используется");
            templateEngine.process("register", context, resp.getWriter());
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setLogin(login);
        user.setPassword(hashedPassword);

        userRepository.save(user);

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}