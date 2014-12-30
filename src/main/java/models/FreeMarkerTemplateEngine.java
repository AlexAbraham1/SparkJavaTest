package main.java.models;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.ModelAndView;
import spark.TemplateEngine;

public class FreeMarkerTemplateEngine extends TemplateEngine {

    private Configuration configuration;

    public FreeMarkerTemplateEngine()
    {
        this.configuration = createFreemarkerConfiguration();
    }


    @Override
    public String render(ModelAndView modelAndView) {

        try {
            StringWriter stringWriter = new StringWriter();

            Template template = configuration.getTemplate(modelAndView.getViewName());
            template.process(modelAndView.getModel(), stringWriter);

            return stringWriter.toString();
        } catch (IOException e){
            throw new IllegalArgumentException(e);
        } catch (TemplateException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Configuration createFreemarkerConfiguration() {

        try {
            Configuration retVal = new Configuration();
            retVal.setClassForTemplateLoading(FreeMarkerTemplateEngine.class, "freemarker");
            retVal.setDirectoryForTemplateLoading(new File("src/main/resources/public/ftl_templates"));
            return retVal;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

    }
}
