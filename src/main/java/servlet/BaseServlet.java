package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import util.ContextUtil;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    protected TemplateEngine templateEngine;
    protected WebContext context;

    @Override
    public void init() throws ServletException {
        templateEngine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        context = ContextUtil.buildWebContext(req, resp, getServletContext());

        super.service(req, resp);
    }
}
