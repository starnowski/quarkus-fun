package com.github.starnowski.quarkus.fun.liquid;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
class TemplateResourceTest {

    private static Stream<Arguments> provideRequestWithTemplateAndExpectedResponse() {
        return Stream.of(
                Arguments.of("req1.xml", "xml-to-json-1.liquid", "expected-json-1.json"),
                Arguments.of("req2.xml", "xml-to-json-1.liquid", "expected-json-1.json"),
                Arguments.of("req-repeated-elements-with-attributes-and-value.xml", "xml-repeated-elements-with-attributes-and-value-to-json.liquid", "expected-repeated-elements-with-attributes-and-value.json")
        );
    }

    private static Stream<Arguments> provideRequestWithTemplateThatHasAttributesAndExpectedResponse() {
        return Stream.of(
                Arguments.of("req-with-attributes.xml", "xml-to-json-2.liquid", "expected-json-2.json")
        );
    }

    private static Stream<Arguments> provideRequestWithTemplateAndExpectedResponseThatJSONIsGenerallyMatches() {
        return Stream.of(
                Arguments.of("req-repeated-elements-with-attributes-and-value.xml", "xml-render-repeated-elements-with-attributes-and-value-to-json.liquid", "expected-render-repeated-elements-with-attributes-and-value.json")
        );
    }

    private static Stream<Arguments> provideRequestWithTemplateThatUseCustomFiltersAndExpectedResponse() {
        return Stream.of(
                Arguments.of("customFilters/req-without-name-tag.xml", "customFilters/xml-to-json-with-custom-filters-1.liquid", "customFilters/expected-json-with-null-name.json"),
                Arguments.of("req1.xml", "customFilters/xml-to-json-with-custom-filters-1.liquid", "expected-json-1.json")
        );
    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndExpectedResponse")
    public void shouldGenerateMapRequestBasedOnTemplate(String requestFile, String templateFile, String expectedContentFile) throws IOException {
        given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template/{templateId}", templateFile)
                .then()
                .statusCode(200)
                .body(is(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath()))));
    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndExpectedResponseThatJSONIsGenerallyMatches")
    public void shouldGenerateMapRequestBasedOnTemplateAndJsonShouldGenerallyMatch(String requestFile, String templateFile, String expectedContentFile) throws IOException, JSONException {
        ExtractableResponse<Response> response = given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template/{templateId}", templateFile)
                .then()
                .statusCode(200).extract();

        org.skyscreamer.jsonassert.JSONAssert.assertEquals(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath())), response.body().jsonPath().prettyPrint(), false);
    }

    @ParameterizedTest
    @MethodSource({"provideRequestWithTemplateAndExpectedResponse", "provideRequestWithTemplateThatUseCustomFiltersAndExpectedResponse"})
    public void shouldGenerateMapRequestBasedOnTemplateWithCustomFilters(String requestFile, String templateFile, String expectedContentFile) throws IOException {
        given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template-with-custom-filters/{templateId}", templateFile)
                .then()
                .statusCode(200)
                .body(is(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath()))));
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateThatHasAttributesAndExpectedResponse")
    public void shouldGenerateMapRequestBasedOnTemplateWithAttributes(String requestFile, String templateFile, String expectedContentFile) throws IOException {
        given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template-with-attributes/{templateId}", templateFile)
                .then()
                .statusCode(200)
                .body(is(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath()))));
    }
}