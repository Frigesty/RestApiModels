package ru.frigesty.tests.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.frigesty.tests.models.LoginBodyModel;
import ru.frigesty.tests.models.LoginResponseModel;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.frigesty.tests.helpers.CustomAllureListener.withCustomTemplates;

public class RequestInTests {

    @BeforeEach
    public void beforeEach() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    void correctDataInPageSingleUser() {
        given()
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemes/singleUserScheme.json"))
                .assertThat().body("data.id", is(2),
                        "data.email", is("janet.weaver@reqres.in"),
                                             "data.first_name", is("Janet"),
                                             "data.last_name", is("Weaver"),
                                             "data.avatar", is("https://reqres.in/img/faces/2-image.jpg"));
    }

    @Test
    void pageSingleUserNotFound() {
        given()
                .when()
                .get("/users/23")
                .then()
                .statusCode(404)
                .log().status()
                .log().body();
    }

    @Test
    void correctDataInPageListResource() {
        given()
                .when()
                .get("/unknown")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemes/listResourceScheme.json"))
                .body("data.id", hasItems(1, 2, 3, 4, 5, 6))
                .assertThat().body("data.id[0]", is(1),
                        "data.name[0]", is("cerulean"),
                        "data.year[0]", is(2000),
                        "data.color[0]", is("#98B2D1"),
                        "data.pantone_value[0]", is("15-4020"));
    }

    @Test
    void correctDataInPageSingleResource() {
        given()
                .when()
                .get("unknown/2")
                .then()
                .statusCode(200)
                .log().body()
                .body(matchesJsonSchemaInClasspath("schemes/singleResourceScheme.json"))
                .assertThat().body("data.id", is(2),
                        "data.name", is("fuchsia rose"),
                        "data.year", is(2001),
                        "data.color", is("#C74375"),
                        "data.pantone_value", is("17-2031"));
    }

    @Test
    void pageSingleResourceNotFound() {
        given()
                .when()
                .get("/unknown/23")
                .then()
                .statusCode(404);
    }

    @Test
    void successfulCreateTest() {

        String requestBody = "{ \"name\": \"morpheus\", \"job\": \"leader\" }";

        given()
            .log().uri()
            .log().body()
            .contentType(JSON)
            .body(requestBody)
            .when()
            .post("/users?page=2")
            .then()
            .statusCode(201)
            .body(matchesJsonSchemaInClasspath("schemes/createUserScheme.json"))
            .assertThat().body("name", is("morpheus"),
                        "job", is("leader"));
    }

    @Test
    void successfulLoginTest() {
        LoginBodyModel requestBody = new LoginBodyModel();
        requestBody.setEmail("eve.holt@reqres.in");
        requestBody.setPassword("cityslicka");

        LoginResponseModel loginResponse = given()
                .log().uri()
                .log().body()
                .filter(withCustomTemplates())
                .contentType(JSON)
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().as(LoginResponseModel.class);

        assertEquals("QpwL5tke4Pnpja7X4", loginResponse.getToken());
    }

    @Test
    void negativeLogin400Test() {
        LoginBodyModel requestBody = new LoginBodyModel();
        requestBody.setEmail("eve.holt@reqres.in");
        requestBody.setPassword("cityslicka");

        given()
                .log().uri()
                .log().body()
                .filter(withCustomTemplates())
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing email or username"));
    }

    @Test
    void negativeLoginTest() {
        given()
                .log().uri()
                .log().body()
                .when()
                .post("/login")
                .then()
                .log().status()
                .log().body()
                .statusCode(415);
    }
}