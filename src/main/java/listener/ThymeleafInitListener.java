package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.thymeleaf.TemplateEngine;
import util.ThymeleafConfig;

public class ThymeleafInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        TemplateEngine templateEngine = ThymeleafConfig.createTemplateEngine(sce.getServletContext());
        sce.getServletContext().setAttribute("templateEngine", templateEngine);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
