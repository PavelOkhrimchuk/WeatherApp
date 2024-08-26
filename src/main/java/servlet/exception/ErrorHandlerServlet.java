package servlet.exception;

import exception.InvalidCredentialsException;
import exception.UserAlreadyExistsException;
import exception.WeakPasswordException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import servlet.BaseServlet;
import util.ContextUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet("/error-handler")
public class ErrorHandlerServlet extends BaseServlet {


    private static final Logger LOGGER = Logger.getLogger(ErrorHandlerServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleError(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleError(req, resp);
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Получаем информацию об ошибке
        Throwable throwable = (Throwable) req.getAttribute("jakarta.servlet.error.exception");
        Integer statusCode = (Integer) req.getAttribute("jakarta.servlet.error.status_code");
        String servletName = (String) req.getAttribute("jakarta.servlet.error.servlet_name");
        String requestUri = (String) req.getAttribute("jakarta.servlet.error.request_uri");

        if (servletName == null) {
            servletName = "Unknown";
        }

        if (requestUri == null) {
            requestUri = "Unknown";
        }

        // Логируем ошибку
        LOGGER.log(Level.SEVERE, "Error occurred in servlet: " + servletName + " at " + requestUri, throwable);

        // Настраиваем контекст для передачи на страницу с ошибкой
        WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
        context.setVariable("statusCode", statusCode);
        context.setVariable("servletName", servletName);
        context.setVariable("requestUri", requestUri);
        context.setVariable("exceptionMessage", throwable != null ? throwable.getMessage() : "Unknown error");


        if (throwable instanceof InvalidCredentialsException) {
            context.setVariable("errorMessage", throwable.getMessage());
            templateEngine.process("login.html", context, resp.getWriter());
        } else if (throwable instanceof UserAlreadyExistsException || throwable instanceof WeakPasswordException) {
            context.setVariable("errorMessage", throwable.getMessage());
            templateEngine.process("register.html", context, resp.getWriter());
        } else if (statusCode != null) {
            switch (statusCode) {
                case 404:
                    context.setVariable("errorMessage", "Sorry, the page you're looking for does not exist.");
                    templateEngine.process("error/404.html", context, resp.getWriter());
                    break;
                case 500:
                    context.setVariable("errorMessage", "An unexpected error occurred on the server.");
                    templateEngine.process("error/500.html", context, resp.getWriter());
                    break;
                default:
                    context.setVariable("errorMessage", "An unexpected error occurred.");
                    templateEngine.process("error/general.html", context, resp.getWriter());
                    break;
            }
        } else {

            context.setVariable("errorMessage", "An unexpected error occurred.");
            templateEngine.process("error/general.html", context, resp.getWriter());
        }
    }
}
