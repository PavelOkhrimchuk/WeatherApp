package servlet.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlet.BaseServlet;

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
        // Получение атрибутов ошибки, установленных контейнером сервлета
        Integer statusCode = (Integer) req.getAttribute("jakarta.servlet.error.status_code");
        Throwable throwable = (Throwable) req.getAttribute("jakarta.servlet.error.exception");
        String requestUri = (String) req.getAttribute("jakarta.servlet.error.request_uri");

        if (requestUri == null) {
            requestUri = "Unknown";
        }

        if (statusCode != null) {
            switch (statusCode) {
                case 400 -> {
                    LOGGER.log(Level.WARNING, "Bad Request: {0}", throwable != null ? throwable.getMessage() : "N/A");
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Request");
                }
                case 401 -> {
                    LOGGER.log(Level.WARNING, "Unauthorized access attempt: {0}", requestUri);
                    resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                }
                case 404 -> {
                    LOGGER.log(Level.INFO, "Resource not found: {0}", requestUri);
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
                }
                case 500 -> {
                    LOGGER.log(Level.SEVERE, "Server error occurred: {0}", throwable != null ? throwable.getMessage() : "N/A");
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
                }
                default -> {
                    LOGGER.log(Level.SEVERE, "Unexpected error occurred: {0}", throwable != null ? throwable.getMessage() : "N/A");
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected Error");
                }
            }
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error details are not available");
        }
    }
}
