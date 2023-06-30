package ru.frigesty.tests.tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import ru.frigesty.tests.models.GetListUsersModel;
import ru.frigesty.tests.models.UserModel;

import java.util.HashSet;
import java.util.LinkedList;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.frigesty.tests.specs.LoginSpecs.loginRequestSpecBase;
import static ru.frigesty.tests.specs.LoginSpecs.loginResponseSpec;

public class GetListUsersTest {

    @Test
    void correctDataInPageListUsers() {
        RestAssured.filters(new AllureRestAssured());
        Integer[] userId = {7, 8, 9, 10, 11, 12};

        GetListUsersModel response = step("Make request", () ->
                given()
                        .spec(loginRequestSpecBase)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .spec(loginResponseSpec)
                        .extract().as(GetListUsersModel.class));

        HashSet<Integer> idFromResponse = new HashSet<>();

        LinkedList<UserModel> usersFromResponse = response.getData();

        for (UserModel user : usersFromResponse) {
            idFromResponse.add(user.getId());
        }

        step("Check response", () -> {
            for (int id : userId) {
                assertTrue(idFromResponse.contains(id));
            }
            assertEquals(idFromResponse.size(), userId.length);
        });
    }
}