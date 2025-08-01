package tech.ada;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ContaResourceTest {

    @Test
    void criaConta() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .post("/contas")
                .then()
                .statusCode(201);
    }
}