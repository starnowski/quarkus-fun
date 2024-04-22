package com.github.starnowski.quarkus.fun.liquid;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TemplateResourceTest {

    private static Stream<Arguments> provideRequestWithTemplateAndExpectedResponse() {
        return Stream.of(
                Arguments.of("req1.xml", "xml-to-json-1.liquid", "expected-json-1.json"),
                Arguments.of("req2.xml", "xml-to-json-1.liquid", "expected-json-1.json")
        );
    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndExpectedResponse")
    public void shouldGenerateMapRequestBasedOnTemplate(String requestFile, String templateFile, String expectedContentFile) throws IOException {
        given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template/{templateId}",templateFile)
                .then()
                .statusCode(200)
                .body(is(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath()))));
    }
}