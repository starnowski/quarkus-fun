package com.github.starnowski.quarkus.fun.liquid;

import com.github.starnowski.quarkus.fun.liquid.matchers.RegexMatcher;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@QuarkusTest
class TemplateResourceRegexMatcherTest {

    private static Stream<Arguments> provideRequestWithTemplateAndExpectedResponse() {
        return Stream.of(
                Arguments.of("req1.xml", "xml-to-json-1.liquid", "expected-json-1.json"),
                Arguments.of("req2.xml", "xml-to-json-1.liquid", "expected-json-1.json"),
                Arguments.of("req-repeated-elements-with-attributes-and-value.xml", "xml-repeated-elements-with-attributes-and-value-to-json.liquid", "expected-repeated-elements-with-attributes-and-value.json"),
                Arguments.of("req1.xml", "xml-to-json-with-current-year_month.liquid", "expected-json-with-current-year_month.json"),
                Arguments.of("req1.xml", "xml-to-json-with-literal-regex-patterns.liquid", "expected-json-with-literal-regex-pattern.json")
        );
    }

    private static Stream<Arguments> provideRequestWithTemplateAndResponseThatDoNotMatch() {
        return Stream.of(
                Arguments.of("req1.xml", "xml-to-json-with-literal-regex-patterns.liquid", "expected-json-with-literal-regex-pattern_2.json")
        );
    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndExpectedResponse")
    public void shouldGenerateMapRequestBasedOnTemplate(String requestFile, String templateFile, String expectedContentFile) throws IOException {
        given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template/{templateId}", templateFile)
                .then()
                .statusCode(200).body(new RegexMatcher(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath()))));
    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndResponseThatDoNotMatch")
    public void shouldGenerateMapRequestBasedOnTemplateButNotEqualToCloseLookingPattern(String requestFile, String templateFile, String expectedContentFile) throws IOException {
        given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template/{templateId}", templateFile)
                .then()
                .statusCode(200).body(Matchers.not(Matchers.is(new RegexMatcher(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath()))))));
    }
}