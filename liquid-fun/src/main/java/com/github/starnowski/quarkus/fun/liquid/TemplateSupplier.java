package com.github.starnowski.quarkus.fun.liquid;

import liqp.Template;

import java.io.IOException;

public interface TemplateSupplier {

    Template get(String templateFile) throws IOException;
}
