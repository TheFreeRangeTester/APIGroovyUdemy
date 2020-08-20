import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.testng.annotations.BeforeClass

class Base {
    @BeforeClass
    public static void setupRestAssured() {

        RestAssured.baseURI = "http://qa-library-dev.herokuapp.com";
        RestAssured.basePath = "/api/";
        RequestSpecification requestSpecification = new RequestSpecBuilder().
                addHeader("Content-Type", ContentType.JSON.toString()).
                addHeader("Accept", ContentType.JSON.toString())
                .build();
        RestAssured.requestSpecification = requestSpecification;
    }
}
