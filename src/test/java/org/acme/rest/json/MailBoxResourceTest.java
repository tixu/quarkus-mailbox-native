package org.acme.rest.json;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


@QuarkusTest
public class MailBoxResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/mailboxes")
          .then()
             .statusCode(200);
          //   .body(is("hello"));
    }

}