package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import util.ContextUtil;
import util.ThymeleafUtil;

import java.io.IOException;


@WebServlet("/profile")
public class ProfileServlet extends BaseServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }


        WebContext context = ContextUtil.buildWebContext(req, resp, getServletContext());
        context.setVariable("user", user);

        resp.setContentType("text/html");
        templateEngine.process("profile.html", context, resp.getWriter());
    }


}
