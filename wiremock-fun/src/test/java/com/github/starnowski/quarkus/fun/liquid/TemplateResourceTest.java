package com.github.starnowski.quarkus.fun.liquid;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@QuarkusTest
class TemplateResourceTest {

    private WireMockServer wireMockServer;

    @Inject
    private TemplateResource templateResource;

    @BeforeEach
    public void setup(){
        wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
        wireMockServer.start();
        templateResource.setUrl(wireMockServer.baseUrl());
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    private static Stream<Arguments> provideRequestWithTemplateAndExpectedResponse() {
        return Stream.of(
                Arguments.of("req1.xml", "xml-to-json-1.liquid"),
                Arguments.of("req2.xml", "xml-to-json-1.liquid"),
                Arguments.of("req-repeated-elements-with-attributes-and-value.xml", "xml-repeated-elements-with-attributes-and-value-to-json.liquid")
        );
    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndExpectedResponse")
    public void shouldSendTestPayload(String requestFile) throws IOException {
        // GIVEN
        wireMockServer.stubFor(post(urlEqualTo("/test")).willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                { "id": 1, "userId": 1, "title": "my todo" }
                                """)
                )
        );

        String requestBody = Files.readString(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath()));

        // WHEN
        given()
                .body(requestBody)
                .when().post("/send")
                .then()
                .statusCode(200)
                .body(is("""
                                { "id": 1, "userId": 1, "title": "my todo" }
                                """));

        wireMockServer.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/test")).withRequestBody(WireMock.equalTo(requestBody)));

    }

    @ParameterizedTest
    @MethodSource("provideRequestWithTemplateAndExpectedResponse")
    public void shouldReadJsonWithBase64AndSendBinaryContent(String requestFile) throws IOException {
        // GIVEN
        wireMockServer.stubFor(post(urlEqualTo("/test")).willReturn(
                        aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                { "id": 1, "userId": 1, "title": "my todo" }
                                """)
                )
        );

        byte[] binaryContent = Files.readAllBytes(Paths.get(new File(getClass().getClassLoader().getResource(requestFile).getFile()).getPath()));
        String base64 = Base64.getEncoder().encodeToString(binaryContent);
        String requestBody = String.format(
                "{\"data\": \"%s\"}", base64
        );

        // WHEN
        given()
                .body(requestBody)
                .header("Content-Type", "application/json")
                .when().post("/sendWrapped")
                .then()
                .statusCode(200)
                .body(is("""
                                { "id": 1, "userId": 1, "title": "my todo" }
                                """));

        wireMockServer.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/test"))
                .withRequestBody(WireMock.binaryEqualTo(binaryContent)));

    }

}