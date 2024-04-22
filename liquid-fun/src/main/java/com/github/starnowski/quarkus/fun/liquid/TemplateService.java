package com.github.starnowski.quarkus.fun.liquid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.enterprise.context.ApplicationScoped;
import liqp.Template;
import liqp.TemplateParser;
import liqp.org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ApplicationScoped
public class TemplateService {

    public String covert(String templateFile, String payload) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        ObjectMapper jsonMapper = new ObjectMapper();

        JsonNode xmlNode = xmlMapper.readTree(payload);
        String json = jsonMapper.writeValueAsString(xmlNode);
        Template template = TemplateParser.DEFAULT.parse(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(templateFile).getFile()).getPath())));
//        Template template = new TemplateParser.Builder().build().parse(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(templateFile).getFile()).getPath())));
        return template.render(json);
    }
}
