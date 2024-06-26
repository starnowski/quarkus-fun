package com.github.starnowski.quarkus.fun.liquid;

import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import liqp.Template;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@TestProfile(TestProfiles.MockedTemplateSupplierTestProfile.class)
class TemplateResourceWithMockedComponentsTest {

    private static final String MOCKED_RESPONSE = """
            {
                "payload": {
                    "x1": 1
                }
            }
            """;


    @Inject
    private TemplateSupplier templateSupplier;


    @BeforeEach
    public void setup() {
        TemplateSupplier mock = Mockito.mock(TemplateSupplier.class);
        QuarkusMock.installMockForInstance(mock, templateSupplier);
    }

    private static Stream<Arguments> provideRequestWithTemplateAndExpectedResponse() {
        return Stream.of(
                Arguments.of("req1.xml", "xml-to-json-1.liquid", "expected-json-1.json"),
                Arguments.of("req2.xml", "xml-to-json-1.liquid", "expected-json-1.json"),
                Arguments.of("req-repeated-elements-with-attributes-and-value.xml", "xml-repeated-elements-with-attributes-and-value-to-json.liquid", "expected-repeated-elements-with-attributes-and-value.json")
        );
    }

    private static Stream<Arguments> provideRequestWithTemplateAndExpectedResponseThatJSONIsGenerallyMatches() {
        return Stream.of(
                Arguments.of("req-repeated-elements-with-attributes-and-value.xml", "xml-render-repeated-elements-with-attributes-and-value-to-json.liquid", "expected-render-repeated-elements-with-attributes-and-value.json")
        );
    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndExpectedResponse")
    public void shouldGenerateMapRequestBasedOnTemplate(String requestFile, String templateFile, String expectedContentFile) throws IOException {
        Template template = Mockito.mock(Template.class);
        Mockito.when(templateSupplier.get(templateFile)).thenReturn(template);
        Mockito.when(template.render(Mockito.anyString())).thenReturn(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath())));

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
        Template template = Mockito.mock(Template.class);
        Mockito.when(templateSupplier.get(templateFile)).thenReturn(template);
        Mockito.when(template.render(Mockito.anyString())).thenReturn(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath())));

        ExtractableResponse<Response> response = given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template/{templateId}", templateFile)
                .then()
                .statusCode(200).extract();

        org.skyscreamer.jsonassert.JSONAssert.assertEquals(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(expectedContentFile).getFile()).getPath())), response.body().jsonPath().prettyPrint(), false);
    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndExpectedResponse")
    public void shouldGenerateMapRequestBasedOnTemplateAndReturnMockedResponse(String requestFile, String templateFile, String expectedContentFile) throws IOException {
        Template template = Mockito.mock(Template.class);
        Mockito.when(templateSupplier.get(templateFile)).thenReturn(template);
        Mockito.when(template.render(Mockito.anyString())).thenReturn(MOCKED_RESPONSE);

        given()
                .body(Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath())))
                .when().post("/template/{templateId}", templateFile)
                .then()
                .statusCode(200)
                .body(is(MOCKED_RESPONSE));
    }
}