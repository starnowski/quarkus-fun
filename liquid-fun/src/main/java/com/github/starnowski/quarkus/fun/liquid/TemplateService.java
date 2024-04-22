package com.github.starnowski.quarkus.fun.liquid;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.AttributePropertyWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.enterprise.context.ApplicationScoped;
import liqp.Template;
import liqp.TemplateParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

    public String covertWithAttributes(String templateFile, String payload) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        ObjectMapper jsonMapper = new ObjectMapper();
        xmlMapper.setSerializerFactory(xmlMapper.getSerializerFactory().withSerializerModifier(new CustomBeanSerializerModifier()));
        JsonNode xmlNode = xmlMapper.readTree(payload);
        String json = jsonMapper.writeValueAsString(xmlNode);
        Template template = TemplateParser.DEFAULT.parse(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(templateFile).getFile()).getPath())));
//        Template template = new TemplateParser.Builder().build().parse(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(templateFile).getFile()).getPath())));
        return template.render(json);
    }

    static class CustomBeanSerializerModifier extends BeanSerializerModifier {

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            for (BeanPropertyWriter beanProperty : beanProperties) {
                if (beanProperty instanceof AttributePropertyWriter) {
                    beanProperty.assignSerializer(new AttributeSerializer());
                }
            }
            return beanProperties;
        }

    }

    static class AttributeSerializer extends JsonSerializer<Object> {
        public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value instanceof AttributePropertyWriter attribute) {
                gen.writeFieldName(attribute.getName());
//                gen.writeString(attribute);
            }
        }
    }
}
