package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.thymeleaf.TemplateEngine;
import util.ThymeleafUtil;


@WebListener
public class ThymeleafInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        TemplateEngine templateEngine = ThymeleafUtil.createTemplateEngine(sce.getServletContext());
        sce.getServletContext().setAttribute("templateEngine", templateEngine);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
