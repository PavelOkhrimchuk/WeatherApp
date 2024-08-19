package util;

import jakarta.servlet.ServletContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class ThymeleafUtil {

    public static TemplateEngine createTemplateEngine(ServletContext servletContext) {
        TemplateEngine templateEngine = new TemplateEngine();

        FileTemplateResolver templateResolver = new FileTemplateResolver();

        String templatePath = servletContext.getRealPath("/WEB-INF/");
        templateResolver.setPrefix(templatePath);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }
}
