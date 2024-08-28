package servlet.authentication;


import dto.user.UserRegistrationDto;
import exception.UserAlreadyExistsException;
import exception.WeakPasswordException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.thymeleaf.context.WebContext;
import repository.UserRepository;
import service.UserService;
import servlet.BaseServlet;
import util.ContextUtil;
import util.HibernateUtil;

import java.io.IOException;

@WebServlet("/register")
public class RegistrationServlet extends BaseServlet {

    private UserService userService;


    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService(new UserRepository(HibernateUtil.getSessionFactory()));
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

        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setLogin(login);
        registrationDto.setPassword(password);
        registrationDto.setConfirmPassword(confirmPassword);

        try {
            userService.registerUser(registrationDto);
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (UserAlreadyExistsException | WeakPasswordException e) {
            context.setVariable("error", e.getMessage());
            templateEngine.process("register.html", context, resp.getWriter());
        } catch (Exception e) {
            context.setVariable("error", "An unexpected error occurred. Please try again later.");
            templateEngine.process("register.html", context, resp.getWriter());
        }
    }

}

