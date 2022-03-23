package com.wro.CompanyManager.integration;

import com.wro.CompanyManager.CompanyManagerApplication;
import com.wro.CompanyManager.TestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CompanyManagerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmailIntegrationTest {

    @LocalServerPort
    private int port;
    private static final String CONTEXT = "companies";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void should_return_companies() throws IOException {
        given()
            .when().get("/companies")
            .then()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void should_add_new_company() throws IOException {
        given().contentType(ContentType.JSON).body(TestUtils.
                getRequestBodyFromFile("request/add-new-company-request.json", CONTEXT))
                .when().post("/companies")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void should_add_new_company_and_compare() throws IOException {
        given().contentType(ContentType.JSON)
                .body(TestUtils.getRequestBodyFromFile("request/add-new-company-request.json", CONTEXT))
                .when().post("/companies")
                .then()
                .and().body("name", is("TestCompany"))
                .and().body("website", is("test-company.com"));
    }

    @Test
    public void should_add_new_department_and_return_404() throws IOException {
        var companyId = "ab391196-93dd-11ec-b909-0242ac120002";
        given().contentType(ContentType.JSON)
                .body(TestUtils.getRequestBodyFromFile("request/add-new-department-request.json", CONTEXT))
                .when().patch( String.format("/companies/%s", companyId))
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

}
