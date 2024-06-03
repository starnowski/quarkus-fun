package com.github.starnowski.quarkus.fun.liquid;

import jakarta.enterprise.context.ApplicationScoped;
import liqp.Template;
import liqp.TemplateParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ApplicationScoped
public class DefaultTemplateSupplier implements TemplateSupplier {
    @Override
    public Template get(String templateFile) throws IOException {
        return TemplateParser.DEFAULT.parse(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(templateFile).getFile()).getPath())));
    }
}
